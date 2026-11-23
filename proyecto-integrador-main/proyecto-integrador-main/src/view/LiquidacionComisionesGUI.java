package view;

import controller.EmpleadoController;
import controller.LiquidacionController;
import model.Empleado;
import model.LiquidacionComision;
import model.ServicioRealizado;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista de Liquidación de Comisiones y Emisión de Reportes PDF (MVC - Vista).
 * Delega en LiquidacionController y EmpleadoController.
 * Cero SQL en esta interfaz.
 */
public class LiquidacionComisionesGUI extends JFrame {
    private final LiquidacionController liquidacionController;
    private final EmpleadoController empleadoController;

    private JTabbedPane tabbedPane;

    // Tab 1: Nueva Liquidación
    private JComboBox<Empleado> cboEmpleada;
    private JTextField txtFechaInicio, txtFechaFin;
    private JLabel lblEspecialidad, lblPorcentajeComision;
    private JTable tablaServiciosPendientes;
    private DefaultTableModel modeloServicios;
    private JLabel lblTotalServicios, lblTotalVentas, lblTotalComision, lblTotalSalon;
    private JButton btnConsultar, btnProcesarLiquidacion, btnExportarPDF;

    // Tab 2: Historial
    private JTable tablaHistorico;
    private DefaultTableModel modeloHistorico;
    private JButton btnRefrescarHistorico;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    // Estado en memoria para exportación
    private int ultimoIdLiquidacionGuardado = -1;
    private double ultimaVentaTotal = 0;
    private double ultimaComisionTotal = 0;
    private double ultimoSalonTotal = 0;
    private List<ServicioRealizado> ultimosPendientes = new ArrayList<>();

    public LiquidacionComisionesGUI() {
        this.liquidacionController = new LiquidacionController();
        this.empleadoController = new EmpleadoController();

        setTitle("💰 Liquidación de Comisiones del Personal - Salón AREMI");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        cargarEmpleadas();
        cargarHistorico();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Liquidar Período
        JPanel panelLiquidar = crearPanelLiquidacion();
        tabbedPane.addTab("  💼 Liquidar Período  ", panelLiquidar);

        // Tab 2: Historial
        JPanel panelHistorico = crearPanelHistorico();
        tabbedPane.addTab("  📜 Historial de Liquidaciones  ", panelHistorico);

        add(tabbedPane);
    }

