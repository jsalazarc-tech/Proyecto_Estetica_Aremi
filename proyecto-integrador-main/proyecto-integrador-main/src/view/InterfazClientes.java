package view;

import controller.ClienteController;
import model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista de Gestión de Clientes (Patrón MVC - Capa Vista).
 * No contiene sentencias SQL ni conexiones directas a bases de datos;
 * delega toda la lógica de negocio y persistencia en ClienteController.
 */
public class InterfazClientes extends JFrame {
    private final ClienteController clienteController;

    private JTextField txtNombre, txtTelefono, txtDireccion, txtEmail, txtBuscar;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnLimpiar, btnBuscar;
    private int clienteSeleccionadoId = -1;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public InterfazClientes() {
        this.clienteController = new ClienteController();
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
        btnAgregar.setOpaque(true);
        btnAgregar.setBorderPainted(false);
        btnEditar.setBackground(new Color(70, 130, 180));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setOpaque(true);
        btnEditar.setBorderPainted(false);
        btnEliminar.setBackground(new Color(220, 70, 70));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnLimpiar.setBackground(new Color(160, 160, 160));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);

        btnPanelForm.add(btnAgregar);
        btnPanelForm.add(btnEditar);
        btnPanelForm.add(btnEliminar);
        btnPanelForm.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 6, 10);
        formPanel.add(btnPanelForm, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Panel Central: Tabla y Búsqueda
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Buscar Cliente:"));
        txtBuscar = new JTextField(18);
        searchPanel.add(txtBuscar);
        btnBuscar = new JButton("Buscar");
        JButton btnVerTodos = new JButton("Ver Todos");
        searchPanel.add(btnBuscar);
        searchPanel.add(btnVerTodos);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Teléfono", "Dirección", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(24);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(140);

        tablePanel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel);

        // Eventos delegados al Controlador
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
        try {
            List<Cliente> clientes = clienteController.listarClientes(filtro);
            for (Cliente c : clientes) {
                modeloTabla.addRow(new Object[]{
                    c.getId(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getDireccion(),
                    c.getEmail()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarCliente() {
        try {
            clienteController.registrarCliente(
                txtNombre.getText(),
                txtTelefono.getText(),
                txtDireccion.getText(),
                txtEmail.getText()
            );
            JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.");
            limpiarFormulario();
            cargarClientes("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editarCliente() {
        if (clienteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un cliente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            clienteController.actualizarCliente(
                clienteSeleccionadoId,
                txtNombre.getText(),
                txtTelefono.getText(),
                txtDireccion.getText(),
                txtEmail.getText()
            );
            JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.");
            limpiarFormulario();
            cargarClientes("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al actualizar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarCliente() {
        if (clienteSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un cliente para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el cliente seleccionado?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteController.eliminarCliente(clienteSeleccionadoId);
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.");
                limpiarFormulario();
                cargarClientes("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Aviso de Integridad", JOptionPane.ERROR_MESSAGE);
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
