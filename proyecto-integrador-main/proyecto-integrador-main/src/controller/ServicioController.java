package controller;

import dao.ServicioDAO;
import model.Servicio;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para el Catálogo de Servicios y Tarifas.
 */
public class ServicioController {
    private final ServicioDAO servicioDAO;

    public ServicioController() {
        this.servicioDAO = new ServicioDAO();
    }

    public List<Servicio> listarServicios(String filtro) throws SQLException {
        return servicioDAO.listar(filtro);
    }

    public List<Servicio> listarActivos() throws SQLException {
        return servicioDAO.listarActivos();
    }

    public Servicio obtenerPorId(int id) throws SQLException {
        return servicioDAO.buscarPorId(id);
    }

    public void registrarServicio(String nombre, String categoria, double precio, int duracion, String descripcion) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio del servicio debe ser mayor a 0.");
        }
        if (duracion <= 0) duracion = 30;

        Servicio s = new Servicio(0, nombre.trim(), descripcion != null ? descripcion.trim() : "",
                categoria != null ? categoria.trim() : "General", precio, duracion, true);

        boolean exito = servicioDAO.guardar(s);
        if (!exito) {
            throw new SQLException("No se pudo guardar el servicio en el catálogo.");
        }
    }

    public void actualizarServicio(int id, String nombre, String categoria, double precio, int duracion, String descripcion, boolean activo) throws Exception {
        if (id <= 0) throw new IllegalArgumentException("ID de servicio inválido.");
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio del servicio debe ser mayor a 0.");
        }

        Servicio s = new Servicio(id, nombre.trim(), descripcion != null ? descripcion.trim() : "",
                categoria != null ? categoria.trim() : "General", precio, duracion, activo);

        boolean exito = servicioDAO.actualizar(s);
        if (!exito) {
            throw new SQLException("No se pudo actualizar el servicio.");
        }
    }

    public void eliminarServicio(int id) throws Exception {
        if (id <= 0) throw new IllegalArgumentException("ID inválido.");
        boolean exito = servicioDAO.eliminar(id);
        if (!exito) {
            throw new SQLException("No se pudo desactivar el servicio.");
        }
    }
}
