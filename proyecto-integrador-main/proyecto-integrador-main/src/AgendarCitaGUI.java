import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgendarCitaGUI extends JFrame {
    private JTabbedPane tabbedPane;

    // Componentes Tab 1: Agendar
    private JComboBox<ClienteItem> cboClientes;
    private JTextField txtClienteManual, txtTelefonoManual;
    private JComboBox<ServicioItem> cboServicios;
    private JComboBox<EmpleadoItem> cboEmpleadas;
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

    // Helpers
    private static class ClienteItem {
        int id;
        String nombre;
        String telefono;
        ClienteItem(int id, String n, String t) { this.id = id; this.nombre = n; this.telefono = t; }
        @Override public String toString() { return nombre + " (" + telefono + ")"; }
    }

    private static class ServicioItem {
        int id;
        String nombre;
        double precio;
        ServicioItem(int id, String n, double p) { this.id = id; this.nombre = n; this.precio = p; }
        @Override public String toString() { return nombre + " - $" + String.format("%,.0f", precio); }
    }

    private static class EmpleadoItem {
        int id;
        String nombre;
        String especialidad;
        double comision;
        EmpleadoItem(int id, String n, String esp, double com) { this.id = id; this.nombre = n; this.especialidad = esp; this.comision = com; }
        @Override public String toString() { return nombre + " (" + especialidad + ")"; }
    }

    public AgendarCitaGUI() {
        setTitle("📅 Control de Citas y Agenda - Salón AREMI");
        setSize(950, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        cargarCombos();
        cargarCitas();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel tabAgendar = createTabAgendar();
        JPanel tabConsultar = createTabConsultar();

        tabbedPane.addTab("  ➕ Agendar Nueva Cita  ", tabAgendar);
        tabbedPane.addTab("  📋 Agenda y Control de Citas  ", tabConsultar);

        add(tabbedPane);
    }

    private JPanel createTabAgendar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ROSA_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblTitulo = new JLabel("Reserva de Citas con Validación de Horarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(253, 245, 248));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        cboClientes = new JComboBox<>();
        cboClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtClienteManual = new JTextField(15);
        txtTelefonoManual = new JTextField(15);

        cboServicios = new JComboBox<>();
        cboServicios.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cboEmpleadas = new JComboBox<>();
        cboEmpleadas.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Fecha
        JPanel fechaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        fechaPanel.setOpaque(false);

        LocalDate hoy = LocalDate.now();
        Integer[] dias = new Integer[31];
        for (int i = 1; i <= 31; i++) dias[i - 1] = i;
        cboDia = new JComboBox<>(dias);
        cboDia.setSelectedItem(hoy.getDayOfMonth());

        Integer[] meses = new Integer[12];
        for (int i = 1; i <= 12; i++) meses[i - 1] = i;
        cboMes = new JComboBox<>(meses);
        cboMes.setSelectedItem(hoy.getMonthValue());

        Integer[] anos = new Integer[5];
        for (int i = 0; i < 5; i++) anos[i] = hoy.getYear() + i;
        cboAno = new JComboBox<>(anos);
        cboAno.setSelectedItem(hoy.getYear());

        fechaPanel.add(new JLabel("Día:"));
        fechaPanel.add(cboDia);
        fechaPanel.add(new JLabel("Mes:"));
        fechaPanel.add(cboMes);
        fechaPanel.add(new JLabel("Año:"));
        fechaPanel.add(cboAno);

        // Horas disponibles
        String[] horas = {
            "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        };
        cboHora = new JComboBox<>(horas);

        txtNotas = new JTextArea(2, 20);
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotas.setLineWrap(true);

        int row = 0;
        addFormRow(formPanel, "Seleccionar Cliente Registrado:", cboClientes, gbc, row++);
        addFormRow(formPanel, "O Nombre Cliente (si es nuevo):", txtClienteManual, gbc, row++);
        addFormRow(formPanel, "Teléfono del Cliente:", txtTelefonoManual, gbc, row++);
        addFormRow(formPanel, "Servicio Solicitado *:", cboServicios, gbc, row++);
        addFormRow(formPanel, "Empleada Asignada *:", cboEmpleadas, gbc, row++);
        addFormRow(formPanel, "Fecha de la Cita *:", fechaPanel, gbc, row++);
        addFormRow(formPanel, "Hora de la Cita *:", cboHora, gbc, row++);
        addFormRow(formPanel, "Notas / Observaciones:", new JScrollPane(txtNotas), gbc, row++);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);

        btnAgendar = new JButton("Validar Disponibilidad y Agendar");
        btnAgendar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgendar.setBackground(ROSA_OSCURO);
        btnAgendar.setForeground(Color.WHITE);
        btnAgendar.setOpaque(true);
        btnAgendar.setBorderPainted(false);
        btnAgendar.setPreferredSize(new Dimension(270, 40));

        btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLimpiar.setBackground(new Color(230, 230, 230));
        btnLimpiar.setForeground(Color.BLACK);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setPreferredSize(new Dimension(160, 40));

        btnPanel.add(btnAgendar);
        btnPanel.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Eventos
        cboClientes.addActionListener(e -> {
            ClienteItem cli = (ClienteItem) cboClientes.getSelectedItem();
            if (cli != null && cli.id != 0) {
                txtClienteManual.setText(cli.nombre);
                txtTelefonoManual.setText(cli.telefono);
            }
        });

        btnAgendar.addActionListener(e -> agendarCitaConValidacion());
        btnLimpiar.addActionListener(e -> limpiarCamposAgendar());

        return panel;
    }

    private JPanel createTabConsultar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBackground(new Color(250, 245, 248));
        filterPanel.setBorder(BorderFactory.createLineBorder(ROSA_CLARO, 1));

        filterPanel.add(new JLabel("Estado:"));
        cboFiltroEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "Completada", "Cancelada"});
        filterPanel.add(cboFiltroEstado);

        filterPanel.add(new JLabel("Empleada:"));
        cboFiltroEmpleada = new JComboBox<>(new String[]{"Todas"});
        filterPanel.add(cboFiltroEmpleada);

        btnRefrescar = new JButton("Filtrar / Refrescar");
        btnRefrescar.setBackground(ROSA_OSCURO);
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setOpaque(true);
        btnRefrescar.setBorderPainted(false);
        filterPanel.add(btnRefrescar);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"ID", "Fecha", "Hora", "Cliente", "Teléfono", "Servicio", "Empleada", "Estado", "Notas"};
        modeloCitas = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCitas = new JTable(modeloCitas);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCitas.setRowHeight(24);
        tablaCitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCitas.getTableHeader().setBackground(new Color(245, 230, 238));

        tablaCitas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaCitas.getColumnModel().getColumn(1).setPreferredWidth(85);
        tablaCitas.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaCitas.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaCitas.getColumnModel().getColumn(4).setPreferredWidth(95);
        tablaCitas.getColumnModel().getColumn(5).setPreferredWidth(140);
        tablaCitas.getColumnModel().getColumn(6).setPreferredWidth(110);
        tablaCitas.getColumnModel().getColumn(7).setPreferredWidth(85);
        tablaCitas.getColumnModel().getColumn(8).setPreferredWidth(120);

        panel.add(new JScrollPane(tablaCitas), BorderLayout.CENTER);

        // Barra inferior de acciones
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
        cboClientes.addItem(new ClienteItem(0, "-- Seleccione de la lista o escriba abajo --", ""));
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, telefono FROM clientes ORDER BY nombre ASC")) {
            while (rs.next()) {
                cboClientes.addItem(new ClienteItem(rs.getInt("id"), rs.getString("nombre"), rs.getString("telefono")));
            }
        } catch (Exception e) {
            // ignore
        }

        cboServicios.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, precio FROM servicios WHERE activo = 1 ORDER BY nombre ASC")) {
            while (rs.next()) {
                cboServicios.addItem(new ServicioItem(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio")));
            }
        } catch (Exception e) {
            // ignore
        }

        cboEmpleadas.removeAllItems();
        cboFiltroEmpleada.removeAllItems();
        cboFiltroEmpleada.addItem("Todas");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, especialidad, porcentaje_comision FROM empleadas WHERE activo = 1 ORDER BY nombre ASC")) {
            while (rs.next()) {
                EmpleadoItem emp = new EmpleadoItem(rs.getInt("id"), rs.getString("nombre"), rs.getString("especialidad"), rs.getDouble("porcentaje_comision"));
                cboEmpleadas.addItem(emp);
                cboFiltroEmpleada.addItem(rs.getString("nombre"));
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void agendarCitaConValidacion() {
        String nombre = txtClienteManual.getText().trim();
        String telefono = txtTelefonoManual.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe especificar el nombre y teléfono del cliente.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ServicioItem serv = (ServicioItem) cboServicios.getSelectedItem();
        EmpleadoItem emp = (EmpleadoItem) cboEmpleadas.getSelectedItem();

        if (serv == null || emp == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un servicio y una empleada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fechaStr = String.format("%d-%02d-%02d",
                cboAno.getSelectedItem(),
                cboMes.getSelectedItem(),
                cboDia.getSelectedItem());
        Date fecha = Date.valueOf(fechaStr);
        String hora = (String) cboHora.getSelectedItem();

        // RF-06: VALIDACIÓN DE DISPONIBILIDAD (EVITAR CRUCE DE HORARIOS)
        String checkSql = "SELECT COUNT(*) FROM citas WHERE id_empleada = ? AND fecha = ? AND hora = ? AND estado = 'Pendiente'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {

            ps.setInt(1, emp.id);
            ps.setDate(2, fecha);
            ps.setString(3, hora);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "⚠ CRUCE DE HORARIO DETECTADO:\n\n" +
                        "La empleada " + emp.nombre + " ya tiene una cita asignada el día " + fechaStr + " a las " + hora + ".\n" +
                        "Por favor seleccione otra hora, otra fecha u otra empleada disponible.",
                        "Horario No Disponible",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Registrar cliente si no existe en la BD
            Integer idCliente = null;
            ClienteItem cliItem = (ClienteItem) cboClientes.getSelectedItem();
            if (cliItem != null && cliItem.id != 0) {
                idCliente = cliItem.id;
            } else {
                // Insertar nuevo cliente automáticamente
                String insCli = "INSERT INTO clientes (nombre, telefono) VALUES (?, ?)";
                try (PreparedStatement psCli = conn.prepareStatement(insCli, Statement.RETURN_GENERATED_KEYS)) {
                    psCli.setString(1, nombre);
                    psCli.setString(2, telefono);
                    psCli.executeUpdate();
                    try (ResultSet rk = psCli.getGeneratedKeys()) {
                        if (rk.next()) idCliente = rk.getInt(1);
                    }
                }
            }

            // Guardar Cita
            DatabaseConnection.guardarCitaCompleta(
                idCliente, nombre, telefono,
                serv.id, serv.nombre,
                emp.id, emp.nombre,
                fecha, hora, "Pendiente", txtNotas.getText().trim()
            );

            JOptionPane.showMessageDialog(this,
                "✨ ¡Cita agendada exitosamente!\n\n" +
                "Cliente: " + nombre + "\n" +
                "Servicio: " + serv.nombre + "\n" +
                "Atiende: " + emp.nombre + " (" + emp.especialidad + ")\n" +
                "Fecha y Hora: " + fechaStr + " " + hora,
                "Confirmación de Cita",
                JOptionPane.INFORMATION_MESSAGE);

            limpiarCamposAgendar();
            cargarCombos();
            cargarCitas();
            tabbedPane.setSelectedIndex(1); // ir a la lista

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al agendar cita: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarCitas() {
        modeloCitas.setRowCount(0);
        String estadoFiltro = (String) cboFiltroEstado.getSelectedItem();
        String empFiltro = (String) cboFiltroEmpleada.getSelectedItem();

        StringBuilder sql = new StringBuilder("SELECT id, fecha, hora, nombre_cliente, telefono_cliente, nombre_servicio, nombre_empleada, estado, notas FROM citas WHERE 1=1 ");
        if (estadoFiltro != null && !estadoFiltro.equals("Todos")) {
            sql.append(" AND estado = ? ");
        }
        if (empFiltro != null && !empFiltro.equals("Todas")) {
            sql.append(" AND nombre_empleada = ? ");
        }
        sql.append(" ORDER BY fecha DESC, hora ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int pIdx = 1;
            if (estadoFiltro != null && !estadoFiltro.equals("Todos")) {
                ps.setString(pIdx++, estadoFiltro);
            }
            if (empFiltro != null && !empFiltro.equals("Todas")) {
                ps.setString(pIdx++, empFiltro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloCitas.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getDate("fecha"),
                        rs.getString("hora"),
                        rs.getString("nombre_cliente"),
                        rs.getString("telefono_cliente"),
                        rs.getString("nombre_servicio"),
                        rs.getString("nombre_empleada"),
                        rs.getString("estado"),
                        rs.getString("notas") != null ? rs.getString("notas") : ""
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando citas: " + e.getMessage());
        }
    }

    private void cancelarCitaSeleccionada() {
        int sel = tablaCitas.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione la cita que desea cancelar en la tabla.");
            return;
        }

        int idCita = (int) modeloCitas.getValueAt(sel, 0);
        String estado = (String) modeloCitas.getValueAt(sel, 7);

        if ("Cancelada".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "Esta cita ya se encuentra cancelada.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de cancelar esta cita? El horario quedará liberado para nuevas reservas.",
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE citas SET estado = 'Cancelada' WHERE id = ?")) {
                ps.setInt(1, idCita);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "La cita ha sido cancelada y el horario fue liberado.");
                cargarCitas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cancelar cita: " + e.getMessage());
            }
        }
    }

    private void marcarServicioRealizadoDesdeCita() {
        int sel = tablaCitas.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la lista.");
            return;
        }

        int idCita = (int) modeloCitas.getValueAt(sel, 0);
        String estado = (String) modeloCitas.getValueAt(sel, 7);

        if ("Completada".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "Esta cita ya fue marcada como realizada anteriormente.");
            return;
        }
        if ("Cancelada".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "Una cita cancelada no se puede marcar como realizada.");
            return;
        }

        String cliente = (String) modeloCitas.getValueAt(sel, 3);
        String servicio = (String) modeloCitas.getValueAt(sel, 5);
        String empleada = (String) modeloCitas.getValueAt(sel, 6);
        Date fecha = (Date) modeloCitas.getValueAt(sel, 1);

        // Abrir ventana de registro de servicio realizado con datos precargados
        ServiciosRealizadosGUI form = new ServiciosRealizadosGUI(idCita, cliente, servicio, empleada, fecha);
        form.setVisible(true);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cargarCitas();
            }
        });
    }

    private void limpiarCamposAgendar() {
        cboClientes.setSelectedIndex(0);
        txtClienteManual.setText("");
        txtTelefonoManual.setText("");
        txtNotas.setText("");
    }
}
