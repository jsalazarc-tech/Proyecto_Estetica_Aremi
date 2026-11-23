package controller;

import dao.ClienteDAO;
import model.Cliente;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la gestión de Clientes.
 * Aplica validaciones de reglas de negocio antes de delegar la persistencia en el DAO.
 */
public class ClienteController {
    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public List<Cliente> listarClientes(String filtro) throws SQLException {
        return clienteDAO.listar(filtro);
    }

    public Cliente obtenerClientePorId(int id) throws SQLException {
        return clienteDAO.buscarPorId(id);
    }

    public void registrarCliente(String nombre, String telefono, String direccion, String email) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del cliente es obligatorio.");
        }

        Cliente nuevo = new Cliente(nombre.trim(), direccion != null ? direccion.trim() : "", telefono.trim());
        nuevo.setEmail(email != null ? email.trim() : "");

        boolean exito = clienteDAO.guardar(nuevo);
        if (!exito) {
            throw new SQLException("No se pudo insertar el registro del cliente en la base de datos.");
        }
    }

    public void actualizarCliente(int id, String nombre, String telefono, String direccion, String email) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Identificador de cliente no válido.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del cliente es obligatorio.");
        }

        Cliente cliente = new Cliente(id, nombre.trim(), direccion != null ? direccion.trim() : "", telefono.trim(), email != null ? email.trim() : "");
        boolean exito = clienteDAO.actualizar(cliente);
        if (!exito) {
            throw new SQLException("No se pudo actualizar el registro del cliente.");
        }
    }

    public void eliminarCliente(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Identificador de cliente no válido.");
        }
        boolean exito = clienteDAO.eliminar(id);
        if (!exito) {
            throw new SQLException("No se pudo eliminar el cliente. Verifique que no tenga citas asociadas.");
        }
    }
}
