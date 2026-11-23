package dao;

import model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Empleado.
 * Gestiona el personal del salón, especialidades y porcentajes de comisión.
 */
public class EmpleadoDAO {

    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, especialidad, porcentaje_comision, username, activo FROM empleadas ORDER BY nombre ASC";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Empleado(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("especialidad"),
                    rs.getDouble("porcentaje_comision"),
                    rs.getString("username"),
                    rs.getBoolean("activo")
                ));
            }
        }
        return lista;
    }

    public List<Empleado> listarActivas() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, especialidad, porcentaje_comision, username, activo FROM empleadas WHERE activo = 1 ORDER BY nombre ASC";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Empleado(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("especialidad"),
                    rs.getDouble("porcentaje_comision"),
                    rs.getString("username"),
                    rs.getBoolean("activo")
                ));
            }
        }
        return lista;
    }

    public Empleado buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, telefono, especialidad, porcentaje_comision, username, activo FROM empleadas WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("especialidad"),
                        rs.getDouble("porcentaje_comision"),
                        rs.getString("username"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }
        return null;
    }

    public Empleado buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT id, nombre, telefono, especialidad, porcentaje_comision, username, activo FROM empleadas WHERE username = ? LIMIT 1";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("especialidad"),
                        rs.getDouble("porcentaje_comision"),
                        rs.getString("username"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }
        return null;
    }

    public boolean guardar(Empleado e) throws SQLException {
        String sql = "INSERT INTO empleadas (nombre, telefono, especialidad, porcentaje_comision, username, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTelefono());
            ps.setString(3, e.getEspecialidad());
            ps.setDouble(4, e.getPorcentajeComision());
            ps.setString(5, e.getUsername());
            ps.setInt(6, e.isActivo() ? 1 : 0);
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
        String sql = "UPDATE empleadas SET nombre = ?, telefono = ?, especialidad = ?, porcentaje_comision = ?, username = ?, activo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTelefono());
            ps.setString(3, e.getEspecialidad());
            ps.setDouble(4, e.getPorcentajeComision());
            ps.setString(5, e.getUsername());
            ps.setInt(6, e.isActivo() ? 1 : 0);
            ps.setInt(7, e.getId());
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
}
