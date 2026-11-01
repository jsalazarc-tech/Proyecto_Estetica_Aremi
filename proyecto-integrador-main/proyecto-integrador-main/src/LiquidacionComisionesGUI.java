import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LiquidacionComisionesGUI extends JFrame {
    private JTabbedPane tabbedPane;

    // Tab 1: Nueva Liquidación
    private JComboBox<EmpleadoItem> cboEmpleada;
    private JTextField txtFechaInicio, txtFechaFin;
    private JLabel lblEspecialidad, lblPorcentajeComision;
    private JTable tablaServiciosPendientes;
    private DefaultTableModel modeloServicios;
    private JLabel lblTotalServicios, lblTotalVentas, lblTotalComision, lblTotalSalon;
    private JButton btnConsultar, btnProcesarLiquidacion, btnExportarPDF;

    // Tab 2: Historial de Liquidaciones
    private JTable tablaHistorico;
    private DefaultTableModel modeloHistorico;
    private JButton btnRefrescarHistorico, btnReimprimirPDF;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    // Estado actual de cálculo
    private int ultimoIdLiquidacionGuardado = -1;
    private double ultimaVentaTotal = 0;
    private double ultimaComisionTotal = 0;
    private double ultimoSalonTotal = 0;
    private List<Object[]> ultimosDetalles = new ArrayList<>();

    private static class EmpleadoItem {
        int id;
        String nombre;
        String especialidad;
        double porcentaje;
        EmpleadoItem(int id, String n, String esp, double p) {
            this.id = id; this.nombre = n; this.especialidad = esp; this.porcentaje = p;
        }
        @Override public String toString() { return nombre + " (" + especialidad + " - " + String.format("%.0f%%", porcentaje) + ")"; }
    }

    public LiquidacionComisionesGUI() {
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

        JPanel tabNueva = createTabNuevaLiquidacion();
        JPanel tabHistorico = createTabHistorico();

        tabbedPane.addTab("  💵 Liquidar Período  ", tabNueva);
        tabbedPane.addTab("  📜 Historial de Liquidaciones  ", tabHistorico);

        add(tabbedPane);
    }

    private JPanel createTabNuevaLiquidacion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Encabezado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ROSA_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblTitulo = new JLabel("Liquidación de Comisiones por Servicios Prestados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JLabel lblRegla = new JLabel("Ashley: 20% | Monika: 30% | Veronika: 50%");
        lblRegla.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRegla.setForeground(Color.WHITE);
        headerPanel.add(lblRegla, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Barra superior de selección y filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterPanel.setBackground(new Color(253, 245, 248));
        filterPanel.setBorder(BorderFactory.createLineBorder(ROSA_CLARO, 1));

        filterPanel.add(new JLabel("Empleada:"));
        cboEmpleada = new JComboBox<>();
        cboEmpleada.setPreferredSize(new Dimension(220, 28));
        filterPanel.add(cboEmpleada);

        filterPanel.add(new JLabel("Desde:"));
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        txtFechaInicio = new JTextField(inicioMes.toString(), 9);
        filterPanel.add(txtFechaInicio);

        filterPanel.add(new JLabel("Hasta:"));
        txtFechaFin = new JTextField(LocalDate.now().toString(), 9);
        filterPanel.add(txtFechaFin);

        btnConsultar = new JButton("Consultar Servicios");
        btnConsultar.setBackground(ROSA_OSCURO);
        btnConsultar.setForeground(Color.WHITE);
        filterPanel.add(btnConsultar);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabla central con servicios realizados pendientes de liquidar
        String[] cols = {"ID", "Fecha", "Servicio", "Cliente", "Valor Facturado", "Comisión %", "Comisión a Pagar"};
        modeloServicios = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaServiciosPendientes = new JTable(modeloServicios);
        tablaServiciosPendientes.setRowHeight(24);
        tablaServiciosPendientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaServiciosPendientes.getTableHeader().setBackground(new Color(245, 230, 238));

        tablaServiciosPendientes.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaServiciosPendientes.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaServiciosPendientes.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaServiciosPendientes.getColumnModel().getColumn(3).setPreferredWidth(160);
        tablaServiciosPendientes.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaServiciosPendientes.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaServiciosPendientes.getColumnModel().getColumn(6).setPreferredWidth(120);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(new JLabel("Servicios Efectivamente Realizados (Pendientes de Liquidar):"), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tablaServiciosPendientes), BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Panel inferior de resumen y totales
        JPanel bottomContainer = new JPanel(new BorderLayout(10, 10));
        bottomContainer.setBackground(Color.WHITE);

        JPanel totalsPanel = new JPanel(new GridLayout(2, 4, 10, 6));
        totalsPanel.setBackground(new Color(250, 245, 248));
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        lblTotalServicios = new JLabel("0", SwingConstants.CENTER);
        lblTotalVentas = new JLabel("$0", SwingConstants.CENTER);
        lblTotalComision = new JLabel("$0", SwingConstants.CENTER);
        lblTotalSalon = new JLabel("$0", SwingConstants.CENTER);

        Font valFont = new Font("Segoe UI", Font.BOLD, 15);
        lblTotalServicios.setFont(valFont);
        lblTotalVentas.setFont(valFont);
        lblTotalComision.setFont(valFont);
        lblTotalComision.setForeground(new Color(46, 139, 87));
        lblTotalSalon.setFont(valFont);
        lblTotalSalon.setForeground(new Color(70, 130, 180));

        totalsPanel.add(createHeaderLabel("Servicios Realizados"));
        totalsPanel.add(createHeaderLabel("Total Ventas"));
        totalsPanel.add(createHeaderLabel("Comisión Empleada"));
        totalsPanel.add(createHeaderLabel("Ganancia Salón"));

        totalsPanel.add(lblTotalServicios);
        totalsPanel.add(lblTotalVentas);
        totalsPanel.add(lblTotalComision);
        totalsPanel.add(lblTotalSalon);

        bottomContainer.add(totalsPanel, BorderLayout.CENTER);

        // Botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setBackground(Color.WHITE);

        btnProcesarLiquidacion = new JButton("💾 Procesar y Guardar Liquidación");
        btnProcesarLiquidacion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcesarLiquidacion.setBackground(ROSA_OSCURO);
        btnProcesarLiquidacion.setForeground(Color.WHITE);
        btnProcesarLiquidacion.setPreferredSize(new Dimension(280, 42));

        btnExportarPDF = new JButton("📄 Generar Comprobante PDF");
        btnExportarPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportarPDF.setBackground(new Color(70, 130, 180));
        btnExportarPDF.setForeground(Color.WHITE);
        btnExportarPDF.setPreferredSize(new Dimension(230, 42));
        btnExportarPDF.setEnabled(false);

        actionPanel.add(btnExportarPDF);
        actionPanel.add(btnProcesarLiquidacion);
        bottomContainer.add(actionPanel, BorderLayout.SOUTH);

        panel.add(bottomContainer, BorderLayout.SOUTH);

        // Listeners
        btnConsultar.addActionListener(e -> consultarServicios());
        btnProcesarLiquidacion.addActionListener(e -> procesarLiquidacion());
        btnExportarPDF.addActionListener(e -> exportarComprobantePDF());

        return panel;
    }

    private JPanel createTabHistorico() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setBackground(Color.WHITE);
        btnRefrescarHistorico = new JButton("Actualizar Historial");
        btnReimprimirPDF = new JButton("Reimprimir Comprobante PDF");
        btnRefrescarHistorico.setBackground(ROSA_OSCURO);
        btnRefrescarHistorico.setForeground(Color.WHITE);

        topBar.add(btnRefrescarHistorico);
        topBar.add(btnReimprimirPDF);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Fecha Registro", "Empleada", "Período Desde", "Período Hasta", "Servicios", "Total Facturado", "% Comisión", "Comisión Pagada", "Salón"};
        modeloHistorico = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorico = new JTable(modeloHistorico);
        tablaHistorico.setRowHeight(24);
        tablaHistorico.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorico.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaHistorico.getTableHeader().setBackground(new Color(245, 230, 238));

        panel.add(new JScrollPane(tablaHistorico), BorderLayout.CENTER);

        btnRefrescarHistorico.addActionListener(e -> cargarHistorico());
        btnReimprimirPDF.addActionListener(e -> reimprimirPDFHistorico());

        return panel;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.DARK_GRAY);
        return l;
    }

    private void cargarEmpleadas() {
        cboEmpleada.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, especialidad, porcentaje_comision FROM empleadas WHERE activo = 1 ORDER BY nombre ASC")) {
            while (rs.next()) {
                cboEmpleada.addItem(new EmpleadoItem(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("especialidad"),
                    rs.getDouble("porcentaje_comision")
                ));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void consultarServicios() {
        EmpleadoItem emp = (EmpleadoItem) cboEmpleada.getSelectedItem();
        if (emp == null) return;

        String fIniStr = txtFechaInicio.getText().trim();
        String fFinStr = txtFechaFin.getText().trim();

        Date fIni, fFin;
        try {
            fIni = Date.valueOf(fIniStr);
            fFin = Date.valueOf(fFinStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice AAAA-MM-DD.");
            return;
        }

        modeloServicios.setRowCount(0);
        ultimosDetalles.clear();

        String sql = "SELECT id, fecha, nombre_servicio, nombre_cliente, valor_cobrado, porcentaje_comision, comision_calculada " +
                     "FROM servicios_realizados " +
                     "WHERE id_empleada = ? AND fecha BETWEEN ? AND ? AND estado_liquidacion = 'Pendiente' " +
                     "ORDER BY fecha ASC";

        int count = 0;
        double sumVentas = 0;
        double sumComision = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, emp.id);
            ps.setDate(2, fIni);
            ps.setDate(3, fFin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    count++;
                    double val = rs.getDouble("valor_cobrado");
                    double com = rs.getDouble("comision_calculada");
                    double pct = rs.getDouble("porcentaje_comision");

                    sumVentas += val;
                    sumComision += com;

                    modeloServicios.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        rs.getString("nombre_servicio"),
                        rs.getString("nombre_cliente"),
                        String.format("$%,.0f", val),
                        String.format("%.0f%%", pct),
                        String.format("$%,.0f", com)
                    });

                    ultimosDetalles.add(new Object[]{
                        rs.getDate("fecha").toString(),
                        rs.getString("nombre_servicio"),
                        rs.getString("nombre_cliente"),
                        String.format("$%,.0f", val)
                    });
                }
            }

            ultimaVentaTotal = sumVentas;
            ultimaComisionTotal = sumComision;
            ultimoSalonTotal = sumVentas - sumComision;

            lblTotalServicios.setText(String.valueOf(count));
            lblTotalVentas.setText(String.format("$%,.0f", sumVentas));
            lblTotalComision.setText(String.format("$%,.0f", sumComision));
            lblTotalSalon.setText(String.format("$%,.0f", ultimoSalonTotal));

            btnExportarPDF.setEnabled(false);

            if (count == 0) {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron servicios pendientes de liquidar para " + emp.nombre + " en el rango de fechas seleccionado.",
                    "Sin Servicios Pendientes", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar: " + e.getMessage());
        }
    }

    private void procesarLiquidacion() {
        if (modeloServicios.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay servicios cargados para liquidar. Realice una consulta primero.");
            return;
        }

        EmpleadoItem emp = (EmpleadoItem) cboEmpleada.getSelectedItem();
        Date fIni = Date.valueOf(txtFechaInicio.getText().trim());
        Date fFin = Date.valueOf(txtFechaFin.getText().trim());

        int resp = JOptionPane.showConfirmDialog(this,
            "¿Desea cerrar y procesar la liquidación de " + emp.nombre + "?\n\n" +
            "Servicios a liquidar: " + modeloServicios.getRowCount() + "\n" +
            "Total Comisión a Pagar: " + String.format("$%,.0f", ultimaComisionTotal) + "\n" +
            "Participación Salón: " + String.format("$%,.0f", ultimoSalonTotal),
            "Confirmar Liquidación", JOptionPane.YES_NO_OPTION);

        if (resp != JOptionPane.YES_OPTION) return;

        String insertLiq = "INSERT INTO liquidaciones (id_empleada, nombre_empleada, fecha_inicio, fecha_fin, total_servicios, total_ventas, porcentaje_aplicado, total_comision, total_salon) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertLiq, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, emp.id);
            ps.setString(2, emp.nombre);
            ps.setDate(3, fIni);
            ps.setDate(4, fFin);
            ps.setInt(5, modeloServicios.getRowCount());
            ps.setDouble(6, ultimaVentaTotal);
            ps.setDouble(7, emp.porcentaje);
            ps.setDouble(8, ultimaComisionTotal);
            ps.setDouble(9, ultimoSalonTotal);
            ps.executeUpdate();

            int idLiq = 0;
            try (ResultSet rk = ps.getGeneratedKeys()) {
                if (rk.next()) idLiq = rk.getInt(1);
            }
            ultimoIdLiquidacionGuardado = idLiq;

            // Actualizar servicios como liquidados
            String updateServ = "UPDATE servicios_realizados SET estado_liquidacion = 'Liquidado', id_liquidacion = ? " +
                                "WHERE id_empleada = ? AND fecha BETWEEN ? AND ? AND estado_liquidacion = 'Pendiente'";
            try (PreparedStatement psUp = conn.prepareStatement(updateServ)) {
                psUp.setInt(1, idLiq);
                psUp.setInt(2, emp.id);
                psUp.setDate(3, fIni);
                psUp.setDate(4, fFin);
                psUp.executeUpdate();
            }

            btnExportarPDF.setEnabled(true);
            JOptionPane.showMessageDialog(this,
                "✔ Liquidación N° " + idLiq + " registrada con éxito.\n" +
                "Servicios marcados como liquidados.",
                "Liquidación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            // Preguntar si desea exportar PDF de inmediato
            int opt = JOptionPane.showConfirmDialog(this, "¿Desea generar el comprobante oficial en PDF ahora?", "Generar PDF", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                exportarComprobantePDF();
            }

            cargarHistorico();
            modeloServicios.setRowCount(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar liquidación: " + e.getMessage());
        }
    }

    private void exportarComprobantePDF() {
        if (ultimoIdLiquidacionGuardado <= 0) {
            JOptionPane.showMessageDialog(this, "No hay una liquidación activa para imprimir.");
            return;
        }

        EmpleadoItem emp = (EmpleadoItem) cboEmpleada.getSelectedItem();
        try {
            String path = ReportePDFGenerator.generarComprobanteLiquidacion(
                ultimoIdLiquidacionGuardado,
                emp.nombre,
                emp.especialidad,
                emp.porcentaje,
                txtFechaInicio.getText().trim(),
                txtFechaFin.getText().trim(),
                ultimosDetalles.size(),
                ultimaVentaTotal,
                ultimaComisionTotal,
                ultimoSalonTotal,
                ultimosDetalles
            );

            JOptionPane.showMessageDialog(this,
                "📄 Comprobante PDF generado exitosamente:\n" + path,
                "PDF Generado", JOptionPane.INFORMATION_MESSAGE);

            // Abrir archivo automáticamente en el visor predeterminado
            try {
                Desktop.getDesktop().open(new java.io.File(path));
            } catch (Exception ex) {
                // ignore
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generando PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarHistorico() {
        modeloHistorico.setRowCount(0);
        String sql = "SELECT id, fecha_registro, nombre_empleada, fecha_inicio, fecha_fin, total_servicios, total_ventas, porcentaje_aplicado, total_comision, total_salon " +
                     "FROM liquidaciones ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                modeloHistorico.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("fecha_registro"),
                    rs.getString("nombre_empleada"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getInt("total_servicios"),
                    String.format("$%,.0f", rs.getDouble("total_ventas")),
                    String.format("%.0f%%", rs.getDouble("porcentaje_aplicado")),
                    String.format("$%,.0f", rs.getDouble("total_comision")),
                    String.format("$%,.0f", rs.getDouble("total_salon"))
                });
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    private void reimprimirPDFHistorico() {
        int sel = tablaHistorico.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una liquidación de la lista histórica.");
            return;
        }

        int idLiq = (int) modeloHistorico.getValueAt(sel, 0);
        String emp = (String) modeloHistorico.getValueAt(sel, 2);
        Date fIni = (Date) modeloHistorico.getValueAt(sel, 3);
        Date fFin = (Date) modeloHistorico.getValueAt(sel, 4);

        // Obtener detalles de la BD
        List<Object[]> detalles = new ArrayList<>();
        double totalVentas = 0;
        double totalComision = 0;
        double totalSalon = 0;
        double pct = 20;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT fecha, nombre_servicio, nombre_cliente, valor_cobrado, comision_calculada, porcentaje_comision " +
                 "FROM servicios_realizados WHERE id_liquidacion = ?")) {
            ps.setInt(1, idLiq);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new Object[]{
                        rs.getDate("fecha").toString(),
                        rs.getString("nombre_servicio"),
                        rs.getString("nombre_cliente"),
                        String.format("$%,.0f", rs.getDouble("valor_cobrado"))
                    });
                    totalVentas += rs.getDouble("valor_cobrado");
                    totalComision += rs.getDouble("comision_calculada");
                    pct = rs.getDouble("porcentaje_comision");
                }
            }
            totalSalon = totalVentas - totalComision;

            String path = ReportePDFGenerator.generarComprobanteLiquidacion(
                idLiq, emp, SeguridadManager.getEspecialidadEmpleada(emp),
                pct, fIni.toString(), fFin.toString(),
                detalles.size(), totalVentas, totalComision, totalSalon, detalles
            );

            JOptionPane.showMessageDialog(this, "Comprobante reimpreso:\n" + path);
            try { Desktop.getDesktop().open(new java.io.File(path)); } catch (Exception ex) {}

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar comprobante: " + e.getMessage());
        }
    }
}
