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
}
