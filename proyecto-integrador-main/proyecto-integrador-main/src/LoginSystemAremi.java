import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.*;

public class LoginSystemAremi extends JDialog {
    private JPanel contentPane;
    private JTextField usuarioTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton limpiarButton;
    private JButton btnConfigDb;
    private JLabel logoLabel;

    private boolean loginSuccessful = false;

    // Colores del Salón AREMI
    private static final Color ROSA_CLARO = new Color(255, 182, 193);
    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color MORADO_SUAVE = new Color(230, 190, 255);

    private static ImageIcon logoIcon = null;

    public LoginSystemAremi() {
        setTitle("Salón de Belleza AREMI - Iniciar Sesión");
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                GradientPaint gradient = new GradientPaint(
                        0, 0, ROSA_CLARO,
                        0, getHeight(), MORADO_SUAVE
                );
                ((Graphics2D) g).setPaint(gradient);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(new BorderLayout());

        setSize(460, 560);
        setLocationRelativeTo(null);
        setModal(true);
        setResizable(false);

        // Cargar logo
        cargarLogo();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(255, 255, 255, 220));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_OSCURO, 2, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo / Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(logoLabel, gbc);

        JLabel subtitulo = new JLabel("Sistema de Gestión de Citas y Liquidaciones", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitulo.setForeground(new Color(120, 50, 80));
        gbc.gridy = 1;
        mainPanel.add(subtitulo, gbc);

        // Campos
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setFont(labelFont);
        usuarioLabel.setForeground(ROSA_OSCURO);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(usuarioLabel, gbc);

        usuarioTextField = new JTextField("fernanda", 16);
        usuarioTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usuarioTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_OSCURO),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        gbc.gridx = 1;
        mainPanel.add(usuarioTextField, gbc);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(ROSA_OSCURO);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField("fernanda123", 16);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_OSCURO),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Panel de botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setOpaque(false);

        loginButton = new JButton("Ingresar");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(ROSA_OSCURO);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(130, 36));

        limpiarButton = new JButton("Limpiar");
        limpiarButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        limpiarButton.setBackground(new Color(230, 230, 230));
        limpiarButton.setFocusPainted(false);
        limpiarButton.setPreferredSize(new Dimension(100, 36));

        btnPanel.add(loginButton);
        btnPanel.add(limpiarButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        mainPanel.add(btnPanel, gbc);

        // Botón config BD
        btnConfigDb = new JButton("⚙ Configurar Conexión a Base de Datos");
        btnConfigDb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnConfigDb.setBorderPainted(false);
        btnConfigDb.setContentAreaFilled(false);
        btnConfigDb.setForeground(new Color(130, 70, 100));
        btnConfigDb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfigDb.addActionListener(e -> {
            ConfiguracionDBDialog cfg = new ConfiguracionDBDialog(null);
            cfg.setVisible(true);
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 5, 10);
        mainPanel.add(btnConfigDb, gbc);

        contentPane.add(mainPanel, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void cargarLogo() {
        try {
            File logoFile = new File("src/AREMI.png");
            if (!logoFile.exists()) {
                logoFile = new File("proyecto-integrador-main/src/AREMI.png");
            }
            if (logoFile.exists()) {
                ImageIcon icon = new ImageIcon(logoFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(scaled));
                setIconImage(icon.getImage());
                return;
            }
        } catch (Exception e) {
            // fallback
        }
        logoLabel = new JLabel("AREMI", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(ROSA_OSCURO);
    }

    private void setupListeners() {
        loginButton.addActionListener(e -> verificarLogin());
        limpiarButton.addActionListener(e -> {
            usuarioTextField.setText("");
            passwordField.setText("");
            usuarioTextField.requestFocus();
        });

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        getRootPane().registerKeyboardAction(e -> verificarLogin(), enter, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void verificarLogin() {
        String usuario = usuarioTextField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese usuario y contraseña.",
                    "Datos requeridos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hashedPassword = SeguridadManager.hashPassword(password);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT username, password, rol, nombre_completo FROM usuarios WHERE username = ?")) {

            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String rol = rs.getString("rol");
                    String nombreCompleto = rs.getString("nombre_completo");

                    // Verificación directa o con hash (para tolerar contraseñas viejas en texto plano o hash SHA-256)
                    boolean coincide = hashedPassword.equalsIgnoreCase(storedHash) || password.equals(storedHash);

                    if (coincide) {
                        SeguridadManager.setUsuarioActual(usuario, rol, nombreCompleto);
                        loginSuccessful = true;
                        JOptionPane.showMessageDialog(this,
                                "¡Bienvenido/a, " + nombreCompleto + "!\nRol asignado: " + rol.toUpperCase(),
                                "Acceso Concedido",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Contraseña incorrecta para el usuario '" + usuario + "'.",
                                "Error de Acceso",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El usuario '" + usuario + "' no existe en el sistema.",
                            "Usuario No Encontrado",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos:\n" + e.getMessage() +
                    "\n\nPuede ajustar sus credenciales con el botón 'Configurar Conexión a Base de Datos'.",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}
