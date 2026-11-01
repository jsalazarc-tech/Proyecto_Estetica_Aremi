import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InterfazClientes extends JFrame {
    private JTextField txtNombre, txtTelefono, txtDireccion, txtEmail, txtBuscar;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnLimpiar, btnBuscar;
    private int clienteSeleccionadoId = -1;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public InterfazClientes() {
        setTitle("👥 Gestión de Clientes - Salón AREMI");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        cargarClientes("");
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Encabezado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ROSA_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel lblTitulo = new JLabel("Directorio y Registro de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel Izquierdo: Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(253, 245, 248));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            "Datos del Cliente",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), ROSA_OSCURO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        txtNombre = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtDireccion = new JTextField(15);
        txtEmail = new JTextField(15);

        int row = 0;
        addFormField(formPanel, "Nombre Completo *:", txtNombre, gbc, row++);
        addFormField(formPanel, "Teléfono *:", txtTelefono, gbc, row++);
        addFormField(formPanel, "Dirección:", txtDireccion, gbc, row++);
        addFormField(formPanel, "Correo Electrónico:", txtEmail, gbc, row++);

        // Botones del formulario
        JPanel btnPanelForm = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanelForm.setOpaque(false);
        btnAgregar = new JButton("Guardar Nuevo");
        btnEditar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnAgregar.setBackground(ROSA_OSCURO);
        btnAgregar.setForeground(Color.WHITE);
        btnEditar.setBackground(new Color(70, 130, 180));
        btnEditar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(220, 70, 70));
        btnEliminar.setForeground(Color.WHITE);
        btnLimpiar.setBackground(new Color(200, 200, 200));

        btnPanelForm.add(btnAgregar);
        btnPanelForm.add(btnEditar);
        btnPanelForm.add(btnEliminar);
        btnPanelForm.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        formPanel.add(btnPanelForm, gbc);

        // Panel Central / Derecho: Búsqueda y Tabla
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(Color.WHITE);

        // Barra de búsqueda
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchBar.setBackground(Color.WHITE);
        searchBar.add(new JLabel("Buscar por Nombre o Teléfono:"));
        txtBuscar = new JTextField(18);
        btnBuscar = new JButton("Buscar");
        JButton btnVerTodos = new JButton("Ver Todos");

        searchBar.add(txtBuscar);
        searchBar.add(btnBuscar);
        searchBar.add(btnVerTodos);
        tablePanel.add(searchBar, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Teléfono", "Dirección", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(24);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaClientes.getTableHeader().setBackground(new Color(245, 230, 238));

        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(140);

        tablePanel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        // Split o BorderLayout
        JPanel contentGrid = new JPanel(new BorderLayout(15, 10));
        contentGrid.setBackground(Color.WHITE);
        contentGrid.add(formPanel, BorderLayout.WEST);
        contentGrid.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentGrid, BorderLayout.CENTER);
        add(mainPanel);

        // Eventos
        btnAgregar.addActionListener(e -> agregarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> cargarClientes(txtBuscar.getText().trim()));
        btnVerTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarClientes("");
        });

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selected = tablaClientes.getSelectedRow();
                if (selected != -1) {
                    clienteSeleccionadoId = (int) modeloTabla.getValueAt(selected, 0);
                    txtNombre.setText((String) modeloTabla.getValueAt(selected, 1));
                    txtTelefono.setText((String) modeloTabla.getValueAt(selected, 2));
                    txtDireccion.setText((String) modeloTabla.getValueAt(selected, 3));
                    txtEmail.setText((String) modeloTabla.getValueAt(selected, 4));
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

    private void cargarClientes(String filtro) {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id, nombre, telefono, direccion, email FROM clientes";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE nombre LIKE ? OR telefono LIKE ?";
        }
        sql += " ORDER BY nombre ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (filtro != null && !filtro.isEmpty()) {
                ps.setString(1, "%" + filtro + "%");
                ps.setString(2, "%" + filtro + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloTabla.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("email")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage());
        }
    }

    private void agregarCliente() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y el teléfono son campos obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO clientes (nombre, telefono, direccion, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, direccion);
            ps.setString(4, email);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.");
            limpiarFormulario();
            cargarClientes("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar cliente: " + e.getMessage());
        }
    }

    private void editarCliente() {
        if (clienteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un cliente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String email = txtEmail.getText().trim();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y el teléfono son obligatorios.");
            return;
        }

        String sql = "UPDATE clientes SET nombre = ?, telefono = ?, direccion = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, direccion);
            ps.setString(4, email);
            ps.setInt(5, clienteSeleccionadoId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
            limpiarFormulario();
            cargarClientes("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar cliente: " + e.getMessage());
        }
    }

    private void eliminarCliente() {
        if (clienteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione el cliente que desea eliminar.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este cliente?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM clientes WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, clienteSeleccionadoId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                limpiarFormulario();
                cargarClientes("");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar el cliente porque tiene registros asociados:\n" + e.getMessage());
            }
        }
    }

    private void limpiarFormulario() {
        clienteSeleccionadoId = -1;
        txtNombre.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtEmail.setText("");
        tablaClientes.clearSelection();
    }
}
