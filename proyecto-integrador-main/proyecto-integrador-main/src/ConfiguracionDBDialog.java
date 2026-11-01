import javax.swing.*;
import java.awt.*;

public class ConfiguracionDBDialog extends JDialog {
    private JTextField txtHost, txtPort, txtDbName, txtUser;
    private JPasswordField txtPassword;
    private JButton btnProbar, btnGuardar, btnCancelar;
    private JLabel lblStatus;
    private boolean configuradoConExito = false;

    public ConfiguracionDBDialog(Frame parent) {
        super(parent, "Configuración de Base de Datos MySQL - Salón Aremi", true);
        initComponents();
    }

    private void initComponents() {
        setSize(480, 420);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        setResizable(false);

        Color rosaOscuro = new Color(219, 112, 147);
        Color rosaClaro = new Color(255, 182, 193);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(rosaOscuro);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Configuración de Conexión MySQL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        txtHost = new JTextField(DatabaseConfig.getHost(), 15);
        txtPort = new JTextField(DatabaseConfig.getPort(), 15);
        txtDbName = new JTextField(DatabaseConfig.getDbName(), 15);
        txtUser = new JTextField(DatabaseConfig.getUser(), 15);
        txtPassword = new JPasswordField(DatabaseConfig.getPassword(), 15);

        int row = 0;
        addFormField(formPanel, "Host / Servidor:", txtHost, gbc, row++);
        addFormField(formPanel, "Puerto MySQL:", txtPort, gbc, row++);
        addFormField(formPanel, "Nombre Base de Datos:", txtDbName, gbc, row++);
        addFormField(formPanel, "Usuario MySQL:", txtUser, gbc, row++);
        addFormField(formPanel, "Contraseña MySQL:", txtPassword, gbc, row++);

        lblStatus = new JLabel("Ingrese las credenciales de su MySQL local.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(lblStatus, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(new Color(245, 245, 245));

        btnProbar = new JButton("Probar Conexión");
        btnGuardar = new JButton("Guardar y Continuar");
        btnCancelar = new JButton("Cancelar");

        btnProbar.addActionListener(e -> probarConexion());
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnProbar);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void probarConexion() {
        String h = txtHost.getText().trim();
        String p = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String u = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        lblStatus.setText("Probando conexión a MySQL...");
        lblStatus.setForeground(Color.BLUE);

        boolean ok = DatabaseConfig.testConnection(h, p, db, u, pass);
        if (ok) {
            lblStatus.setText("¡Conexión exitosa con el servidor MySQL! ✓");
            lblStatus.setForeground(new Color(0, 130, 0));
        } else {
            lblStatus.setText("Error: no se pudo conectar. Verifique usuario o contraseña. ✗");
            lblStatus.setForeground(Color.RED);
        }
    }

    private void guardarConfiguracion() {
        String h = txtHost.getText().trim();
        String p = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String u = txtUser.getText().trim();
        String pass = new String(txtPassword.getPassword());

        DatabaseConfig.saveConfig(h, p, db, u, pass);
        configuradoConExito = true;
        dispose();
    }

    public boolean isConfiguradoConExito() {
        return configuradoConExito;
    }
}
