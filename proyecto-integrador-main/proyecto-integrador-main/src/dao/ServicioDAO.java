package dao;

import model.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para el catálogo de Servicios.
 */
public class ServicioDAO {

    public List<Servicio> listar(String filtro) throws SQLException {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, categoria, precio, duracion_minutos, activo FROM servicios";
        boolean tieneFiltro = filtro != null && !filtro.trim().isEmpty();
        if (tieneFiltro) {
            sql += " WHERE LOWER(nombre) LIKE ? OR LOWER(categoria) LIKE ?";
        }
        sql += " ORDER BY categoria ASC, nombre ASC";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (tieneFiltro) {
                String term = "%" + filtro.trim().toLowerCase() + "%";
                ps.setString(1, term);
                ps.setString(2, term);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Servicio(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getInt("duracion_minutos"),
                        rs.getBoolean("activo")
                    ));
                }
            }
        }
        return lista;
    }

    public List<Servicio> listarActivos() throws SQLException {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, categoria, precio, duracion_minutos, activo FROM servicios WHERE activo = 1 ORDER BY categoria ASC, nombre ASC";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Servicio(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getString("categoria"),
                    rs.getDouble("precio"),
                    rs.getInt("duracion_minutos"),
                    rs.getBoolean("activo")
                ));
            }
        }
        return lista;
    }

    public Servicio buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, categoria, precio, duracion_minutos, activo FROM servicios WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Servicio(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"),
                        rs.getInt("duracion_minutos"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }
        return null;
    }

    public boolean guardar(Servicio s) throws SQLException {
        String sql = "INSERT INTO servicios (nombre, descripcion, categoria, precio, duracion_minutos, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setString(3, s.getCategoria());
            ps.setDouble(4, s.getPrecio());
            ps.setInt(5, s.getDuracionMinutos());
            ps.setInt(6, s.isActivo() ? 1 : 0);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) s.setId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean actualizar(Servicio s) throws SQLException {
        String sql = "UPDATE servicios SET nombre = ?, descripcion = ?, categoria = ?, precio = ?, duracion_minutos = ?, activo = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setString(3, s.getCategoria());
            ps.setDouble(4, s.getPrecio());
            ps.setInt(5, s.getDuracionMinutos());
            ps.setInt(6, s.isActivo() ? 1 : 0);
            ps.setInt(7, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE servicios SET activo = 0 WHERE id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
