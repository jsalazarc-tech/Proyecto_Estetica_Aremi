package view;

import controller.CitaController;
import controller.ClienteController;
import controller.EmpleadoController;
import controller.ServicioController;
import model.Cita;
import model.Cliente;
import model.Empleado;
import model.Servicio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista de Agendamiento y Control de Citas (MVC - Vista).
 * Delega en CitaController, ClienteController, ServicioController y EmpleadoController.
 * Cero SQL en esta clase.
 */
public class AgendarCitaGUI extends JFrame {
    private final CitaController citaController;
    private final ClienteController clienteController;
    private final ServicioController servicioController;
    private final EmpleadoController empleadoController;

    private JTabbedPane tabbedPane;

    // Componentes Tab 1: Agendar
    private JComboBox<Cliente> cboClientes;
    private JTextField txtClienteManual, txtTelefonoManual;
    private JComboBox<Servicio> cboServicios;
    private JComboBox<Empleado> cboEmpleadas;
    private JComboBox<Integer> cboDia, cboMes, cboAno;
    private JComboBox<String> cboHora;
    private JTextArea txtNotas;
    private JButton btnAgendar, btnLimpiar;

    // Componentes Tab 2: Agenda / Consultas
    private JTable tablaCitas;
    private DefaultTableModel modeloCitas;
    private JComboBox<String> cboFiltroEstado;
    private JComboBox<String> cboFiltroEmpleada;
    private JButton btnRefrescar, btnCancelarCita, btnMarcarRealizada;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public AgendarCitaGUI() {
        this.citaController = new CitaController();
        this.clienteController = new ClienteController();
        this.servicioController = new ServicioController();
        this.empleadoController = new EmpleadoController();

        setTitle("📅 Control de Citas y Agenda - Salón AREMI");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        cargarCombos();
        cargarCitas();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Formulario de Agendamiento
        JPanel panelAgendar = crearPanelAgendamiento();
        tabbedPane.addTab("  ➕ Agendar Nueva Cita  ", panelAgendar);

        // Tab 2: Consultar Agenda
        JPanel panelConsulta = crearPanelConsulta();
        tabbedPane.addTab("  📋 Agenda y Control de Citas  ", panelConsulta);

        add(tabbedPane);
    }

