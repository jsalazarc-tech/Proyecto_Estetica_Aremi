import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class ServiciosRealizadosGUI extends JFrame {
    private JComboBox<EmpleadoComboItem> cboEmpleada;
    private JComboBox<ServicioComboItem> cboServicio;
    private JTextField txtCliente, txtValorCobrado, txtPorcentajeComision, txtComisionCalculada;
    private JTextField txtFecha;
    private JTable tablaRealizados;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnRefrescar, btnLimpiar;

    private Integer idCitaAsociada = null;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    private static class EmpleadoComboItem {
        int id;
        String nombre;
        double porcentaje;
        EmpleadoComboItem(int id, String n, double p) { this.id = id; this.nombre = n; this.porcentaje = p; }
        @Override public String toString() { return nombre + " (" + String.format("%.0f%%", porcentaje) + ")"; }
    }

    private static class ServicioComboItem {
        int id;
        String nombre;
        double precio;
        ServicioComboItem(int id, String n, double p) { this.id = id; this.nombre = n; this.precio = p; }
        @Override public String toString() { return nombre + " - $" + String.format("%,.0f", precio); }
    }

    public ServiciosRealizadosGUI() {
        this(null, "", "", "", null);
    }

    public ServiciosRealizadosGUI(Integer idCita, String cliente, String servicio, String empleada, Date fecha) {
        this.idCitaAsociada = idCita;
        setTitle("💅 Servicios Efectivamente Realizados - Salón AREMI");
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        cargarCombos();

        if (idCita != null) {
            precargarDesdeCita(cliente, servicio, empleada, fecha);
        }
        cargarTablaServiciosRealizados();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ROSA_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblTitulo = new JLabel("Registro y Control de Servicios Prestados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JLabel lblSub = new JLabel("Base para la liquidación de comisiones del personal");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(Color.WHITE);
        headerPanel.add(lblSub, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(253, 245, 248));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            "Registrar Servicio Prestado",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), ROSA_OSCURO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        cboEmpleada = new JComboBox<>();
        cboServicio = new JComboBox<>();
        txtCliente = new JTextField(15);
        txtFecha = new JTextField(LocalDate.now().toString(), 15);
        txtValorCobrado = new JTextField(15);
        txtPorcentajeComision = new JTextField(15);
        txtPorcentajeComision.setEditable(false);
        txtPorcentajeComision.setBackground(new Color(240, 240, 240));

        txtComisionCalculada = new JTextField(15);
        txtComisionCalculada.setEditable(false);
        txtComisionCalculada.setBackground(new Color(240, 240, 240));
        txtComisionCalculada.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtComisionCalculada.setForeground(new Color(46, 139, 87));

        int row = 0;
        addFormRow(formPanel, "Empleada que atendió *:", cboEmpleada, gbc, row++);
        addFormRow(formPanel, "Servicio realizado *:", cboServicio, gbc, row++);
        addFormRow(formPanel, "Cliente atendido *:", txtCliente, gbc, row++);
        addFormRow(formPanel, "Fecha (AAAA-MM-DD) *:", txtFecha, gbc, row++);
        addFormRow(formPanel, "Valor Cobrado ($ COP) *:", txtValorCobrado, gbc, row++);
        addFormRow(formPanel, "Porcentaje Comisión:", txtPorcentajeComision, gbc, row++);
        addFormRow(formPanel, "Comisión a Pagar ($):", txtComisionCalculada, gbc, row++);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setOpaque(false);
        btnRegistrar = new JButton("Guardar Servicio Realizado");
        btnRegistrar.setBackground(ROSA_OSCURO);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(220, 220, 220));
        btnLimpiar.setForeground(Color.BLACK);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);

        btnPanel.add(btnRegistrar);
        btnPanel.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 5, 10);
        formPanel.add(btnPanel, gbc);

        // Tabla de servicios realizados
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(Color.WHITE);

        JPanel tableHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        tableHeader.setBackground(Color.WHITE);
        tableHeader.add(new JLabel("Historial de Servicios Realizados:"));
        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBackground(new Color(70, 130, 180));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setOpaque(true);
        btnRefrescar.setBorderPainted(false);
        tableHeader.add(btnRefrescar);
        tablePanel.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {"ID", "Fecha", "Empleada", "Servicio", "Cliente", "Valor Cobrado", "Comisión %", "Comisión $", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaRealizados = new JTable(modeloTabla);
        tablaRealizados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRealizados.setRowHeight(24);
        tablaRealizados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaRealizados.getTableHeader().setBackground(new Color(245, 230, 238));

        tablaRealizados.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaRealizados.getColumnModel().getColumn(1).setPreferredWidth(85);
        tablaRealizados.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaRealizados.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaRealizados.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaRealizados.getColumnModel().getColumn(5).setPreferredWidth(95);
        tablaRealizados.getColumnModel().getColumn(6).setPreferredWidth(75);
        tablaRealizados.getColumnModel().getColumn(7).setPreferredWidth(95);
        tablaRealizados.getColumnModel().getColumn(8).setPreferredWidth(80);

        tablePanel.add(new JScrollPane(tablaRealizados), BorderLayout.CENTER);

        JPanel contentSplit = new JPanel(new BorderLayout(15, 10));
        contentSplit.setBackground(Color.WHITE);
        contentSplit.add(formPanel, BorderLayout.WEST);
        contentSplit.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentSplit, BorderLayout.CENTER);
        add(mainPanel);

        // Eventos
        cboEmpleada.addActionListener(e -> recalcularComision());
        cboServicio.addActionListener(e -> {
            ServicioComboItem s = (ServicioComboItem) cboServicio.getSelectedItem();
            if (s != null) {
                txtValorCobrado.setText(String.format("%.0f", s.precio));
                recalcularComision();
            }
        });

        txtValorCobrado.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                recalcularComision();
            }
        });

        btnRegistrar.addActionListener(e -> guardarServicioRealizado());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRefrescar.addActionListener(e -> cargarTablaServiciosRealizados());
    }

    private void addFormRow(JPanel panel, String label, JComponent comp, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    private void cargarCombos() {
        cboEmpleada.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, porcentaje_comision FROM empleadas WHERE activo = 1 ORDER BY nombre ASC")) {
            while (rs.next()) {
                cboEmpleada.addItem(new EmpleadoComboItem(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("porcentaje_comision")));
            }
        } catch (Exception e) {
            // ignore
        }

        cboServicio.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, precio FROM servicios WHERE activo = 1 ORDER BY nombre ASC")) {
            while (rs.next()) {
                cboServicio.addItem(new ServicioComboItem(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio")));
            }
        } catch (Exception e) {
            // ignore
        }
        recalcularComision();
    }

    private void precargarDesdeCita(String cliente, String servicio, String empleada, Date fecha) {
        txtCliente.setText(cliente);
        if (fecha != null) txtFecha.setText(fecha.toString());

        for (int i = 0; i < cboEmpleada.getItemCount(); i++) {
            if (cboEmpleada.getItemAt(i).nombre.equalsIgnoreCase(empleada)) {
                cboEmpleada.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cboServicio.getItemCount(); i++) {
            if (cboServicio.getItemAt(i).nombre.equalsIgnoreCase(servicio)) {
                cboServicio.setSelectedIndex(i);
                break;
            }
        }
        recalcularComision();
    }

    private void recalcularComision() {
        EmpleadoComboItem emp = (EmpleadoComboItem) cboEmpleada.getSelectedItem();
        if (emp != null) {
            txtPorcentajeComision.setText(String.format("%.0f%%", emp.porcentaje));
            try {
                double valor = Double.parseDouble(txtValorCobrado.getText().trim());
                double comision = valor * (emp.porcentaje / 100.0);
                txtComisionCalculada.setText(String.format("$%,.0f", comision));
            } catch (Exception e) {
                txtComisionCalculada.setText("$0");
            }
        }
    }

    private void guardarServicioRealizado() {
        EmpleadoComboItem emp = (EmpleadoComboItem) cboEmpleada.getSelectedItem();
        ServicioComboItem serv = (ServicioComboItem) cboServicio.getSelectedItem();
        String cliente = txtCliente.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        String valorStr = txtValorCobrado.getText().trim();

        if (emp == null || serv == null || cliente.isEmpty() || fechaStr.isEmpty() || valorStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        double valorCobrado;
        Date fecha;
        try {
            valorCobrado = Double.parseDouble(valorStr);
            fecha = Date.valueOf(fechaStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fecha (formato AAAA-MM-DD) o valor numérico inválidos.");
            return;
        }

        double comision = valorCobrado * (emp.porcentaje / 100.0);

        String sql = "INSERT INTO servicios_realizados (id_cita, id_empleada, nombre_empleada, id_servicio, nombre_servicio, nombre_cliente, fecha, valor_cobrado, porcentaje_comision, comision_calculada, estado_liquidacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pendiente')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (idCitaAsociada != null) ps.setInt(1, idCitaAsociada); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, emp.id);
            ps.setString(3, emp.nombre);
            ps.setInt(4, serv.id);
            ps.setString(5, serv.nombre);
            ps.setString(6, cliente);
            ps.setDate(7, fecha);
            ps.setDouble(8, valorCobrado);
            ps.setDouble(9, emp.porcentaje);
            ps.setDouble(10, comision);
            ps.executeUpdate();

            // Si venía de una cita, actualizar estado de la cita a Completada
            if (idCitaAsociada != null) {
                try (PreparedStatement psCita = conn.prepareStatement("UPDATE citas SET estado = 'Completada' WHERE id = ?")) {
                    psCita.setInt(1, idCitaAsociada);
                    psCita.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this,
                "✔ Servicio prestado registrado con éxito.\n" +
                "Comisión generada para " + emp.nombre + ": $" + String.format("%,.0f", comision),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
            cargarTablaServiciosRealizados();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar servicio realizado: " + e.getMessage());
        }
    }

    private void cargarTablaServiciosRealizados() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id, fecha, nombre_empleada, nombre_servicio, nombre_cliente, valor_cobrado, porcentaje_comision, comision_calculada, estado_liquidacion " +
                     "FROM servicios_realizados ORDER BY fecha DESC, id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getDate("fecha"),
                    rs.getString("nombre_empleada"),
                    rs.getString("nombre_servicio"),
                    rs.getString("nombre_cliente"),
                    String.format("$%,.0f", rs.getDouble("valor_cobrado")),
                    String.format("%.0f%%", rs.getDouble("porcentaje_comision")),
                    String.format("$%,.0f", rs.getDouble("comision_calculada")),
                    rs.getString("estado_liquidacion")
                });
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    private void limpiarFormulario() {
        idCitaAsociada = null;
        txtCliente.setText("");
        txtFecha.setText(LocalDate.now().toString());
        if (cboServicio.getItemCount() > 0) {
            ServicioComboItem s = cboServicio.getItemAt(0);
            txtValorCobrado.setText(String.format("%.0f", s.precio));
        }
        recalcularComision();
    }
}
