package view;

import controller.EmpleadoController;
import model.Empleado;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista de Administración de Personal, Comisiones, Datos de Contacto y Asignación de Roles.
 * Delega en EmpleadoController siguiendo el patrón MVC.
 */
public class GestionEmpleadasGUI extends JFrame {
    private final EmpleadoController empleadoController;

    private JTextField txtNombre, txtTelefono, txtDireccion, txtEmail, txtEspecialidad, txtComision, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cboRol;
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
        setSize(1040, 640);
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
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel lblTitulo = new JLabel("Administración de Personal, Cargos y Comisiones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(253, 245, 248));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            "Ficha del Empleado / Personal",
            0, 0, new Font("Segoe UI", Font.BOLD, 14), ROSA_OSCURO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        txtNombre = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtDireccion = new JTextField(15);
        txtEmail = new JTextField(15);
        txtEmail.setToolTipText("Correo electrónico para notificaciones de pago y liquidación");
        txtEspecialidad = new JTextField(15);
        txtComision = new JTextField(15);
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setToolTipText("Contraseña para el inicio de sesión");

        cboRol = new JComboBox<>(new String[]{"Empleada", "Administrador"});
        cboRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboRol.setToolTipText("Permite ascender al empleado a Administrador con acceso total al sistema");

        chkActivo = new JCheckBox("Personal Activo", true);
        chkActivo.setOpaque(false);

        int row = 0;
        addFormField(formPanel, "Nombre *:", txtNombre, gbc, row++);
        addFormField(formPanel, "Teléfono:", txtTelefono, gbc, row++);
        addFormField(formPanel, "Dirección:", txtDireccion, gbc, row++);
        addFormField(formPanel, "Correo / Pagos:", txtEmail, gbc, row++);
        addFormField(formPanel, "Especialidad *:", txtEspecialidad, gbc, row++);
        addFormField(formPanel, "Comisión (%) *:", txtComision, gbc, row++);
        addFormField(formPanel, "Usuario Sistema:", txtUsername, gbc, row++);
        addFormField(formPanel, "Contraseña Acceso:", txtPassword, gbc, row++);
        addFormField(formPanel, "Cargo / Rol:", cboRol, gbc, row++);

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
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEditar.setBackground(new Color(70, 130, 180));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setOpaque(true);
        btnEditar.setBorderPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEliminar.setBackground(new Color(205, 92, 92));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setOpaque(true);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLimpiar.setBackground(new Color(160, 160, 160));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setOpaque(true);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanelForm.add(btnGuardar);
        btnPanelForm.add(btnEditar);
        btnPanelForm.add(btnEliminar);
        btnPanelForm.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 6, 10);
        formPanel.add(btnPanelForm, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Tabla
        modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Teléfono", "Dirección", "Correo / Pagos", "Especialidad", "Comisión %", "Usuario", "Rol", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaEmpleadas = new JTable(modeloTabla);
        tablaEmpleadas.setRowHeight(24);
        tablaEmpleadas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleadas.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaEmpleadas.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaEmpleadas.getColumnModel().getColumn(2).setPreferredWidth(85);
        tablaEmpleadas.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaEmpleadas.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaEmpleadas.getColumnModel().getColumn(5).setPreferredWidth(95);
        tablaEmpleadas.getColumnModel().getColumn(6).setPreferredWidth(70);
        tablaEmpleadas.getColumnModel().getColumn(7).setPreferredWidth(75);
        tablaEmpleadas.getColumnModel().getColumn(8).setPreferredWidth(80);
        tablaEmpleadas.getColumnModel().getColumn(9).setPreferredWidth(60);

        mainPanel.add(new JScrollPane(tablaEmpleadas), BorderLayout.CENTER);
        add(mainPanel);

        // Listeners
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
                    txtDireccion.setText((String) modeloTabla.getValueAt(sel, 3));
                    txtEmail.setText((String) modeloTabla.getValueAt(sel, 4));
                    txtEspecialidad.setText((String) modeloTabla.getValueAt(sel, 5));
                    txtComision.setText(String.valueOf(modeloTabla.getValueAt(sel, 6)));
                    txtUsername.setText((String) modeloTabla.getValueAt(sel, 7));
                    String rolVal = (String) modeloTabla.getValueAt(sel, 8);
                    cboRol.setSelectedItem("administrador".equalsIgnoreCase(rolVal) ? "Administrador" : "Empleada");
                    txtPassword.setText("");
                    chkActivo.setSelected("Activo".equals(modeloTabla.getValueAt(sel, 9)));
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
                    emp.getDireccion() != null ? emp.getDireccion() : "",
                    emp.getEmail() != null ? emp.getEmail() : "",
                    emp.getEspecialidad(),
                    emp.getPorcentajeComision(),
                    emp.getUsername() != null ? emp.getUsername() : "",
                    emp.getRol() != null ? emp.getRol() : "empleada",
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
            String pass = new String(txtPassword.getPassword()).trim();
            String rolSeleccionado = ((String) cboRol.getSelectedItem()).toLowerCase().trim();

            empleadoController.registrarEmpleado(
                txtNombre.getText(),
                txtTelefono.getText(),
                txtDireccion.getText(),
                txtEmail.getText(),
                txtEspecialidad.getText(),
                comision,
                txtUsername.getText(),
                pass,
                rolSeleccionado,
                chkActivo.isSelected()
            );

            JOptionPane.showMessageDialog(this,
                "Empleado registrado exitosamente.\n" +
                "Cargo asignado: " + (rolSeleccionado.equals("administrador") ? "Administrador (Acceso Total)" : "Empleada (Acceso Operativo)"),
                "Registro Exitoso",
                JOptionPane.INFORMATION_MESSAGE
            );

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
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double comision = Double.parseDouble(txtComision.getText().trim());
            String pass = new String(txtPassword.getPassword()).trim();
            String rolSeleccionado = ((String) cboRol.getSelectedItem()).toLowerCase().trim();

            empleadoController.actualizarEmpleado(
                empleadaSeleccionadaId,
                txtNombre.getText(),
                txtTelefono.getText(),
                txtDireccion.getText(),
                txtEmail.getText(),
                txtEspecialidad.getText(),
                comision,
                txtUsername.getText(),
                pass,
                rolSeleccionado,
                chkActivo.isSelected()
            );

            JOptionPane.showMessageDialog(this,
                "Información actualizada correctamente.\n" +
                "Cargo actual: " + (rolSeleccionado.equals("administrador") ? "Administrador (Acceso Total)" : "Empleada (Acceso Operativo)"),
                "Actualización Exitosa",
                JOptionPane.INFORMATION_MESSAGE
            );

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
            JOptionPane.showMessageDialog(this, "Seleccione un empleado para inactivar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desea cambiar el estado a inactivo del empleado seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
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
        txtDireccion.setText("");
        txtEmail.setText("");
        txtEspecialidad.setText("");
        txtComision.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cboRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tablaEmpleadas.clearSelection();
    }
}
