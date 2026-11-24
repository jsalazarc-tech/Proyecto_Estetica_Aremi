package dao;

import model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Empleado.
 * Gestiona el personal del salón, especialidades, comisiones, datos de contacto (dirección, email)
 * y sincronización de roles de acceso.
 */
public class EmpleadoDAO {

    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.telefono, e.direccion, e.email, e.especialidad, e.porcentaje_comision, e.username, e.activo, " +
                     "COALESCE(u.rol, 'empleada') AS rol " +
                     "FROM empleadas e " +
                     "LEFT JOIN usuarios u ON LOWER(e.username) = LOWER(u.username) " +
                     "ORDER BY e.nombre ASC";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }
        }
        return lista;
    }

    public List<Empleado> listarActivas() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.telefono, e.direccion, e.email, e.especialidad, e.porcentaje_comision, e.username, e.activo, " +
                     "COALESCE(u.rol, 'empleada') AS rol " +
                     "FROM empleadas e " +
                     "LEFT JOIN usuarios u ON LOWER(e.username) = LOWER(u.username) " +
                     "WHERE e.activo = 1 " +
                     "ORDER BY e.nombre ASC";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }
        }
        return lista;
    }

    public Empleado buscarPorId(int id) throws SQLException {
        String sql = "SELECT e.id, e.nombre, e.telefono, e.direccion, e.email, e.especialidad, e.porcentaje_comision, e.username, e.activo, " +
                     "COALESCE(u.rol, 'empleada') AS rol " +
                     "FROM empleadas e " +
                     "LEFT JOIN usuarios u ON LOWER(e.username) = LOWER(u.username) " +
                     "WHERE e.id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }
        }
        return null;
    }

    public Empleado buscarPorUsername(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) return null;
        String sql = "SELECT e.id, e.nombre, e.telefono, e.direccion, e.email, e.especialidad, e.porcentaje_comision, e.username, e.activo, " +
                     "COALESCE(u.rol, 'empleada') AS rol " +
                     "FROM empleadas e " +
                     "LEFT JOIN usuarios u ON LOWER(e.username) = LOWER(u.username) " +
                     "WHERE LOWER(e.username) = ? LIMIT 1";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }
        }
        return null;
    }

    public boolean guardar(Empleado e) throws SQLException {
        String sql = "INSERT INTO empleadas (nombre, telefono, direccion, email, especialidad, porcentaje_comision, username, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTelefono());
            ps.setString(3, e.getDireccion() != null ? e.getDireccion() : "");
            ps.setString(4, e.getEmail() != null ? e.getEmail() : "");
            ps.setString(5, e.getEspecialidad());
            ps.setDouble(6, e.getPorcentajeComision());
            ps.setString(7, e.getUsername());
            ps.setInt(8, e.isActivo() ? 1 : 0);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) e.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean actualizar(Empleado e) throws SQLException {
        String sql = "UPDATE empleadas SET nombre = ?, telefono = ?, direccion = ?, email = ?, especialidad = ?, " +
                     "porcentaje_comision = ?, username = ?, activo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTelefono());
            ps.setString(3, e.getDireccion() != null ? e.getDireccion() : "");
            ps.setString(4, e.getEmail() != null ? e.getEmail() : "");
            ps.setString(5, e.getEspecialidad());
            ps.setDouble(6, e.getPorcentajeComision());
            ps.setString(7, e.getUsername());
            ps.setInt(8, e.isActivo() ? 1 : 0);
            ps.setInt(9, e.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstadoActivo(int id, boolean activo) throws SQLException {
        String sql = "UPDATE empleadas SET activo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activo ? 1 : 0);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        try (Connection conn = ConexionBD.getInstancia().getConexion()) {
            // Desvincular de citas si existen
            try (PreparedStatement ps = conn.prepareStatement("UPDATE citas SET id_empleada = NULL WHERE id_empleada = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            } catch (SQLException ignored) {}

            // Desvincular de servicios realizados
            try (PreparedStatement ps = conn.prepareStatement("UPDATE servicios_realizados SET id_empleada = 0 WHERE id_empleada = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            } catch (SQLException ignored) {}

            // Eliminar empleado de la tabla
            String sql = "DELETE FROM empleadas WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        }
    }

    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        String dir = "";
        String em = "";
        String rol = "empleada";

        try { dir = rs.getString("direccion"); } catch (SQLException ignored) {}
        try { em = rs.getString("email"); } catch (SQLException ignored) {}
        try { rol = rs.getString("rol"); } catch (SQLException ignored) {}

        return new Empleado(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("telefono"),
            rs.getString("especialidad"),
            rs.getDouble("porcentaje_comision"),
            rs.getString("username"),
            rs.getBoolean("activo"),
            dir != null ? dir : "",
            em != null ? em : "",
            rol != null ? rol : "empleada"
        );
    }
}
