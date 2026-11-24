package dao;

import model.ServicioRealizado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para Servicios Realizados.
 * Registra servicios prestados y los mantiene listos para su posterior liquidación de comisiones.
 */
public class ServicioRealizadoDAO {

    public boolean registrar(ServicioRealizado sr) throws SQLException {
        String sql = "INSERT INTO servicios_realizados (id_cita, id_empleada, nombre_empleada, id_servicio, " +
                     "nombre_servicio, id_cliente, nombre_cliente, fecha, valor_cobrado, porcentaje_comision, " +
                     "comision_calculada, estado_liquidacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (sr.getIdCita() != null) ps.setInt(1, sr.getIdCita()); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, sr.getIdEmpleada());
            ps.setString(3, sr.getNombreEmpleada());
            ps.setInt(4, sr.getIdServicio());
            ps.setString(5, sr.getNombreServicio());
            if (sr.getIdCliente() != null) ps.setInt(6, sr.getIdCliente()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, sr.getNombreCliente());
            ps.setDate(8, sr.getFecha());
            ps.setDouble(9, sr.getValorCobrado());
            ps.setDouble(10, sr.getPorcentajeComision());
            ps.setDouble(11, sr.getComisionCalculada());
            ps.setString(12, sr.getEstadoLiquidacion() != null ? sr.getEstadoLiquidacion() : "Pendiente");

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) sr.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<ServicioRealizado> listarTodos() throws SQLException {
        List<ServicioRealizado> lista = new ArrayList<>();
        String sql = "SELECT id, id_cita, id_empleada, nombre_empleada, id_servicio, nombre_servicio, " +
                     "id_cliente, nombre_cliente, fecha, valor_cobrado, porcentaje_comision, " +
                     "comision_calculada, estado_liquidacion, id_liquidacion " +
                     "FROM servicios_realizados ORDER BY fecha DESC, id DESC";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ServicioRealizado sr = new ServicioRealizado(
                    (Integer) rs.getObject("id_cita"),
                    rs.getInt("id_empleada"),
                    rs.getString("nombre_empleada"),
                    rs.getInt("id_servicio"),
                    rs.getString("nombre_servicio"),
                    (Integer) rs.getObject("id_cliente"),
                    rs.getString("nombre_cliente"),
                    rs.getDate("fecha"),
                    rs.getDouble("valor_cobrado"),
                    rs.getDouble("porcentaje_comision")
                );
                sr.setId(rs.getInt("id"));
                sr.setComisionCalculada(rs.getDouble("comision_calculada"));
                sr.setEstadoLiquidacion(rs.getString("estado_liquidacion"));
                sr.setIdLiquidacion((Integer) rs.getObject("id_liquidacion"));
                lista.add(sr);
            }
        }
        return lista;
    }

    public List<ServicioRealizado> listarPorEmpleada(int idEmpleada) throws SQLException {
        List<ServicioRealizado> lista = new ArrayList<>();
        String sql = "SELECT id, id_cita, id_empleada, nombre_empleada, id_servicio, nombre_servicio, " +
                     "id_cliente, nombre_cliente, fecha, valor_cobrado, porcentaje_comision, " +
                     "comision_calculada, estado_liquidacion, id_liquidacion " +
                     "FROM servicios_realizados WHERE id_empleada = ? ORDER BY fecha DESC, id DESC";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleada);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServicioRealizado sr = new ServicioRealizado(
                        (Integer) rs.getObject("id_cita"),
                        rs.getInt("id_empleada"),
                        rs.getString("nombre_empleada"),
                        rs.getInt("id_servicio"),
                        rs.getString("nombre_servicio"),
                        (Integer) rs.getObject("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getDate("fecha"),
                        rs.getDouble("valor_cobrado"),
                        rs.getDouble("porcentaje_comision")
                    );
                    sr.setId(rs.getInt("id"));
                    sr.setComisionCalculada(rs.getDouble("comision_calculada"));
                    sr.setEstadoLiquidacion(rs.getString("estado_liquidacion"));
                    sr.setIdLiquidacion((Integer) rs.getObject("id_liquidacion"));
                    lista.add(sr);
                }
            }
        }
        return lista;
    }

    public List<ServicioRealizado> listarPendientesPorEmpleadaYFechas(int idEmpleada, Date fechaInicio, Date fechaFin) throws SQLException {
        List<ServicioRealizado> lista = new ArrayList<>();
        String sql = "SELECT id, id_cita, id_empleada, nombre_empleada, id_servicio, nombre_servicio, " +
                     "id_cliente, nombre_cliente, fecha, valor_cobrado, porcentaje_comision, " +
                     "comision_calculada, estado_liquidacion, id_liquidacion " +
                     "FROM servicios_realizados " +
                     "WHERE id_empleada = ? AND estado_liquidacion = 'Pendiente' AND fecha >= ? AND fecha <= ? " +
                     "ORDER BY fecha ASC";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEmpleada);
            ps.setDate(2, fechaInicio);
            ps.setDate(3, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServicioRealizado sr = new ServicioRealizado(
                        (Integer) rs.getObject("id_cita"),
                        rs.getInt("id_empleada"),
                        rs.getString("nombre_empleada"),
                        rs.getInt("id_servicio"),
                        rs.getString("nombre_servicio"),
                        (Integer) rs.getObject("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getDate("fecha"),
                        rs.getDouble("valor_cobrado"),
                        rs.getDouble("porcentaje_comision")
                    );
                    sr.setId(rs.getInt("id"));
                    sr.setComisionCalculada(rs.getDouble("comision_calculada"));
                    sr.setEstadoLiquidacion(rs.getString("estado_liquidacion"));
                    sr.setIdLiquidacion((Integer) rs.getObject("id_liquidacion"));
                    lista.add(sr);
                }
            }
        }
        return lista;
    }

    public boolean marcarComoLiquidados(List<Integer> idsServicios, int idLiquidacion, Connection conn) throws SQLException {
        if (idsServicios == null || idsServicios.isEmpty()) return true;
        String sql = "UPDATE servicios_realizados SET estado_liquidacion = 'Liquidado', id_liquidacion = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer idServ : idsServicios) {
                ps.setInt(1, idLiquidacion);
                ps.setInt(2, idServ);
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        }
    }
}
