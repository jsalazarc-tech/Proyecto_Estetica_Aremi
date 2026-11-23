package controller;

import dao.EmpleadoDAO;
import dao.UsuarioDAO;
import model.Empleado;
import model.Usuario;
import util.SeguridadManager;
import java.sql.SQLException;

/**
 * Controlador de Autenticación y Seguridad (RNF-01, RNF-05).
 * Valida credenciales contra hashes SHA-256 almacenados y gestiona la sesión activa.
 */
public class AuthController {
    private final UsuarioDAO usuarioDAO;
    private final EmpleadoDAO empleadoDAO;

    public AuthController() {
        this.usuarioDAO = new UsuarioDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }

    public boolean iniciarSesion(String username, String passwordPlano) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese el nombre de usuario.");
        }
        if (passwordPlano == null || passwordPlano.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese su contraseña.");
        }

        String hash = SeguridadManager.hashPassword(passwordPlano);
        Usuario usuario = usuarioDAO.autenticar(username.trim(), hash);

        if (usuario != null) {
            Integer idEmpleada = null;
            if (usuario.esEmpleada()) {
                Empleado emp = empleadoDAO.buscarPorUsername(usuario.getUsername());
                if (emp != null) idEmpleada = emp.getId();
            }
            SeguridadManager.setUsuarioActual(usuario.getUsername(), usuario.getRol(), usuario.getNombreCompleto(), idEmpleada);
            return true;
        }
        return false;
    }

    public void cerrarSesion() {
        SeguridadManager.cerrarSesion();
    }
}
