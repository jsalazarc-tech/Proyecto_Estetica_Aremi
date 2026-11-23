package dao;

import model.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la gestión de Citas.
 * Contiene la verificación crítica de cruces de horario para empleadas (Requerimiento RF-06).
 */
public class CitaDAO {

    public List<Cita> listar(String filtroEstado, String filtroEmpleada) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT id, id_cliente, nombre_cliente, telefono_cliente, id_servicio, nombre_servicio, " +
            "id_empleada, nombre_empleada, fecha, hora, estado, notas FROM citas WHERE 1=1"
        );

        boolean hayFiltroEstado = filtroEstado != null && !filtroEstado.equalsIgnoreCase("Todos");
        boolean hayFiltroEmp = filtroEmpleada != null && !filtroEmpleada.equalsIgnoreCase("Todas");

        if (hayFiltroEstado) sql.append(" AND estado = ?");
        if (hayFiltroEmp) sql.append(" AND nombre_empleada = ?");
        sql.append(" ORDER BY fecha DESC, hora ASC");

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (hayFiltroEstado) ps.setString(idx++, filtroEstado);
            if (hayFiltroEmp) ps.setString(idx++, filtroEmpleada);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cita(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getString("telefono_cliente"),
                        (Integer) rs.getObject("id_servicio"),
                        rs.getString("nombre_servicio"),
                        (Integer) rs.getObject("id_empleada"),
                        rs.getString("nombre_empleada"),
                        rs.getDate("fecha"),
                        rs.getString("hora"),
                        rs.getString("estado"),
                        rs.getString("notas")
                    ));
                }
            }
        }
        return lista;
    }

    /**
     * Valida si la empleada ya tiene una cita agendada en la misma fecha y hora
     * que se encuentre en estado diferente a 'Cancelada'.
     */
    public boolean existeCruceHorario(int idEmpleada, Date fecha, String hora, Integer idCitaExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_empleada = ? AND fecha = ? AND hora = ? AND estado != 'Cancelada'";
        if (idCitaExcluir != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleada);
            ps.setDate(2, fecha);
            ps.setString(3, hora);
            if (idCitaExcluir != null) {
                ps.setInt(4, idCitaExcluir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean agendar(Cita c) throws SQLException {
        String sql = "INSERT INTO citas (id_cliente, nombre_cliente, telefono_cliente, id_servicio, " +
                     "nombre_servicio, id_empleada, nombre_empleada, fecha, hora, estado, notas) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (c.getIdCliente() != null) ps.setInt(1, c.getIdCliente()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, c.getNombreCliente());
            ps.setString(3, c.getTelefonoCliente());
            if (c.getIdServicio() != null) ps.setInt(4, c.getIdServicio()); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, c.getNombreServicio());
            if (c.getIdEmpleada() != null) ps.setInt(6, c.getIdEmpleada()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, c.getNombreEmpleada());
            ps.setDate(8, c.getFecha());
            ps.setString(9, c.getHora());
            ps.setString(10, c.getEstado() != null ? c.getEstado() : "Pendiente");
            ps.setString(11, c.getNotas() != null ? c.getNotas() : "");

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) c.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean actualizarEstado(int idCita, String nuevoEstado) throws SQLException {
        String sql = "UPDATE citas SET estado = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;
        }
    }

    public Cita buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM citas WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cita(
                        rs.getInt("id"),
                        (Integer) rs.getObject("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getString("telefono_cliente"),
                        (Integer) rs.getObject("id_servicio"),
                        rs.getString("nombre_servicio"),
                        (Integer) rs.getObject("id_empleada"),
                        rs.getString("nombre_empleada"),
                        rs.getDate("fecha"),
                        rs.getString("hora"),
                        rs.getString("estado"),
                        rs.getString("notas")
                    );
                }
            }
        }
        return null;
    }
}
