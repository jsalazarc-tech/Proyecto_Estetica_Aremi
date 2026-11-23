package view;

import controller.EmpleadoController;
import controller.ServicioController;
import controller.ServicioRealizadoController;
import model.Empleado;
import model.Servicio;
import model.ServicioRealizado;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista de Servicios Efectivamente Prestados (MVC - Vista).
 * Conexión delegada en ServicioRealizadoController, EmpleadoController y ServicioController.
 * Cero SQL en esta interfaz.
 */
public class ServiciosRealizadosGUI extends JFrame {
    private final ServicioRealizadoController realizadoController;
    private final EmpleadoController empleadoController;
    private final ServicioController servicioController;

    private JComboBox<Empleado> cboEmpleada;
    private JComboBox<Servicio> cboServicio;
    private JTextField txtCliente, txtValorCobrado, txtPorcentajeComision, txtComisionCalculada;
    private JTextField txtFecha;
    private JTable tablaRealizados;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar, btnRefrescar, btnLimpiar;

    private Integer idCitaAsociada = null;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public ServiciosRealizadosGUI() {
        this(null, "", "", "", null);
    }

    public ServiciosRealizadosGUI(Integer idCita, String cliente, String servicio, String empleada, Date fecha) {
        this.realizadoController = new ServicioRealizadoController();
        this.empleadoController = new EmpleadoController();
        this.servicioController = new ServicioController();
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

        // Encabezado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ROSA_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblTitulo = new JLabel("Registro y Control de Servicios Prestados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JLabel lblSub = new JLabel("Base para la liquidación de comisiones del personal");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(255, 235, 240));
        headerPanel.add(lblSub, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario Izquierdo
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
        txtPorcentajeComision.setBackground(new Color(245, 245, 245));

        txtComisionCalculada = new JTextField(15);
        txtComisionCalculada.setEditable(false);
        txtComisionCalculada.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtComisionCalculada.setForeground(new Color(46, 139, 87));
        txtComisionCalculada.setBackground(new Color(240, 255, 240));

        int row = 0;
        addFormRow(formPanel, "Empleada que atendió *:", cboEmpleada, gbc, row++);
        addFormRow(formPanel, "Servicio realizado *:", cboServicio, gbc, row++);
        addFormRow(formPanel, "Cliente atendido *:", txtCliente, gbc, row++);
        addFormRow(formPanel, "Fecha (AAAA-MM-DD) *:", txtFecha, gbc, row++);
        addFormRow(formPanel, "Valor Cobrado ($ COP) *:", txtValorCobrado, gbc, row++);
        addFormRow(formPanel, "Porcentaje Comisión:", txtPorcentajeComision, gbc, row++);
        addFormRow(formPanel, "Comisión a Pagar ($):", txtComisionCalculada, gbc, row++);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setOpaque(false);
        btnRegistrar = new JButton("Guardar Servicio Realizado");
        btnRegistrar.setBackground(ROSA_OSCURO);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(160, 160, 160));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);

        btnPanel.add(btnRegistrar);
        btnPanel.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Tabla Derecha
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setOpaque(false);

