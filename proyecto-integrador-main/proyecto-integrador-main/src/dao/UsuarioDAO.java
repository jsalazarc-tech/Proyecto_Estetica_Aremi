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

    public boolean crearUsuario(String username, String passwordHash, String nombreCompleto, String rol) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, passwordHash);
            ps.setString(3, nombreCompleto.trim());
            ps.setString(4, rol != null ? rol.trim().toLowerCase() : "empleada");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarUsuario(String username, String passwordHash, String nombreCompleto) throws SQLException {
        if (passwordHash != null && !passwordHash.trim().isEmpty()) {
            String sql = "UPDATE usuarios SET password = ?, nombre_completo = ? WHERE LOWER(username) = ?";
            try (Connection conn = ConexionBD.getInstancia().getConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, passwordHash);
                ps.setString(2, nombreCompleto.trim());
                ps.setString(3, username.trim().toLowerCase());
                return ps.executeUpdate() > 0;
            }
        } else {
            String sql = "UPDATE usuarios SET nombre_completo = ? WHERE LOWER(username) = ?";
            try (Connection conn = ConexionBD.getInstancia().getConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nombreCompleto.trim());
                ps.setString(2, username.trim().toLowerCase());
                return ps.executeUpdate() > 0;
            }
        }
    }
}
