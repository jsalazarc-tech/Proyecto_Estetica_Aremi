import javax.swing.*;
import java.awt.*;

public class ConfiguracionDBDialog extends JDialog {
    private JRadioButton rdoLocal, rdoMysql;
    private JTextField txtHost, txtPort, txtDbName, txtUser;
    private JPasswordField txtPassword;
    private JButton btnProbar, btnGuardar, btnCancelar;
    private JLabel lblStatus;
    private JPanel mysqlFieldsPanel;
    private boolean configuradoConExito = false;

    public ConfiguracionDBDialog(Frame parent) {
        super(parent, "Configuración de Base de Datos - Salón AREMI", true);
        initComponents();
    }

    private void initComponents() {
        setSize(520, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setResizable(false);

        Color rosaOscuro = new Color(219, 112, 147);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(rosaOscuro);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Configuración de Base de Datos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Selector de tipo de base de datos
        JPanel typePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        typePanel.setBackground(new Color(250, 245, 248));
        typePanel.setBorder(BorderFactory.createTitledBorder("Tipo de Base de Datos"));

        rdoLocal = new JRadioButton("Base de Datos Local Integrada (Sin contraseña, 100% lista para usar)", DatabaseConfig.isUseLocalDb());
        rdoLocal.setOpaque(false);
        rdoMysql = new JRadioButton("Servidor MySQL Local (Para conectar con MySQL Workbench / phpMyAdmin)", !DatabaseConfig.isUseLocalDb());
        rdoMysql.setOpaque(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdoLocal);
        bg.add(rdoMysql);

        typePanel.add(rdoLocal);
        typePanel.add(rdoMysql);
        contentPanel.add(typePanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Campos para MySQL
        mysqlFieldsPanel = new JPanel(new GridBagLayout());
        mysqlFieldsPanel.setBackground(Color.WHITE);
        mysqlFieldsPanel.setBorder(BorderFactory.createTitledBorder("Parámetros de Servidor MySQL"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtHost = new JTextField(DatabaseConfig.getHost(), 15);
        txtPort = new JTextField(DatabaseConfig.getPort(), 15);
        txtDbName = new JTextField(DatabaseConfig.getDbName(), 15);
        txtUser = new JTextField(DatabaseConfig.getUser(), 15);
        txtPassword = new JPasswordField(DatabaseConfig.getPassword(), 15);

        int row = 0;
        addFormField(mysqlFieldsPanel, "Servidor / Host:", txtHost, gbc, row++);
        addFormField(mysqlFieldsPanel, "Puerto:", txtPort, gbc, row++);
        addFormField(mysqlFieldsPanel, "Base de Datos:", txtDbName, gbc, row++);
        addFormField(mysqlFieldsPanel, "Usuario MySQL:", txtUser, gbc, row++);
        addFormField(mysqlFieldsPanel, "Contraseña MySQL:", txtPassword, gbc, row++);

        contentPanel.add(mysqlFieldsPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        lblStatus = new JLabel("Seleccione el modo que desea utilizar.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.DARK_GRAY);
        contentPanel.add(lblStatus);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnProbar = new JButton("Probar Conexión");
        btnProbar.setBackground(new Color(70, 130, 180));
        btnProbar.setForeground(Color.WHITE);
        btnProbar.setOpaque(true);
        btnProbar.setBorderPainted(false);

        btnGuardar = new JButton("Guardar y Continuar");
        btnGuardar.setBackground(rosaOscuro);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(220, 220, 220));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setOpaque(true);
        btnCancelar.setBorderPainted(false);

        btnProbar.addActionListener(e -> probarConexion());
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnProbar);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(buttonPanel, BorderLayout.SOUTH);

        // Actualizar visibilidad de campos
        rdoLocal.addActionListener(e -> actualizarEstadoCampos());
        rdoMysql.addActionListener(e -> actualizarEstadoCampos());
        actualizarEstadoCampos();
    }

    private void actualizarEstadoCampos() {
        boolean esMysql = rdoMysql.isSelected();
        txtHost.setEnabled(esMysql);
        txtPort.setEnabled(esMysql);
        txtDbName.setEnabled(esMysql);
        txtUser.setEnabled(esMysql);
        txtPassword.setEnabled(esMysql);
        if (!esMysql) {
            lblStatus.setText("Modo Local activo: no requiere instalación ni contraseñas. Funciona de inmediato.");
            lblStatus.setForeground(new Color(0, 130, 0));
        } else {
            lblStatus.setText("Modo MySQL activo: ingrese las credenciales de su MySQL local.");
            lblStatus.setForeground(Color.DARK_GRAY);
        }
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void probarConexion() {
        boolean local = rdoLocal.isSelected();
        String h = txtHost.getText().trim();
        String p = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String u = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        boolean ok = DatabaseConfig.testConnection(local, h, p, db, u, pass);
        if (ok) {
            lblStatus.setText("¡Conexión probada exitosamente! ✓");
            lblStatus.setForeground(new Color(0, 130, 0));
        } else {
            lblStatus.setText("Error al conectar. Verifique los datos o use el Modo Local. ✗");
            lblStatus.setForeground(Color.RED);
        }
    }

    private void guardarConfiguracion() {
        boolean local = rdoLocal.isSelected();
        String h = txtHost.getText().trim();
        String p = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String u = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        DatabaseConfig.saveConfig(local, h, p, db, u, pass);
        configuradoConExito = true;
        dispose();
    }

    public boolean isConfiguradoConExito() {
        return configuradoConExito;
    }
}
