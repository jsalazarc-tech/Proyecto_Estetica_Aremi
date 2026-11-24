package dao;

import model.Usuario;
import java.sql.*;

/**
 * Data Access Object (DAO) para Autenticación y Cuentas de Usuario.
 */
public class UsuarioDAO {

    public Usuario autenticar(String username, String passwordHash) throws SQLException {
        String sql = "SELECT id, username, password, nombre_completo, rol FROM usuarios WHERE LOWER(username) = ? AND password = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nombre_completo"),
                        rs.getString("rol")
                    );
                }
            }
        }
        return null;
    }

    public boolean existeUsuario(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE LOWER(username) = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public String obtenerRol(String username) throws SQLException {
        String sql = "SELECT rol FROM usuarios WHERE LOWER(username) = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("rol");
                }
            }
        }
        return "empleada";
    }

    public boolean crearUsuario(String username, String passwordHash, String nombreCompleto, String rol) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, passwordHash);
            ps.setString(3, nombreCompleto.trim());
            ps.setString(4, (rol != null && !rol.trim().isEmpty()) ? rol.trim().toLowerCase() : "empleada");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarUsuario(String username, String passwordHash, String nombreCompleto) throws SQLException {
        return actualizarUsuario(username, passwordHash, nombreCompleto, null);
    }

    public boolean actualizarUsuario(String username, String passwordHash, String nombreCompleto, String rol) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE usuarios SET nombre_completo = ?");
        boolean tienePass = (passwordHash != null && !passwordHash.trim().isEmpty());
        boolean tieneRol = (rol != null && !rol.trim().isEmpty());

        if (tienePass) sql.append(", password = ?");
        if (tieneRol) sql.append(", rol = ?");
        sql.append(" WHERE LOWER(username) = ?");

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, nombreCompleto.trim());
            if (tienePass) ps.setString(idx++, passwordHash.trim());
            if (tieneRol) ps.setString(idx++, rol.trim().toLowerCase());
            ps.setString(idx++, username.trim().toLowerCase());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarUsuario(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) return false;
        String sql = "DELETE FROM usuarios WHERE LOWER(username) = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            return ps.executeUpdate() > 0;
        }
    }
}
