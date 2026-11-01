import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GestionEmpleadasGUI extends JFrame {
    private JTextField txtNombre, txtTelefono, txtEspecialidad, txtComision, txtUsername;
    private JCheckBox chkActivo;
    private JTable tablaEmpleadas;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar;
    private int empleadaSeleccionadaId = -1;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public GestionEmpleadasGUI() {
        setTitle("💅 Personal y Comisiones - Salón AREMI");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        cargarEmpleadas();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Encabezado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ROSA_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblTitulo = new JLabel("Administración de Empleadas y Porcentajes de Comisión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JLabel lblInfo = new JLabel("Configuración: Ashley 20% | Monika 30% | Veronika 50%");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(255, 235, 240));
        headerPanel.add(lblInfo, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(253, 245, 248));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            "Ficha de la Empleada",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), ROSA_OSCURO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        txtNombre = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtEspecialidad = new JTextField(15);
        txtComision = new JTextField(15); // Ej: 20
        txtUsername = new JTextField(15);
        chkActivo = new JCheckBox("Empleada Activa", true);
        chkActivo.setOpaque(false);

        int row = 0;
        addFormField(formPanel, "Nombre *:", txtNombre, gbc, row++);
        addFormField(formPanel, "Teléfono:", txtTelefono, gbc, row++);
        addFormField(formPanel, "Especialidad *:", txtEspecialidad, gbc, row++);
        addFormField(formPanel, "Porcentaje Comisión (%) *:", txtComision, gbc, row++);
        addFormField(formPanel, "Usuario del Sistema:", txtUsername, gbc, row++);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(chkActivo, gbc);

        // Botones
        JPanel btnPanelForm = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanelForm.setOpaque(false);
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Actualizar");
        btnEliminar = new JButton("Inactivar / Borrar");
        btnLimpiar = new JButton("Limpiar");

        btnGuardar.setBackground(ROSA_OSCURO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);
        btnEditar.setBackground(new Color(70, 130, 180));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setOpaque(true);
        btnEditar.setBorderPainted(false);
        btnEliminar.setBackground(new Color(220, 70, 70));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnLimpiar.setBackground(new Color(220, 220, 220));
        btnLimpiar.setForeground(Color.BLACK);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);

        btnPanelForm.add(btnGuardar);
        btnPanelForm.add(btnEditar);
        btnPanelForm.add(btnEliminar);
        btnPanelForm.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        formPanel.add(btnPanelForm, gbc);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Teléfono", "Especialidad", "Comisión", "Usuario", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaEmpleadas = new JTable(modeloTabla);
        tablaEmpleadas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleadas.setRowHeight(24);
        tablaEmpleadas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaEmpleadas.getTableHeader().setBackground(new Color(245, 230, 238));

        tablaEmpleadas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaEmpleadas.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaEmpleadas.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaEmpleadas.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaEmpleadas.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaEmpleadas.getColumnModel().getColumn(5).setPreferredWidth(90);
        tablaEmpleadas.getColumnModel().getColumn(6).setPreferredWidth(70);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(new JScrollPane(tablaEmpleadas), BorderLayout.CENTER);

        JPanel contentSplit = new JPanel(new BorderLayout(15, 10));
        contentSplit.setBackground(Color.WHITE);
        contentSplit.add(formPanel, BorderLayout.WEST);
        contentSplit.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentSplit, BorderLayout.CENTER);
        add(mainPanel);

        // Acciones
        btnGuardar.addActionListener(e -> guardarEmpleada());
        btnEditar.addActionListener(e -> editarEmpleada());
        btnEliminar.addActionListener(e -> eliminarEmpleada());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaEmpleadas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = tablaEmpleadas.getSelectedRow();
                if (sel != -1) {
                    empleadaSeleccionadaId = (int) modeloTabla.getValueAt(sel, 0);
                    txtNombre.setText((String) modeloTabla.getValueAt(sel, 1));
                    txtTelefono.setText((String) modeloTabla.getValueAt(sel, 2));
                    txtEspecialidad.setText((String) modeloTabla.getValueAt(sel, 3));
                    String comStr = (String) modeloTabla.getValueAt(sel, 4);
                    txtComision.setText(comStr.replace("%", "").trim());
                    txtUsername.setText((String) modeloTabla.getValueAt(sel, 5));
                    String estado = (String) modeloTabla.getValueAt(sel, 6);
                    chkActivo.setSelected("Activo".equalsIgnoreCase(estado));
                }
            }
        });
    }

    private void addFormField(JPanel panel, String label, JTextField field, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void cargarEmpleadas() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id, nombre, telefono, especialidad, porcentaje_comision, username, activo FROM empleadas ORDER BY nombre ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono") != null ? rs.getString("telefono") : "",
                    rs.getString("especialidad"),
                    String.format("%.0f%%", rs.getDouble("porcentaje_comision")),
                    rs.getString("username") != null ? rs.getString("username") : "",
                    rs.getInt("activo") == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando empleadas: " + e.getMessage());
        }
    }

    private void guardarEmpleada() {
        String nombre = txtNombre.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();
        String comStr = txtComision.getText().trim();

        if (nombre.isEmpty() || especialidad.isEmpty() || comStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Especialidad y Comisión son campos obligatorios.");
            return;
        }

        double comision;
        try {
            comision = Double.parseDouble(comStr);
            if (comision < 0 || comision > 100) {
                JOptionPane.showMessageDialog(this, "El porcentaje de comisión debe estar entre 0 y 100.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido para la comisión (ejemplo: 20).");
            return;
        }

        String sql = "INSERT INTO empleadas (nombre, telefono, especialidad, porcentaje_comision, username, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, txtTelefono.getText().trim());
            ps.setString(3, especialidad);
            ps.setDouble(4, comision);
            String u = txtUsername.getText().trim();
            ps.setString(5, u.isEmpty() ? null : u);
            ps.setInt(6, chkActivo.isSelected() ? 1 : 0);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Empleada registrada exitosamente.");
            limpiarFormulario();
            cargarEmpleadas();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar empleada: " + e.getMessage());
        }
    }

    private void editarEmpleada() {
        if (empleadaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una empleada de la tabla para editar.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();
        String comStr = txtComision.getText().trim();

        if (nombre.isEmpty() || especialidad.isEmpty() || comStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, Especialidad y Comisión son campos obligatorios.");
            return;
        }

        double comision;
        try {
            comision = Double.parseDouble(comStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido para la comisión.");
            return;
        }

        String sql = "UPDATE empleadas SET nombre = ?, telefono = ?, especialidad = ?, porcentaje_comision = ?, username = ?, activo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, txtTelefono.getText().trim());
            ps.setString(3, especialidad);
            ps.setDouble(4, comision);
            String u = txtUsername.getText().trim();
            ps.setString(5, u.isEmpty() ? null : u);
            ps.setInt(6, chkActivo.isSelected() ? 1 : 0);
            ps.setInt(7, empleadaSeleccionadaId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Datos de empleada actualizados.");
            limpiarFormulario();
            cargarEmpleadas();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar empleada: " + e.getMessage());
        }
    }

    private void eliminarEmpleada() {
        if (empleadaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione la empleada que desea inactivar o eliminar.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea inactivar a esta empleada? (Si no tiene citas ni servicios asociados, se eliminará)",
                "Confirmar Acción", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Intentar marcar como inactivo primero para mantener integridad referencial
                try (PreparedStatement ps = conn.prepareStatement("UPDATE empleadas SET activo = 0 WHERE id = ?")) {
                    ps.setInt(1, empleadaSeleccionadaId);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Empleada marcada como inactiva.");
                limpiarFormulario();
                cargarEmpleadas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void limpiarFormulario() {
        empleadaSeleccionadaId = -1;
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEspecialidad.setText("");
        txtComision.setText("");
        txtUsername.setText("");
        chkActivo.setSelected(true);
        tablaEmpleadas.clearSelection();
    }
}