        JPanel topTable = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topTable.setOpaque(false);
        btnRefrescar = new JButton("Refrescar");
        topTable.add(new JLabel("Historial de Servicios Realizados:"));
        topTable.add(btnRefrescar);
        tablePanel.add(topTable, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Empleada", "Servicio", "Cliente", "Valor Cobrado", "Comisión %", "Comisión $", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaRealizados = new JTable(modeloTabla);
        tablaRealizados.setRowHeight(24);
        tablePanel.add(new JScrollPane(tablaRealizados), BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel);

        // Cálculos automáticos al seleccionar
        cboEmpleada.addActionListener(e -> recalcular());
        cboServicio.addActionListener(e -> {
            Servicio s = (Servicio) cboServicio.getSelectedItem();
            if (s != null && s.getId() > 0) {
                txtValorCobrado.setText(String.format("%.0f", s.getPrecio()));
            }
            recalcular();
        });

        txtValorCobrado.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                recalcular();
            }
        });

        btnRegistrar.addActionListener(e -> registrarServicio());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRefrescar.addActionListener(e -> cargarTablaServiciosRealizados());
    }

    private void addFormRow(JPanel panel, String label, JComponent comp, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.40;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.60;
        panel.add(comp, gbc);
    }

    private void cargarCombos() {
        cboEmpleada.removeAllItems();
        try {
            List<Empleado> emps = empleadoController.listarActivas();
            for (Empleado e : emps) cboEmpleada.addItem(e);
        } catch (Exception ignored) {}

        cboServicio.removeAllItems();
        try {
            List<Servicio> servs = servicioController.listarActivos();
            for (Servicio s : servs) cboServicio.addItem(s);
        } catch (Exception ignored) {}
    }

    private void recalcular() {
        Empleado emp = (Empleado) cboEmpleada.getSelectedItem();
        if (emp != null) {
            txtPorcentajeComision.setText(String.format("%.0f%%", emp.getPorcentajeComision()));
            try {
                double val = Double.parseDouble(txtValorCobrado.getText().trim());
                double com = emp.calcularComision(val);
                txtComisionCalculada.setText("$ " + String.format("%,.0f", com));
            } catch (Exception e) {
                txtComisionCalculada.setText("$ 0");
            }
        }
    }

    private void precargarDesdeCita(String cliente, String servicio, String empleada, Date fecha) {
        txtCliente.setText(cliente);
        if (fecha != null) txtFecha.setText(fecha.toString());

        for (int i = 0; i < cboEmpleada.getItemCount(); i++) {
            if (cboEmpleada.getItemAt(i).getNombre().equalsIgnoreCase(empleada)) {
                cboEmpleada.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cboServicio.getItemCount(); i++) {
            if (cboServicio.getItemAt(i).getNombre().equalsIgnoreCase(servicio)) {
                cboServicio.setSelectedIndex(i);
                break;
            }
        }
    }

    private void registrarServicio() {
        Empleado emp = (Empleado) cboEmpleada.getSelectedItem();
        Servicio srv = (Servicio) cboServicio.getSelectedItem();
        String cli = txtCliente.getText().trim();
        String fStr = txtFecha.getText().trim();
        String valStr = txtValorCobrado.getText().trim();

        if (emp == null || srv == null || cli.isEmpty() || fStr.isEmpty() || valStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date fDate = Date.valueOf(fStr);
            double valor = Double.parseDouble(valStr);

            realizadoController.registrarServicioRealizado(
                idCitaAsociada, emp.getId(), emp.getNombre(),
                srv.getId(), srv.getNombre(), null, cli,
                fDate, valor, emp.getPorcentajeComision()
            );

            JOptionPane.showMessageDialog(this, "✔ Servicio registrado exitosamente.");
            limpiarFormulario();
            cargarTablaServiciosRealizados();

        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTablaServiciosRealizados() {
        modeloTabla.setRowCount(0);
        try {
            List<ServicioRealizado> lista = realizadoController.listarTodos();
            for (ServicioRealizado sr : lista) {
                modeloTabla.addRow(new Object[]{
                    sr.getId(),
                    sr.getFecha(),
                    sr.getNombreEmpleada(),
                    sr.getNombreServicio(),
                    sr.getNombreCliente(),
                    "$" + String.format("%,.0f", sr.getValorCobrado()),
                    String.format("%.0f%%", sr.getPorcentajeComision()),
                    "$" + String.format("%,.0f", sr.getComisionCalculada()),
                    sr.getEstadoLiquidacion()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar historial: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        idCitaAsociada = null;
        txtCliente.setText("");
        txtValorCobrado.setText("");
        txtComisionCalculada.setText("");
        txtFecha.setText(LocalDate.now().toString());
    }
}
