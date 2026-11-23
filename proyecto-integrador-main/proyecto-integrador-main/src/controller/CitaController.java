package controller;

import dao.CitaDAO;
import model.Cita;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de Citas y Agenda (Requerimientos RF-05, RF-06 y RF-07).
 * Encargado de coordinar la validación rigurosa de disponibilidad y cruce de horarios.
 */
public class CitaController {
    private final CitaDAO citaDAO;

    public CitaController() {
        this.citaDAO = new CitaDAO();
    }

    public List<Cita> listarCitas(String filtroEstado, String filtroEmpleada) throws SQLException {
        return citaDAO.listar(filtroEstado, filtroEmpleada);
    }

    public void agendarCita(Integer idCliente, String nombreCliente, String telefonoCliente,
                            Integer idServicio, String nombreServicio,
                            Integer idEmpleada, String nombreEmpleada,
                            Date fecha, String hora, String notas) throws Exception {

        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (telefonoCliente == null || telefonoCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del cliente es obligatorio.");
        }
        if (nombreServicio == null || nombreServicio.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un servicio.");
        }
        if (nombreEmpleada == null || nombreEmpleada.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe asignar una empleada para la atención.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la cita no es válida.");
        }
        if (hora == null || hora.trim().isEmpty()) {
            throw new IllegalArgumentException("La hora de la cita es obligatoria.");
        }

        // VALIDACIÓN DE NEGOCIO: Prevención de cruce de horarios para la misma empleada (RF-06)
        if (idEmpleada != null && idEmpleada > 0) {
            boolean cruce = citaDAO.existeCruceHorario(idEmpleada, fecha, hora.trim(), null);
            if (cruce) {
                throw new IllegalStateException(
                    "Conflicto de horario: La profesional " + nombreEmpleada +
                    " ya tiene una cita reservada el día " + fecha + " a las " + hora + ".\n" +
                    "Por favor seleccione otro horario o asigne a otra profesional disponible."
                );
            }
        }

        Cita nuevaCita = new Cita(
            0, idCliente, nombreCliente.trim(), telefonoCliente.trim(),
            idServicio, nombreServicio.trim(), idEmpleada, nombreEmpleada.trim(),
            fecha, hora.trim(), "Pendiente", notas != null ? notas.trim() : ""
        );

        boolean exito = citaDAO.agendar(nuevaCita);
        if (!exito) {
            throw new SQLException("Error al persistir la cita en el sistema.");
        }
    }

    public void cancelarCita(int idCita) throws Exception {
        if (idCita <= 0) throw new IllegalArgumentException("Identificador de cita no válido.");
        boolean exito = citaDAO.actualizarEstado(idCita, "Cancelada");
        if (!exito) {
            throw new SQLException("No se pudo cancelar la cita seleccionada.");
        }
    }

    public void marcarComoAtendida(int idCita) throws Exception {
        if (idCita <= 0) throw new IllegalArgumentException("Identificador de cita no válido.");
        boolean exito = citaDAO.actualizarEstado(idCita, "Completada");
        if (!exito) {
            throw new SQLException("No se pudo actualizar el estado de la cita a Completada.");
        }
    }

    public Cita obtenerPorId(int idCita) throws SQLException {
        return citaDAO.buscarPorId(idCita);
    }
}
