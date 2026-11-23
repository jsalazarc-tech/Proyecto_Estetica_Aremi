package dao;

import model.LiquidacionComision;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para Liquidaciones de Comisiones.
 * Ejecuta el registro transaccional que agrupa los servicios y crea el comprobante de liquidación.
 */
public class LiquidacionDAO {

    public int registrarLiquidacionTransaccional(LiquidacionComision lc, List<Integer> idsServicios) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.getInstancia().getConexion();
            conn.setAutoCommit(false); // Transacción atómica

            String sql = "INSERT INTO liquidaciones (id_empleada, nombre_empleada, fecha_inicio, fecha_fin, " +
                         "total_servicios, total_ventas, porcentaje_aplicado, total_comision, total_salon, notas) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int idLiquidacionGenerado = -1;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, lc.getIdEmpleada());
                ps.setString(2, lc.getNombreEmpleada());
                ps.setDate(3, lc.getFechaInicio());
                ps.setDate(4, lc.getFechaFin());
                ps.setInt(5, lc.getTotalServicios());
                ps.setDouble(6, lc.getTotalVentas());
                ps.setDouble(7, lc.getPorcentajeAplicado());
                ps.setDouble(8, lc.getTotalComision());
                ps.setDouble(9, lc.getTotalSalon());
                ps.setString(10, lc.getNotas() != null ? lc.getNotas() : "");
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        idLiquidacionGenerado = keys.getInt(1);
                        lc.setId(idLiquidacionGenerado);
                    }
                }
            }

            if (idLiquidacionGenerado > 0 && idsServicios != null && !idsServicios.isEmpty()) {
                ServicioRealizadoDAO srDAO = new ServicioRealizadoDAO();
                srDAO.marcarComoLiquidados(idsServicios, idLiquidacionGenerado, conn);
            }

            conn.commit(); // Confirmación de transacción
            return idLiquidacionGenerado;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public List<LiquidacionComision> listarHistorico() throws SQLException {
        List<LiquidacionComision> lista = new ArrayList<>();
        String sql = "SELECT id, id_empleada, nombre_empleada, fecha_inicio, fecha_fin, total_servicios, " +
                     "total_ventas, porcentaje_aplicado, total_comision, total_salon, fecha_registro, notas " +
                     "FROM liquidaciones ORDER BY id DESC";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LiquidacionComision lc = new LiquidacionComision(
                    rs.getInt("id_empleada"),
                    rs.getString("nombre_empleada"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getInt("total_servicios"),
                    rs.getDouble("total_ventas"),
                    rs.getDouble("porcentaje_aplicado"),
                    rs.getDouble("total_comision"),
                    rs.getDouble("total_salon")
                );
                lc.setId(rs.getInt("id"));
                lc.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                lc.setNotas(rs.getString("notas"));
                lista.add(lc);
            }
        }
        return lista;
    }

    public LiquidacionComision buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM liquidaciones WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LiquidacionComision lc = new LiquidacionComision(
                        rs.getInt("id_empleada"),
                        rs.getString("nombre_empleada"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getInt("total_servicios"),
                        rs.getDouble("total_ventas"),
                        rs.getDouble("porcentaje_aplicado"),
                        rs.getDouble("total_comision"),
                        rs.getDouble("total_salon")
                    );
                    lc.setId(rs.getInt("id"));
                    lc.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                    lc.setNotas(rs.getString("notas"));
                    return lc;
                }
            }
        }
        return null;
    }
}
