package view;

import dao.ConexionBD;
import javax.swing.*;
import java.awt.*;

/**
 * Diálogo de Configuración de Base de Datos.
 * Permite alternar entre Base de Datos Local Embebida (H2) y Servidor MySQL externo.
 * Elimina credenciales quemadas en el código fuente.
 */
public class ConfiguracionDBDialog extends JDialog {
    private JRadioButton rdoLocal;
    private JRadioButton rdoMySQL;
    private JTextField txtHost;
    private JTextField txtPuerto;
    private JTextField txtBaseDatos;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnProbar;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JLabel lblEstado;

    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public ConfiguracionDBDialog(Window parent) {
        super(parent, "Configuración de Base de Datos - Salón AREMI", ModalityType.APPLICATION_MODAL);
        setSize(480, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        cargarValoresActuales();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ROSA_OSCURO);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel lblTit = new JLabel("Configuración de Base de Datos", SwingConstants.CENTER);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(Color.WHITE);
        header.add(lblTit);
        main.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Selección tipo BD
        JPanel typePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        typePanel.setBackground(new Color(253, 245, 248));
        typePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            "Tipo de Base de Datos",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), ROSA_OSCURO
        ));

        rdoLocal = new JRadioButton("Base de Datos Local Integrada (Sin contraseña, 100% lista para usar)");
        rdoLocal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rdoLocal.setOpaque(false);

        rdoMySQL = new JRadioButton("Servidor MySQL Local (Para conectar con MySQL Workbench / phpMyAdmin)");
        rdoMySQL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rdoMySQL.setOpaque(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdoLocal);
        bg.add(rdoMySQL);

        typePanel.add(rdoLocal);
        typePanel.add(rdoMySQL);
        content.add(typePanel);
        content.add(Box.createVerticalStrut(10));

        // Parámetros MySQL
        JPanel mysqlPanel = new JPanel(new GridBagLayout());
        mysqlPanel.setBackground(Color.WHITE);
        mysqlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1),
            "Parámetros de Servidor MySQL",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), ROSA_OSCURO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 5, 8);

        txtHost = new JTextField("127.0.0.1", 14);
        txtPuerto = new JTextField("3306", 14);
        txtBaseDatos = new JTextField("salon_aremi", 14);
        txtUsuario = new JTextField("root", 14);
        txtPassword = new JPasswordField(14);

        int r = 0;
        addRow(mysqlPanel, "Servidor / Host:", txtHost, gbc, r++);
        addRow(mysqlPanel, "Puerto:", txtPuerto, gbc, r++);
        addRow(mysqlPanel, "Base de Datos:", txtBaseDatos, gbc, r++);
        addRow(mysqlPanel, "Usuario MySQL:", txtUsuario, gbc, r++);
        addRow(mysqlPanel, "Contraseña MySQL:", txtPassword, gbc, r++);

        content.add(mysqlPanel);
        content.add(Box.createVerticalStrut(10));

        lblEstado = new JLabel(" ", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblEstado);

        main.add(content, BorderLayout.CENTER);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setOpaque(false);

        btnProbar = new JButton("Probar Conexión");
        btnProbar.setBackground(new Color(70, 130, 180));
        btnProbar.setForeground(Color.WHITE);
        btnProbar.setOpaque(true);
        btnProbar.setBorderPainted(false);

        btnGuardar = new JButton("Guardar y Continuar");
        btnGuardar.setBackground(ROSA_OSCURO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(160, 160, 160));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setOpaque(true);
        btnCancelar.setBorderPainted(false);

        btnPanel.add(btnProbar);
        btnPanel.add(btnGuardar);
        btnPanel.add(btnCancelar);

        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);

        // Listeners
        rdoLocal.addActionListener(e -> alternarCampos());
        rdoMySQL.addActionListener(e -> alternarCampos());

        btnProbar.addActionListener(e -> {
            boolean exito = ConexionBD.getInstancia().testConexion();
            if (exito) {
                lblEstado.setText("✔ Conexión probada exitosamente.");
                lblEstado.setForeground(new Color(46, 139, 87));
            } else {
                lblEstado.setText("✖ Falló la prueba de conexión.");
                lblEstado.setForeground(Color.RED);
            }
        });

        btnGuardar.addActionListener(e -> {
            boolean local = rdoLocal.isSelected();
            ConexionBD.getInstancia().guardarConfiguracion(
                local,
                txtHost.getText().trim(),
                txtPuerto.getText().trim(),
                txtBaseDatos.getText().trim(),
                txtUsuario.getText().trim(),
                new String(txtPassword.getPassword())
            );
            JOptionPane.showMessageDialog(this, "Configuración guardada exitosamente.");
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    private void addRow(JPanel p, String l, JComponent c, GridBagConstraints gbc, int r) {
        JLabel lbl = new JLabel(l);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.4;
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        p.add(c, gbc);
    }

    private void cargarValoresActuales() {
        ConexionBD bd = ConexionBD.getInstancia();
        if (bd.isUsarLocal()) {
            rdoLocal.setSelected(true);
        } else {
            rdoMySQL.setSelected(true);
        }
        txtHost.setText(bd.getHost());
        txtPuerto.setText(bd.getPuerto());
        txtBaseDatos.setText(bd.getBaseDatos());
        txtUsuario.setText(bd.getUsuario());
        alternarCampos();
    }

    private void alternarCampos() {
        boolean mysqlActivo = rdoMySQL.isSelected();
        txtHost.setEnabled(mysqlActivo);
        txtPuerto.setEnabled(mysqlActivo);
        txtBaseDatos.setEnabled(mysqlActivo);
        txtUsuario.setEnabled(mysqlActivo);
        txtPassword.setEnabled(mysqlActivo);
        if (!mysqlActivo) {
            lblEstado.setText("Modo Local H2 activo: No requiere servidor ni contraseñas.");
            lblEstado.setForeground(new Color(100, 100, 100));
        } else {
            lblEstado.setText("Modo MySQL activo: Ingrese sus credenciales locales de MySQL.");
            lblEstado.setForeground(new Color(70, 130, 180));
        }
    }
}
