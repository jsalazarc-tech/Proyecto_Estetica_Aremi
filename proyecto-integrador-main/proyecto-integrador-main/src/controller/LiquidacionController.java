package controller;

import dao.LiquidacionDAO;
import dao.ServicioRealizadoDAO;
import model.LiquidacionComision;
import model.ServicioRealizado;
import util.ReportePDFGenerator;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de Liquidación de Comisiones y Emisión de Comprobantes PDF (RF-09, RF-10).
 * Calcula montos consolidados: Total Ventas, Comisión a Pagar (20%, 30%, 50%) y Ganancia Salón.
 */
public class LiquidacionController {
    private final LiquidacionDAO liquidacionDAO;
    private final ServicioRealizadoDAO servicioRealizadoDAO;

    public LiquidacionController() {
        this.liquidacionDAO = new LiquidacionDAO();
        this.servicioRealizadoDAO = new ServicioRealizadoDAO();
    }

    public List<ServicioRealizado> consultarServiciosPendientes(int idEmpleada, Date fechaInicio, Date fechaFin) throws SQLException {
        return servicioRealizadoDAO.listarPendientesPorEmpleadaYFechas(idEmpleada, fechaInicio, fechaFin);
    }

    public List<LiquidacionComision> listarHistorico() throws SQLException {
        return liquidacionDAO.listarHistorico();
    }

    /**
     * Procesa la liquidación de comisiones de forma transaccional y retorna el ID del comprobante generado.
     */
    public int procesarLiquidacion(
            int idEmpleada, String nombreEmpleada, String especialidad,
            Date fechaInicio, Date fechaFin, double porcentajeComision,
            String notas) throws Exception {

        if (idEmpleada <= 0) throw new IllegalArgumentException("Seleccione una empleada válida.");
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Debe especificar el rango de fechas a liquidar.");
        }
        if (fechaInicio.after(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha final.");
        }

        List<ServicioRealizado> pendientes = consultarServiciosPendientes(idEmpleada, fechaInicio, fechaFin);
        if (pendientes.isEmpty()) {
            throw new IllegalStateException("No se encontraron servicios realizados pendientes de liquidar para " + nombreEmpleada + " en el período indicado.");
        }

        double totalVentas = 0;
        double totalComision = 0;
        List<Integer> idsServicios = new ArrayList<>();

        for (ServicioRealizado sr : pendientes) {
            totalVentas += sr.getValorCobrado();
            totalComision += sr.getComisionCalculada();
            idsServicios.add(sr.getId());
        }

        double totalSalon = totalVentas - totalComision;

        LiquidacionComision lc = new LiquidacionComision(
            idEmpleada, nombreEmpleada, fechaInicio, fechaFin,
            pendientes.size(), totalVentas, porcentajeComision, totalComision, totalSalon
        );
        lc.setNotas(notas);

        int idLiquidacion = liquidacionDAO.registrarLiquidacionTransaccional(lc, idsServicios);
        if (idLiquidacion <= 0) {
            throw new SQLException("No se pudo generar el registro de la liquidación.");
        }
        return idLiquidacion;
    }

    /**
     * Genera el documento PDF del comprobante oficial de liquidación.
     */
    public String exportarComprobantePDF(
            int idLiquidacion, String nombreEmpleada, String especialidad,
            double porcentajeComision, String fechaInicio, String fechaFin,
            int totalServicios, double totalVentas, double totalComision,
            double totalSalon, List<ServicioRealizado> listaServicios) throws Exception {

        List<Object[]> detalles = new ArrayList<>();
        if (listaServicios != null) {
            for (ServicioRealizado s : listaServicios) {
                detalles.add(new Object[]{
                    s.getFecha().toString(),
                    s.getNombreServicio(),
                    s.getNombreCliente(),
                    s.getValorCobrado()
                });
            }
        }

        return ReportePDFGenerator.generarComprobanteLiquidacion(
            idLiquidacion, nombreEmpleada, especialidad, porcentajeComision,
            fechaInicio, fechaFin, totalServicios, totalVentas, totalComision, totalSalon, detalles
        );
    }
}
