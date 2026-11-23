package view;

import controller.EmpleadoController;
import model.Empleado;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista de Administración de Empleadas y Configuración de Comisiones (MVC - Vista).
 * Delega en EmpleadoController. Cero SQL en la interfaz gráfica.
 */
public class GestionEmpleadasGUI extends JFrame {
    private final EmpleadoController empleadoController;

    private JTextField txtNombre, txtTelefono, txtEspecialidad, txtComision, txtUsername;
    private JCheckBox chkActivo;
    private JTable tablaEmpleadas;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar;
    private int empleadaSeleccionadaId = -1;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public GestionEmpleadasGUI() {
        this.empleadoController = new EmpleadoController();
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
        txtComision = new JTextField(15);
        txtUsername = new JTextField(15);
        chkActivo = new JCheckBox("Empleada Activa", true);
        chkActivo.setOpaque(false);

        int row = 0;
        addFormField(formPanel, "Nombre *:", txtNombre, gbc, row++);
        addFormField(formPanel, "Teléfono:", txtTelefono, gbc, row++);
        addFormField(formPanel, "Especialidad *:", txtEspecialidad, gbc, row++);
        addFormField(formPanel, "Porcentaje Comisión (%) *:", txtComision, gbc, row++);
        addFormField(formPanel, "Usuario del Sistema:", txtUsername, gbc, row++);

        gbc.gridx = 1;
        gbc.gridy = row++;
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

        // Tabla
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Teléfono", "Especialidad", "Comisión %", "Usuario", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaEmpleadas = new JTable(modeloTabla);
        tablaEmpleadas.setRowHeight(24);
        tablaEmpleadas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleadas.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaEmpleadas.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaEmpleadas.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaEmpleadas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaEmpleadas.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaEmpleadas.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaEmpleadas.getColumnModel().getColumn(6).setPreferredWidth(60);

        mainPanel.add(new JScrollPane(tablaEmpleadas), BorderLayout.CENTER);
        add(mainPanel);

        // Eventos delegados
        btnGuardar.addActionListener(e -> guardarEmpleada());
        btnEditar.addActionListener(e -> actualizarEmpleada());
        btnEliminar.addActionListener(e -> desactivarEmpleada());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaEmpleadas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int sel = tablaEmpleadas.getSelectedRow();
                if (sel != -1) {
                    empleadaSeleccionadaId = (int) modeloTabla.getValueAt(sel, 0);
                    txtNombre.setText((String) modeloTabla.getValueAt(sel, 1));
                    txtTelefono.setText((String) modeloTabla.getValueAt(sel, 2));
                    txtEspecialidad.setText((String) modeloTabla.getValueAt(sel, 3));
                    txtComision.setText(String.valueOf(modeloTabla.getValueAt(sel, 4)));
                    txtUsername.setText((String) modeloTabla.getValueAt(sel, 5));
                    chkActivo.setSelected("Activo".equals(modeloTabla.getValueAt(sel, 6)));
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
        gbc.weightx = 0.40;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.60;
        panel.add(field, gbc);
    }

    private void cargarEmpleadas() {
        modeloTabla.setRowCount(0);
        try {
            List<Empleado> lista = empleadoController.listarTodas();
            for (Empleado emp : lista) {
                modeloTabla.addRow(new Object[]{
                    emp.getId(),
                    emp.getNombre(),
                    emp.getTelefono(),
                    emp.getEspecialidad(),
                    emp.getPorcentajeComision(),
                    emp.getUsername() != null ? emp.getUsername() : "",
                    emp.isActivo() ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleadas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEmpleada() {
        try {
            double comision = Double.parseDouble(txtComision.getText().trim());
            empleadoController.registrarEmpleado(
                txtNombre.getText(),
                txtTelefono.getText(),
                txtEspecialidad.getText(),
                comision,
                txtUsername.getText(),
                chkActivo.isSelected()
            );
            JOptionPane.showMessageDialog(this, "Empleada registrada exitosamente.");
            limpiarFormulario();
            cargarEmpleadas();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El porcentaje de comisión debe ser un valor numérico (ej: 20, 30, 50).", "Formato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarEmpleada() {
        if (empleadaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una empleada de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double comision = Double.parseDouble(txtComision.getText().trim());
            empleadoController.actualizarEmpleado(
                empleadaSeleccionadaId,
                txtNombre.getText(),
                txtTelefono.getText(),
                txtEspecialidad.getText(),
                comision,
                txtUsername.getText(),
                chkActivo.isSelected()
            );
            JOptionPane.showMessageDialog(this, "Información de la empleada actualizada correctamente.");
            limpiarFormulario();
            cargarEmpleadas();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Porcentaje de comisión inválido.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al actualizar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void desactivarEmpleada() {
        if (empleadaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una empleada para inactivar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desea cambiar el estado a inactivo de la empleada seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                empleadoController.desactivarEmpleado(empleadaSeleccionadaId);
                JOptionPane.showMessageDialog(this, "Estado actualizado con éxito.");
                limpiarFormulario();
                cargarEmpleadas();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
