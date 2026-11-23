package view;

import controller.ServicioController;
import model.Servicio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista del Catálogo de Servicios y Precios (MVC - Vista).
 * Delega en ServicioController. Cero SQL en la interfaz gráfica.
 */
public class GestionServiciosGUI extends JFrame {
    private final ServicioController servicioController;

    private JTextField txtNombre, txtPrecio, txtDuracion, txtDescripcion, txtBuscar;
    private JComboBox<String> cboCategoria;
    private JTable tablaServicios;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnBuscar;
    private int servicioSeleccionadoId = -1;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public GestionServiciosGUI() {
        this.servicioController = new ServicioController();
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

        // Botones
        JPanel btnPanelForm = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanelForm.setOpaque(false);
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnGuardar.setBackground(ROSA_OSCURO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);
        btnEditar.setBackground(new Color(70, 130, 180));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setOpaque(true);
        btnEditar.setBorderPainted(false);
        btnEliminar.setBackground(new Color(205, 92, 92));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnLimpiar.setBackground(new Color(160, 160, 160));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);

        btnPanelForm.add(btnGuardar);
        btnPanelForm.add(btnEditar);
        btnPanelForm.add(btnEliminar);
        btnPanelForm.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 6, 10);
        formPanel.add(btnPanelForm, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Tabla y Filtro
        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Filtrar por Servicio / Categoría:"));
        txtBuscar = new JTextField(16);
        searchPanel.add(txtBuscar);
        btnBuscar = new JButton("Filtrar");
        JButton btnVerTodos = new JButton("Ver Todos");
        searchPanel.add(btnBuscar);
        searchPanel.add(btnVerTodos);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Servicio", "Categoría", "Precio", "Duración", "Descripción"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaServicios = new JTable(modeloTabla);
        tablaServicios.setRowHeight(24);
        tablaServicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaServicios.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaServicios.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaServicios.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaServicios.getColumnModel().getColumn(3).setPreferredWidth(75);
        tablaServicios.getColumnModel().getColumn(4).setPreferredWidth(65);
        tablaServicios.getColumnModel().getColumn(5).setPreferredWidth(170);

        tablePanel.add(new JScrollPane(tablaServicios), BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel);

        // Eventos
        btnGuardar.addActionListener(e -> guardarServicio());
        btnEditar.addActionListener(e -> actualizarServicio());
        btnEliminar.addActionListener(e -> eliminarServicio());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> cargarServicios(txtBuscar.getText().trim()));
        btnVerTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarServicios("");
        });

        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = tablaServicios.getSelectedRow();
                if (sel != -1) {
                    servicioSeleccionadoId = (int) modeloTabla.getValueAt(sel, 0);
                    txtNombre.setText((String) modeloTabla.getValueAt(sel, 1));
                    cboCategoria.setSelectedItem((String) modeloTabla.getValueAt(sel, 2));
                    String precioStr = String.valueOf(modeloTabla.getValueAt(sel, 3)).replace("$", "").replace(",", "").trim();
                    txtPrecio.setText(precioStr);
                    String durStr = String.valueOf(modeloTabla.getValueAt(sel, 4)).replace(" min", "").trim();
                    txtDuracion.setText(durStr);
                    txtDescripcion.setText((String) modeloTabla.getValueAt(sel, 5));
                }
            }
        });
    }

    private void addFormField(JPanel panel, String label, JComponent comp, GridBagConstraints gbc, int row) {
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

    private void cargarServicios(String filtro) {
        modeloTabla.setRowCount(0);
        try {
            List<Servicio> lista = servicioController.listarServicios(filtro);
            for (Servicio s : lista) {
                modeloTabla.addRow(new Object[]{
                    s.getId(),
                    s.getNombre(),
                    s.getCategoria(),
                    "$" + String.format("%,.0f", s.getPrecio()),
                    s.getDuracionMinutos() + " min",
                    s.getDescripcion()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar servicios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarServicio() {
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            servicioController.registrarServicio(
                txtNombre.getText(),
                (String) cboCategoria.getSelectedItem(),
                precio,
                duracion,
                txtDescripcion.getText()
            );
            JOptionPane.showMessageDialog(this, "Servicio registrado exitosamente en el catálogo.");
            limpiarFormulario();
            cargarServicios("");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El precio y la duración deben ser valores numéricos válidos.", "Error de formato", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarServicio() {
        if (servicioSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            servicioController.actualizarServicio(
                servicioSeleccionadoId,
                txtNombre.getText(),
                (String) cboCategoria.getSelectedItem(),
                precio,
                duracion,
                txtDescripcion.getText(),
                true
            );
            JOptionPane.showMessageDialog(this, "Servicio actualizado correctamente.");
            limpiarFormulario();
            cargarServicios("");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Precio o duración no válidos.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al actualizar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarServicio() {
        if (servicioSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para desactivar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de desactivar este servicio?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                servicioController.eliminarServicio(servicioSeleccionadoId);
                JOptionPane.showMessageDialog(this, "Servicio desactivado del catálogo.");
                limpiarFormulario();
                cargarServicios("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        servicioSeleccionadoId = -1;
        txtNombre.setText("");
        txtPrecio.setText("");
        txtDuracion.setText("60");
        txtDescripcion.setText("");
        cboCategoria.setSelectedIndex(0);
        tablaServicios.clearSelection();
    }
}