    private JPanel crearPanelLiquidacion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Encabezado
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JPanel headerTitle = new JPanel(new BorderLayout());
        headerTitle.setBackground(ROSA_OSCURO);
        headerTitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel lblTit = new JLabel("Módulo Financiero de Liquidación de Comisiones");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Color.WHITE);
        headerTitle.add(lblTit, BorderLayout.WEST);
        topPanel.add(headerTitle, BorderLayout.NORTH);

        // Barra de Parámetros de Consulta
        JPanel paramsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        paramsPanel.setBackground(new Color(253, 245, 248));
        paramsPanel.setBorder(BorderFactory.createLineBorder(ROSA_CLARO, 1));

        paramsPanel.add(new JLabel("Empleada:"));
        cboEmpleada = new JComboBox<>();
        paramsPanel.add(cboEmpleada);

        lblEspecialidad = new JLabel("Especialidad: -");
        lblEspecialidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        paramsPanel.add(lblEspecialidad);

        lblPorcentajeComision = new JLabel("Comisión: -");
        lblPorcentajeComision.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPorcentajeComision.setForeground(new Color(180, 50, 90));
        paramsPanel.add(lblPorcentajeComision);

        LocalDate ahora = LocalDate.now();
        LocalDate primerDiaMes = ahora.withDayOfMonth(1);
        paramsPanel.add(new JLabel("Desde:"));
        txtFechaInicio = new JTextField(primerDiaMes.toString(), 9);
        paramsPanel.add(txtFechaInicio);

        paramsPanel.add(new JLabel("Hasta:"));
        txtFechaFin = new JTextField(ahora.toString(), 9);
        paramsPanel.add(txtFechaFin);

        btnConsultar = new JButton("Consultar Servicios");
        btnConsultar.setBackground(ROSA_OSCURO);
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setOpaque(true);
        btnConsultar.setBorderPainted(false);
        paramsPanel.add(btnConsultar);

        topPanel.add(paramsPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        // Tabla de servicios pendientes
        modeloServicios = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Servicio", "Cliente", "Valor Facturado", "Comisión %", "Comisión a Pagar"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaServiciosPendientes = new JTable(modeloServicios);
        tablaServiciosPendientes.setRowHeight(24);
        panel.add(new JScrollPane(tablaServiciosPendientes), BorderLayout.CENTER);

        // Panel Inferior: Consolidado y Acciones
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblTotalServicios = new JLabel("0", SwingConstants.CENTER);
        lblTotalVentas = new JLabel("$0", SwingConstants.CENTER);
        lblTotalComision = new JLabel("$0", SwingConstants.CENTER);
        lblTotalSalon = new JLabel("$0", SwingConstants.CENTER);

        cardsPanel.add(crearCard("Servicios Realizados", lblTotalServicios, new Color(70, 130, 180)));
        cardsPanel.add(crearCard("Total Facturado", lblTotalVentas, new Color(50, 50, 50)));
        cardsPanel.add(crearCard("Comisión Empleada", lblTotalComision, new Color(46, 139, 87)));
        cardsPanel.add(crearCard("Ganancia Salón", lblTotalSalon, ROSA_OSCURO));

        bottomPanel.add(cardsPanel, BorderLayout.NORTH);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        actionsPanel.setOpaque(false);

        btnExportarPDF = new JButton("📄 Generar Comprobante PDF");
        btnExportarPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportarPDF.setBackground(new Color(70, 130, 180));
        btnExportarPDF.setForeground(Color.WHITE);
        btnExportarPDF.setOpaque(true);
        btnExportarPDF.setBorderPainted(false);
        btnExportarPDF.setEnabled(false);

        btnProcesarLiquidacion = new JButton("✔ Procesar y Guardar Liquidación");
        btnProcesarLiquidacion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnProcesarLiquidacion.setBackground(ROSA_OSCURO);
        btnProcesarLiquidacion.setForeground(Color.WHITE);
        btnProcesarLiquidacion.setOpaque(true);
        btnProcesarLiquidacion.setBorderPainted(false);

        actionsPanel.add(btnExportarPDF);
        actionsPanel.add(btnProcesarLiquidacion);
        bottomPanel.add(actionsPanel, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        cboEmpleada.addActionListener(e -> actualizarDatosEmpleada());
        btnConsultar.addActionListener(e -> consultarServiciosPendientes());
        btnProcesarLiquidacion.addActionListener(e -> procesarLiquidacion());
        btnExportarPDF.addActionListener(e -> exportarPDF());

        return panel;
    }

    private JPanel crearPanelHistorico() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        btnRefrescarHistorico = new JButton("Refrescar Historial");
        top.add(btnRefrescarHistorico);
        panel.add(top, BorderLayout.NORTH);

        modeloHistorico = new DefaultTableModel(
            new String[]{"ID", "Empleada", "Período Desde", "Período Hasta", "Servicios", "Total Ventas", "Comisión %", "Total Comisión", "Total Salón", "Fecha Registro"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaHistorico = new JTable(modeloHistorico);
        tablaHistorico.setRowHeight(24);
        panel.add(new JScrollPane(tablaHistorico), BorderLayout.CENTER);

        btnRefrescarHistorico.addActionListener(e -> cargarHistorico());
        return panel;
    }

    private JPanel crearCard(String titulo, JLabel valorLabel, Color colorTexto) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(Color.GRAY);

        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valorLabel.setForeground(colorTexto);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        return card;
    }

    private void cargarEmpleadas() {
        cboEmpleada.removeAllItems();
        try {
            List<Empleado> lista = empleadoController.listarActivas();
            for (Empleado emp : lista) cboEmpleada.addItem(emp);
            actualizarDatosEmpleada();
        } catch (Exception ignored) {}
    }

    private void actualizarDatosEmpleada() {
        Empleado emp = (Empleado) cboEmpleada.getSelectedItem();
        if (emp != null) {
            lblEspecialidad.setText("Especialidad: " + emp.getEspecialidad());
            lblPorcentajeComision.setText("Comisión: " + String.format("%.0f%%", emp.getPorcentajeComision()));
        }
    }

    private void consultarServiciosPendientes() {
        Empleado emp = (Empleado) cboEmpleada.getSelectedItem();
        if (emp == null) return;

        try {
            Date fIni = Date.valueOf(txtFechaInicio.getText().trim());
            Date fFin = Date.valueOf(txtFechaFin.getText().trim());

            modeloServicios.setRowCount(0);
            ultimosPendientes = liquidacionController.consultarServiciosPendientes(emp.getId(), fIni, fFin);

            double ventas = 0;
            double comision = 0;

            for (ServicioRealizado sr : ultimosPendientes) {
                ventas += sr.getValorCobrado();
                comision += sr.getComisionCalculada();

                modeloServicios.addRow(new Object[]{
                    sr.getId(),
                    sr.getFecha(),
                    sr.getNombreServicio(),
                    sr.getNombreCliente(),
                    "$" + String.format("%,.0f", sr.getValorCobrado()),
                    String.format("%.0f%%", sr.getPorcentajeComision()),
                    "$" + String.format("%,.0f", sr.getComisionCalculada())
                });
            }

            double salon = ventas - comision;
            ultimaVentaTotal = ventas;
            ultimaComisionTotal = comision;
            ultimoSalonTotal = salon;

            lblTotalServicios.setText(String.valueOf(ultimosPendientes.size()));
            lblTotalVentas.setText("$" + String.format("%,.0f", ventas));
            lblTotalComision.setText("$" + String.format("%,.0f", comision));
            lblTotalSalon.setText("$" + String.format("%,.0f", salon));

            btnExportarPDF.setEnabled(false);

            if (ultimosPendientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay servicios pendientes de liquidar para " + emp.getNombre() + " en este rango de fechas.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en formato de fechas (AAAA-MM-DD): " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarLiquidacion() {
        Empleado emp = (Empleado) cboEmpleada.getSelectedItem();
        if (emp == null || ultimosPendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay servicios pendientes listados para liquidar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Desea procesar y asentar formalmente la liquidación de " + emp.getNombre() + "?\n\n" +
            "Servicios a liquidar: " + ultimosPendientes.size() + "\n" +
            "Total Comisión a Pagar: $" + String.format("%,.0f", ultimaComisionTotal) + " COP\n" +
            "Ganancia Neta Salón: $" + String.format("%,.0f", ultimoSalonTotal) + " COP",
            "Confirmar Liquidación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Date fIni = Date.valueOf(txtFechaInicio.getText().trim());
                Date fFin = Date.valueOf(txtFechaFin.getText().trim());

                ultimoIdLiquidacionGuardado = liquidacionController.procesarLiquidacion(
                    emp.getId(), emp.getNombre(), emp.getEspecialidad(),
                    fIni, fFin, emp.getPorcentajeComision(), "Liquidación oficial registrada."
                );

                btnExportarPDF.setEnabled(true);
                JOptionPane.showMessageDialog(this, "✔ Liquidación procesada exitosamente con N° LIQ-" + String.format("%05d", ultimoIdLiquidacionGuardado) + ".\nYa puede generar el Comprobante en PDF.");

                consultarServiciosPendientes();
                cargarHistorico();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al procesar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarPDF() {
        if (ultimoIdLiquidacionGuardado <= 0) {
            JOptionPane.showMessageDialog(this, "Primero debe procesar una liquidación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Empleado emp = (Empleado) cboEmpleada.getSelectedItem();
        try {
            String ruta = liquidacionController.exportarComprobantePDF(
                ultimoIdLiquidacionGuardado,
                emp.getNombre(),
                emp.getEspecialidad(),
                emp.getPorcentajeComision(),
                txtFechaInicio.getText(),
                txtFechaFin.getText(),
                Integer.parseInt(lblTotalServicios.getText()),
                ultimaVentaTotal,
                ultimaComisionTotal,
                ultimoSalonTotal,
                ultimosPendientes
            );

            JOptionPane.showMessageDialog(this, "✔ Comprobante PDF generado exitosamente en:\n" + ruta);
            try {
                Desktop.getDesktop().open(new File(ruta));
            } catch (Exception ignored) {}

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorico() {
        modeloHistorico.setRowCount(0);
        try {
            List<LiquidacionComision> lista = liquidacionController.listarHistorico();
            for (LiquidacionComision lc : lista) {
                modeloHistorico.addRow(new Object[]{
                    lc.getId(),
                    lc.getNombreEmpleada(),
                    lc.getFechaInicio(),
                    lc.getFechaFin(),
                    lc.getTotalServicios(),
                    "$" + String.format("%,.0f", lc.getTotalVentas()),
                    String.format("%.0f%%", lc.getPorcentajeAplicado()),
                    "$" + String.format("%,.0f", lc.getTotalComision()),
                    "$" + String.format("%,.0f", lc.getTotalSalon()),
                    lc.getFechaRegistro()
                });
            }
        } catch (Exception ignored) {}
    }
}