    private JPanel crearPanelAgendamiento() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ROSA_OSCURO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel lblTit = new JLabel("Reserva de Citas con Validación de Horarios");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(Color.WHITE);
        header.add(lblTit, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(253, 245, 248));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 12, 6, 12);

        cboClientes = new JComboBox<>();
        txtClienteManual = new JTextField(18);
        txtTelefonoManual = new JTextField(18);
        cboServicios = new JComboBox<>();
        cboEmpleadas = new JComboBox<>();

        LocalDate hoy = LocalDate.now();
        cboDia = new JComboBox<>();
        for (int i = 1; i <= 31; i++) cboDia.addItem(i);
        cboDia.setSelectedItem(hoy.getDayOfMonth());

        cboMes = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cboMes.addItem(i);
        cboMes.setSelectedItem(hoy.getMonthValue());

        cboAno = new JComboBox<>(new Integer[]{hoy.getYear(), hoy.getYear() + 1});

        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelFecha.setOpaque(false);
        panelFecha.add(new JLabel("Día:"));
        panelFecha.add(cboDia);
        panelFecha.add(new JLabel("Mes:"));
        panelFecha.add(cboMes);
        panelFecha.add(new JLabel("Año:"));
        panelFecha.add(cboAno);

        String[] horas = {
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30", "18:00", "18:30"
        };
        cboHora = new JComboBox<>(horas);

        txtNotas = new JTextArea(3, 18);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        JScrollPane scrollNotas = new JScrollPane(txtNotas);

        int r = 0;
        addFormRow(form, "Seleccionar Cliente Registrado:", cboClientes, gbc, r++);
        addFormRow(form, "O Nombre Cliente (si es nuevo):", txtClienteManual, gbc, r++);
        addFormRow(form, "Teléfono del Cliente:", txtTelefonoManual, gbc, r++);
        addFormRow(form, "Servicio Solicitado *:", cboServicios, gbc, r++);
        addFormRow(form, "Empleada Asignada *:", cboEmpleadas, gbc, r++);
        addFormRow(form, "Fecha de la Cita *:", panelFecha, gbc, r++);
        addFormRow(form, "Hora de la Cita *:", cboHora, gbc, r++);
        addFormRow(form, "Notas / Observaciones:", scrollNotas, gbc, r++);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);
        btnAgendar = new JButton("Validar Disponibilidad y Agendar");
        btnAgendar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgendar.setBackground(ROSA_OSCURO);
        btnAgendar.setForeground(Color.WHITE);
        btnAgendar.setOpaque(true);
        btnAgendar.setBorderPainted(false);

        btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimpiar.setBackground(new Color(160, 160, 160));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);

        btnPanel.add(btnAgendar);
        btnPanel.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = r++;
        gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        panel.add(form, BorderLayout.CENTER);

        // Selección automática cliente
        cboClientes.addActionListener(e -> {
            Cliente sel = (Cliente) cboClientes.getSelectedItem();
            if (sel != null && sel.getId() > 0) {
                txtClienteManual.setText(sel.getNombre());
                txtTelefonoManual.setText(sel.getTelefono());
            }
        });

        btnAgendar.addActionListener(e -> agendarCitaConValidacion());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        return panel;
    }

    private JPanel crearPanelConsulta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filtros superiores
        JPanel topFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        topFilters.setOpaque(false);

        topFilters.add(new JLabel("Estado:"));
        cboFiltroEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "Completada", "Cancelada"});
        topFilters.add(cboFiltroEstado);

        topFilters.add(new JLabel("Empleada:"));
        cboFiltroEmpleada = new JComboBox<>();
        cboFiltroEmpleada.addItem("Todas");
        topFilters.add(cboFiltroEmpleada);

        btnRefrescar = new JButton("Filtrar / Refrescar");
        btnRefrescar.setBackground(ROSA_OSCURO);
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setOpaque(true);
        btnRefrescar.setBorderPainted(false);
        topFilters.add(btnRefrescar);

        panel.add(topFilters, BorderLayout.NORTH);

        // Tabla
        modeloCitas = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Hora", "Cliente", "Teléfono", "Servicio", "Empleada", "Estado", "Notas"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaCitas = new JTable(modeloCitas);
        tablaCitas.setRowHeight(24);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaCitas), BorderLayout.CENTER);

        // Acciones inferiores
        JPanel bottomActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomActions.setBackground(Color.WHITE);

        btnMarcarRealizada = new JButton("✔ Marcar como Servicio Realizado");
        btnMarcarRealizada.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnMarcarRealizada.setBackground(new Color(46, 139, 87));
        btnMarcarRealizada.setForeground(Color.WHITE);
        btnMarcarRealizada.setOpaque(true);
        btnMarcarRealizada.setBorderPainted(false);

        btnCancelarCita = new JButton("✖ Cancelar Cita");
        btnCancelarCita.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelarCita.setBackground(new Color(205, 92, 92));
        btnCancelarCita.setForeground(Color.WHITE);
        btnCancelarCita.setOpaque(true);
        btnCancelarCita.setBorderPainted(false);

        bottomActions.add(btnMarcarRealizada);
        bottomActions.add(btnCancelarCita);
        panel.add(bottomActions, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargarCitas());
        btnCancelarCita.addActionListener(e -> cancelarCitaSeleccionada());
        btnMarcarRealizada.addActionListener(e -> marcarServicioRealizadoDesdeCita());

        return panel;
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
        cboClientes.removeAllItems();
        cboClientes.addItem(new Cliente(0, "-- Seleccione de la lista o escriba abajo --", "", ""));
        try {
            List<Cliente> clientes = clienteController.listarClientes("");
            for (Cliente c : clientes) cboClientes.addItem(c);
        } catch (Exception ignored) {}

        cboServicios.removeAllItems();
        try {
            List<Servicio> servs = servicioController.listarActivos();
            for (Servicio s : servs) cboServicios.addItem(s);
        } catch (Exception ignored) {}

        cboEmpleadas.removeAllItems();
        cboFiltroEmpleada.removeAllItems();
        boolean esAdmin = util.SeguridadManager.esAdministrador();
        Integer idActual = util.SeguridadManager.getIdEmpleadaActual();
        String userActual = util.SeguridadManager.getUsuarioActual();

        if (esAdmin) {
            cboFiltroEmpleada.addItem("Todas");
        }
        try {
            List<Empleado> emps = empleadoController.listarActivas();
            for (Empleado e : emps) {
                if (esAdmin) {
                    cboEmpleadas.addItem(e);
                    cboFiltroEmpleada.addItem(e.getNombre());
                } else {
                    boolean coincide = (idActual != null && e.getId() == idActual) ||
                                       (e.getUsername() != null && e.getUsername().equalsIgnoreCase(userActual)) ||
                                       (e.getNombre() != null && (e.getNombre().equalsIgnoreCase(userActual) || e.getNombre().toLowerCase().contains(userActual.toLowerCase())));
                    if (coincide) {
                        cboEmpleadas.addItem(e);
                        cboFiltroEmpleada.addItem(e.getNombre());
                    }
                }
            }
            if (!esAdmin && cboEmpleadas.getItemCount() > 0) {
                cboEmpleadas.setSelectedIndex(0);
                cboEmpleadas.setEnabled(false);
                cboFiltroEmpleada.setSelectedIndex(0);
                cboFiltroEmpleada.setEnabled(false);
            }
        } catch (Exception ignored) {}
    }

    private void agendarCitaConValidacion() {
        String nombre = txtClienteManual.getText().trim();
        String telefono = txtTelefonoManual.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar el nombre y teléfono del cliente.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Servicio srv = (Servicio) cboServicios.getSelectedItem();
        Empleado emp = (Empleado) cboEmpleadas.getSelectedItem();

        if (srv == null || emp == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un servicio y una empleada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int d = (Integer) cboDia.getSelectedItem();
        int m = (Integer) cboMes.getSelectedItem();
        int y = (Integer) cboAno.getSelectedItem();
        LocalDate fecha = LocalDate.of(y, m, d);
        Date sqlDate = Date.valueOf(fecha);
        String hora = (String) cboHora.getSelectedItem();
        String notas = txtNotas.getText().trim();

        Cliente cliSel = (Cliente) cboClientes.getSelectedItem();
        Integer idCli = (cliSel != null && cliSel.getId() > 0) ? cliSel.getId() : null;

        try {
            // Invoca al Controlador que valida cruce de horario (RF-06)
            citaController.agendarCita(
                idCli, nombre, telefono,
                srv.getId(), srv.getNombre(),
                emp.getId(), emp.getNombre(),
                sqlDate, hora, notas
            );

            JOptionPane.showMessageDialog(
                this,
                "✔ Cita agendada exitosamente.\n\n" +
                "Cliente: " + nombre + "\n" +
                "Profesional: " + emp.getNombre() + " (" + emp.getEspecialidad() + ")\n" +
                "Servicio: " + srv.getNombre() + "\n" +
                "Fecha y Hora: " + fecha + " a las " + hora,
                "Cita Confirmada",
                JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            cargarCitas();
            tabbedPane.setSelectedIndex(1);

        } catch (IllegalStateException ise) {
            // Captura choque de disponibilidad de la empleada
            JOptionPane.showMessageDialog(this, ise.getMessage(), "Conflicto de Horario (RF-06)", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agendar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCitas() {
        modeloCitas.setRowCount(0);
        String fEstado = (String) cboFiltroEstado.getSelectedItem();
        String fEmp = (String) cboFiltroEmpleada.getSelectedItem();

        try {
            List<Cita> lista = citaController.listarCitas(fEstado, fEmp);
            for (Cita c : lista) {
                modeloCitas.addRow(new Object[]{
                    c.getId(),
                    c.getFecha(),
                    c.getHora(),
                    c.getNombreCliente(),
                    c.getTelefonoCliente(),
                    c.getNombreServicio(),
                    c.getNombreEmpleada(),
                    c.getEstado(),
                    c.getNotas()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar citas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarCitaSeleccionada() {
        int sel = tablaCitas.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una cita de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = (int) modeloCitas.getValueAt(sel, 0);
        String estado = (String) modeloCitas.getValueAt(sel, 7);

        if ("Cancelada".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "Esta cita ya se encuentra cancelada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this, "¿Está seguro de cancelar la cita N° " + idCita + "?\nEl horario quedará liberado.", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                citaController.cancelarCita(idCita);
                JOptionPane.showMessageDialog(this, "Cita cancelada exitosamente.");
                cargarCitas();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void marcarServicioRealizadoDesdeCita() {
        int sel = tablaCitas.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione la cita que fue atendida.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = (int) modeloCitas.getValueAt(sel, 0);
        Date fecha = (Date) modeloCitas.getValueAt(sel, 1);
        String cliente = (String) modeloCitas.getValueAt(sel, 3);
        String servicio = (String) modeloCitas.getValueAt(sel, 5);
        String empleada = (String) modeloCitas.getValueAt(sel, 6);
        String estado = (String) modeloCitas.getValueAt(sel, 7);

        if ("Cancelada".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "No es posible registrar un servicio realizado de una cita cancelada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Abre la ventana de Servicios Realizados precargando los datos de la cita
        ServiciosRealizadosGUI guiRealizado = new ServiciosRealizadosGUI(idCita, cliente, servicio, empleada, fecha);
        guiRealizado.setVisible(true);
    }

    private void limpiarFormulario() {
        cboClientes.setSelectedIndex(0);
        txtClienteManual.setText("");
        txtTelefonoManual.setText("");
        txtNotas.setText("");
        cboHora.setSelectedIndex(0);
    }
}
