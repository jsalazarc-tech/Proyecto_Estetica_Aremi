package controller;

import dao.EmpleadoDAO;
import dao.UsuarioDAO;
import model.Empleado;
import util.SeguridadManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la gestión de Empleadas, Porcentajes de Comisión,
 * datos de contacto (dirección, email) y asignación de rol (administrador / empleada).
 */
public class EmpleadoController {
    private final EmpleadoDAO empleadoDAO;
    private final UsuarioDAO usuarioDAO;

    public EmpleadoController() {
        this.empleadoDAO = new EmpleadoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Empleado> listarTodas() throws SQLException {
        return empleadoDAO.listarTodos();
    }

    public List<Empleado> listarActivas() throws SQLException {
        return empleadoDAO.listarActivas();
    }

    public Empleado obtenerPorId(int id) throws SQLException {
        return empleadoDAO.buscarPorId(id);
    }

    public Empleado obtenerPorUsername(String username) throws SQLException {
        return empleadoDAO.buscarPorUsername(username);
    }

    public void registrarEmpleado(String nombre, String telefono, String especialidad, double comision, String username, boolean activo) throws Exception {
        registrarEmpleado(nombre, telefono, "", "", especialidad, comision, username, "", "empleada", activo);
    }

    public void registrarEmpleado(String nombre, String telefono, String especialidad, double comision, String username, String password, boolean activo) throws Exception {
        registrarEmpleado(nombre, telefono, "", "", especialidad, comision, username, password, "empleada", activo);
    }

    public void registrarEmpleado(String nombre, String telefono, String direccion, String email, String especialidad, double comision, String username, String password, String rol, boolean activo) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empleada es obligatorio.");
        }
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
        if (comision < 0 || comision > 100) {
            throw new IllegalArgumentException("El porcentaje de comisión debe estar entre 0% y 100%.");
        }

        String userTrim = username != null ? username.trim() : "";
        String rolFinal = (rol != null && !rol.trim().isEmpty()) ? rol.toLowerCase().trim() : "empleada";

        Empleado emp = new Empleado(
            0,
            nombre.trim(),
            telefono != null ? telefono.trim() : "",
            especialidad.trim(),
            comision,
            userTrim,
            activo,
            direccion != null ? direccion.trim() : "",
            email != null ? email.trim() : "",
            rolFinal
        );

        boolean exito = empleadoDAO.guardar(emp);
        if (!exito) {
            throw new SQLException("No se pudo registrar la empleada en la base de datos.");
        }

        if (!userTrim.isEmpty()) {
            String passFinal = (password != null && !password.trim().isEmpty()) ? password.trim() : userTrim + "123";
            String hash = SeguridadManager.hashPassword(passFinal);
            if (!usuarioDAO.existeUsuario(userTrim)) {
                usuarioDAO.crearUsuario(userTrim, hash, nombre.trim(), rolFinal);
            } else {
                usuarioDAO.actualizarUsuario(userTrim, hash, nombre.trim(), rolFinal);
            }
        }
    }

    public void actualizarEmpleado(int id, String nombre, String telefono, String especialidad, double comision, String username, boolean activo) throws Exception {
        actualizarEmpleado(id, nombre, telefono, "", "", especialidad, comision, username, "", "empleada", activo);
    }

    public void actualizarEmpleado(int id, String nombre, String telefono, String especialidad, double comision, String username, String password, boolean activo) throws Exception {
        actualizarEmpleado(id, nombre, telefono, "", "", especialidad, comision, username, password, "empleada", activo);
    }

    public void actualizarEmpleado(int id, String nombre, String telefono, String direccion, String email, String especialidad, double comision, String username, String password, String rol, boolean activo) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de empleada inválido.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empleada es obligatorio.");
        }
        if (comision < 0 || comision > 100) {
            throw new IllegalArgumentException("El porcentaje de comisión debe estar entre 0% y 100%.");
        }

        String userTrim = username != null ? username.trim() : "";
        String rolFinal = (rol != null && !rol.trim().isEmpty()) ? rol.toLowerCase().trim() : "empleada";

        Empleado emp = new Empleado(
            id,
            nombre.trim(),
            telefono != null ? telefono.trim() : "",
            especialidad.trim(),
            comision,
            userTrim,
            activo,
            direccion != null ? direccion.trim() : "",
            email != null ? email.trim() : "",
            rolFinal
        );

        boolean exito = empleadoDAO.actualizar(emp);
        if (!exito) {
            throw new SQLException("No se pudo actualizar la empleada.");
        }

        if (!userTrim.isEmpty()) {
            String hash = (password != null && !password.trim().isEmpty()) ? SeguridadManager.hashPassword(password.trim()) : null;
            if (usuarioDAO.existeUsuario(userTrim)) {
                usuarioDAO.actualizarUsuario(userTrim, hash, nombre.trim(), rolFinal);
            } else {
                String passFinal = (password != null && !password.trim().isEmpty()) ? password.trim() : userTrim + "123";
                usuarioDAO.crearUsuario(userTrim, SeguridadManager.hashPassword(passFinal), nombre.trim(), rolFinal);
            }
        }
    }

    public void desactivarEmpleado(int id) throws Exception {
        if (id <= 0) throw new IllegalArgumentException("ID inválido.");
        boolean exito = empleadoDAO.cambiarEstadoActivo(id, false);
        if (!exito) {
            throw new SQLException("No se pudo desactivar la empleada.");
        }
    }
}
