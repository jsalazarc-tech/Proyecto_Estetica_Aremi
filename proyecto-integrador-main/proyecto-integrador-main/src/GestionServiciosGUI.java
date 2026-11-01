import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GestionServiciosGUI extends JFrame {
    private JTextField txtNombre, txtPrecio, txtDuracion, txtDescripcion, txtBuscar;
    private JComboBox<String> cboCategoria;
    private JTable tablaServicios;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnBuscar;
    private int servicioSeleccionadoId = -1;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public GestionServiciosGUI() {
        setTitle("✨ Catálogo de Servicios y Precios - Salón AREMI");
        setSize(920, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        cargarServicios("");
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Encabezado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ROSA_OSCURO);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        JLabel lblTitulo = new JLabel("Catálogo de Servicios y Tarifas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(253, 245, 248));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            "Detalle del Servicio",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), ROSA_OSCURO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        txtNombre = new JTextField(15);
        cboCategoria = new JComboBox<>(new String[]{
            "Uñas y Manicure", "Cejas", "Depilación", "Facial / Masajes", "General"
        });
        txtPrecio = new JTextField(15);
        txtDuracion = new JTextField("60", 15);
        txtDescripcion = new JTextField(15);

        int row = 0;
        addFormField(formPanel, "Nombre del Servicio *:", txtNombre, gbc, row++);
        addFormField(formPanel, "Categoría / Especialidad *:", cboCategoria, gbc, row++);
        addFormField(formPanel, "Precio ($ COP) *:", txtPrecio, gbc, row++);
        addFormField(formPanel, "Duración (minutos):", txtDuracion, gbc, row++);
        addFormField(formPanel, "Descripción:", txtDescripcion, gbc, row++);

        // Botones formulario
        JPanel btnPanelForm = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanelForm.setOpaque(false);
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnGuardar.setBackground(ROSA_OSCURO);
        btnGuardar.setForeground(Color.WHITE);
        btnEditar.setBackground(new Color(70, 130, 180));
        btnEditar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(220, 70, 70));
        btnEliminar.setForeground(Color.WHITE);
        btnLimpiar.setBackground(new Color(200, 200, 200));

        btnPanelForm.add(btnGuardar);
        btnPanelForm.add(btnEditar);
        btnPanelForm.add(btnEliminar);
        btnPanelForm.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        formPanel.add(btnPanelForm, gbc);

        // Tabla y búsqueda
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(Color.WHITE);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchBar.setBackground(Color.WHITE);
        searchBar.add(new JLabel("Filtrar por Servicio / Categoría:"));
        txtBuscar = new JTextField(16);
        btnBuscar = new JButton("Filtrar");
        JButton btnTodos = new JButton("Ver Todos");

        searchBar.add(txtBuscar);
        searchBar.add(btnBuscar);
        searchBar.add(btnTodos);
        tablePanel.add(searchBar, BorderLayout.NORTH);

        String[] columnas = {"ID", "Servicio", "Categoría", "Precio", "Duración", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaServicios = new JTable(modeloTabla);
        tablaServicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaServicios.setRowHeight(24);
        tablaServicios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaServicios.getTableHeader().setBackground(new Color(245, 230, 238));

        tablaServicios.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaServicios.getColumnModel().getColumn(1).setPreferredWidth(170);
        tablaServicios.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaServicios.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaServicios.getColumnModel().getColumn(4).setPreferredWidth(70);
        tablaServicios.getColumnModel().getColumn(5).setPreferredWidth(180);

        tablePanel.add(new JScrollPane(tablaServicios), BorderLayout.CENTER);

        JPanel contentSplit = new JPanel(new BorderLayout(15, 10));
        contentSplit.setBackground(Color.WHITE);
        contentSplit.add(formPanel, BorderLayout.WEST);
        contentSplit.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentSplit, BorderLayout.CENTER);
        add(mainPanel);

        // Eventos
        btnGuardar.addActionListener(e -> guardarServicio());
        btnEditar.addActionListener(e -> editarServicio());
        btnEliminar.addActionListener(e -> eliminarServicio());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> cargarServicios(txtBuscar.getText().trim()));
        btnTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarServicios("");
        });

        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = tablaServicios.getSelectedRow();
                if (sel != -1) {
                    servicioSeleccionadoId = (int) modeloTabla.getValueAt(sel, 0);
                    txtNombre.setText((String) modeloTabla.getValueAt(sel, 1));
                    cboCategoria.setSelectedItem(modeloTabla.getValueAt(sel, 2));
                    String prStr = (String) modeloTabla.getValueAt(sel, 3);
                    txtPrecio.setText(prStr.replace("$", "").replace(",", "").replace(".", "").trim());
                    txtDuracion.setText(String.valueOf(modeloTabla.getValueAt(sel, 4)).replace(" min", "").trim());
                    txtDescripcion.setText((String) modeloTabla.getValueAt(sel, 5));
                }
            }
        });
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
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

    private void cargarServicios(String filtro) {
        modeloTabla.setRowCount(0);
        String sql = "SELECT id, nombre, categoria, precio, duracion_minutos, descripcion FROM servicios WHERE activo = 1";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " AND (nombre LIKE ? OR categoria LIKE ?)";
        }
        sql += " ORDER BY categoria, nombre ASC";

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
                        rs.getString("categoria"),
                        String.format("$%,.0f", rs.getDouble("precio")),
                        rs.getInt("duracion_minutos") + " min",
                        rs.getString("descripcion") != null ? rs.getString("descripcion") : ""
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando servicios: " + e.getMessage());
        }
    }

    private void guardarServicio() {
        String nombre = txtNombre.getText().trim();
        String categoria = (String) cboCategoria.getSelectedItem();
        String precioStr = txtPrecio.getText().trim();
        String duracionStr = txtDuracion.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Precio son obligatorios.");
            return;
        }

        double precio;
        int duracion = 60;
        try {
            precio = Double.parseDouble(precioStr);
            if (!duracionStr.isEmpty()) duracion = Integer.parseInt(duracionStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio o duración no son valores numéricos válidos.");
            return;
        }

        String sql = "INSERT INTO servicios (nombre, categoria, precio, duracion_minutos, descripcion, activo) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, categoria);
            ps.setDouble(3, precio);
            ps.setInt(4, duracion);
            ps.setString(5, descripcion);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Servicio registrado exitosamente.");
            limpiarFormulario();
            cargarServicios("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar servicio: " + e.getMessage());
        }
    }

    private void editarServicio() {
        if (servicioSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para editar.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String categoria = (String) cboCategoria.getSelectedItem();
        String precioStr = txtPrecio.getText().trim();
        String duracionStr = txtDuracion.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Precio son requeridos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int duracion = Integer.parseInt(duracionStr);

            String sql = "UPDATE servicios SET nombre = ?, categoria = ?, precio = ?, duracion_minutos = ?, descripcion = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, nombre);
                ps.setString(2, categoria);
                ps.setDouble(3, precio);
                ps.setInt(4, duracion);
                ps.setString(5, descripcion);
                ps.setInt(6, servicioSeleccionadoId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Servicio actualizado.");
                limpiarFormulario();
                cargarServicios("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void eliminarServicio() {
        if (servicioSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione el servicio a eliminar.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea deshabilitar este servicio del catálogo?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE servicios SET activo = 0 WHERE id = ?")) {
                ps.setInt(1, servicioSeleccionadoId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Servicio eliminado del catálogo.");
                limpiarFormulario();
                cargarServicios("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void limpiarFormulario() {
        servicioSeleccionadoId = -1;
        txtNombre.setText("");
        txtPrecio.setText("");
        txtDuracion.setText("60");
        txtDescripcion.setText("");
        tablaServicios.clearSelection();
    }
}
