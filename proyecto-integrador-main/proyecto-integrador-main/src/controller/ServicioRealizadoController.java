package controller;

import dao.ServicioRealizadoDAO;
import model.ServicioRealizado;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para Registro y Control de Servicios Efectivamente Prestados (RF-08).
 * Calcula la comisión individual según la tarifa pactada con la empleada.
 */
public class ServicioRealizadoController {
    private final ServicioRealizadoDAO servicioRealizadoDAO;
    private final CitaController citaController;

    public ServicioRealizadoController() {
        this.servicioRealizadoDAO = new ServicioRealizadoDAO();
        this.citaController = new CitaController();
    }

    public List<ServicioRealizado> listarTodos() throws SQLException {
        return servicioRealizadoDAO.listarTodos();
    }

    public List<ServicioRealizado> listarPendientes(int idEmpleada, Date inicio, Date fin) throws SQLException {
        return servicioRealizadoDAO.listarPendientesPorEmpleadaYFechas(idEmpleada, inicio, fin);
    }

    public void registrarServicioRealizado(
            Integer idCita, int idEmpleada, String nombreEmpleada,
            int idServicio, String nombreServicio, Integer idCliente,
            String nombreCliente, Date fecha, double valorCobrado,
            double porcentajeComision) throws Exception {

        if (idEmpleada <= 0 || nombreEmpleada == null || nombreEmpleada.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una empleada responsable.");
        }
        if (idServicio <= 0 || nombreServicio == null || nombreServicio.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un servicio realizado.");
        }
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del servicio es requerida.");
        }
        if (valorCobrado <= 0) {
            throw new IllegalArgumentException("El valor cobrado debe ser mayor a 0.");
        }
        if (porcentajeComision < 0 || porcentajeComision > 100) {
            throw new IllegalArgumentException("El porcentaje de comisión debe estar entre 0% y 100%.");
        }

        ServicioRealizado sr = new ServicioRealizado(
            idCita, idEmpleada, nombreEmpleada.trim(),
            idServicio, nombreServicio.trim(), idCliente,
            nombreCliente.trim(), fecha, valorCobrado, porcentajeComision
        );

        boolean exito = servicioRealizadoDAO.registrar(sr);
        if (!exito) {
            throw new SQLException("No se pudo registrar el servicio realizado.");
        }

        // Si provenía de una cita agendada, actualizar automáticamente la cita a Atendida/Completada
        if (idCita != null && idCita > 0) {
            try {
                citaController.marcarComoAtendida(idCita);
            } catch (Exception ignored) {}
        }
    }
}
