import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginSystemAremi extends JDialog {
    private JPanel contentPane;
    private JTextField usuarioTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton limpiarButton;
    private JLabel logoLabel;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin1023";

    private boolean loginSuccessful = false;

    // Definimos colores personalizados
    private static final Color ROSA_CLARO = new Color(255, 182, 193);
    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color MORADO_SUAVE = new Color(230, 190, 255);

    // Cache de imágenes para optimización de rendimiento
    private static ImageIcon logoIcon = null;
    private static Image logoImageScaled = null;

    public LoginSystemAremi() {
        setTitle("Salón de Belleza AREMI - Login");
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

        // Configuración de la ventana
        setSize(500, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setResizable(false);

        // Cargar y mostrar el logo (con caché para optimización)
        if (logoIcon == null || logoImageScaled == null) {
            try {
                logoIcon = new ImageIcon("src/AREMI.png");
                logoImageScaled = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            } catch (Exception e) {
                System.out.println("Error al cargar el logo: " + e.getMessage());
            }
        }

        if (logoImageScaled != null) {
            logoLabel = new JLabel(new ImageIcon(logoImageScaled));
            if (logoIcon != null) {
                setIconImage(logoIcon.getImage());
            }
        } else {
            logoLabel = new JLabel("AREMI");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        }

        // Panel principal con margen y efecto de transparencia
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(255, 255, 255, 200));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_OSCURO, 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        // Agregar componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(logoLabel, gbc);

        // Estilo para las etiquetas
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        JLabel usuarioLabel = new JLabel("Usuario:");
        JLabel passwordLabel = new JLabel("Contraseña:");
        usuarioLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        usuarioLabel.setForeground(ROSA_OSCURO);
        passwordLabel.setForeground(ROSA_OSCURO);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(usuarioLabel, gbc);

        usuarioTextField = new JTextField(20);
        usuarioTextField.setPreferredSize(new Dimension(200, 30));
        usuarioTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_OSCURO),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        gbc.gridx = 1;
        mainPanel.add(usuarioTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_OSCURO),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Crear botones personalizados
        loginButton = new JButton("Iniciar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(MORADO_SUAVE.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(MORADO_SUAVE);
                } else {
                    g2.setColor(ROSA_OSCURO);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        limpiarButton = new JButton("Limpiar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(MORADO_SUAVE.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(MORADO_SUAVE);
                } else {
                    g2.setColor(ROSA_OSCURO);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Panel para botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Configurar estilo de botones
        for (JButton button : new JButton[]{loginButton, limpiarButton}) {
            button.setPreferredSize(new Dimension(130, 35));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setOpaque(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        buttonPanel.add(loginButton);
        buttonPanel.add(limpiarButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);

        contentPane.add(mainPanel, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void setupListeners() {
        loginButton.addActionListener(e -> verificarLogin());
        limpiarButton.addActionListener(e -> limpiarCampos());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        contentPane.registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                verificarLogin();
            }
        };

        usuarioTextField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
    }

    private void verificarLogin() {
        String usuario = usuarioTextField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese usuario y contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = hashPassword(password);
        System.out.println("Debug Login:");
        System.out.println("Usuario: " + usuario);
        System.out.println("Contraseña sin hash: " + password);
        System.out.println("Contraseña con hash: " + hashedPassword);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT password, rol, nombre_completo FROM usuarios WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, usuario);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        String rol = rs.getString("rol");
                        String nombreCompleto = rs.getString("nombre_completo");
                        System.out.println("Hash almacenado en BD: " + storedHash);

                        if (hashedPassword.equals(storedHash)) {
                            loginExitoso(rol, nombreCompleto);
                        } else {
                            System.out.println("Hash no coincide:");
                            System.out.println("Hash generado: " + hashedPassword);
                            System.out.println("Hash en BD: " + storedHash);
                            JOptionPane.showMessageDialog(this,
                                    "Contraseña incorrecta.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Usuario no encontrado.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error de conexión: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loginExitoso(String rol, String nombreCompleto) {
        // Establecer sesión en SeguridadManager
        SeguridadManager.setUsuarioActual(nombreCompleto, rol);

        JOptionPane.showMessageDialog(this,
                "Bienvenido/a " + nombreCompleto + "!\nRol: " + rol,
                "Login Exitoso",
                JOptionPane.INFORMATION_MESSAGE);
        loginSuccessful = true;
        dispose();
    }

    private void limpiarCampos() {
        usuarioTextField.setText("");
        passwordField.setText("");
        usuarioTextField.requestFocus();
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", new Color(255, 255, 255));
            UIManager.put("Panel.background", new Color(255, 255, 255));
            UIManager.put("OptionPane.messageForeground", ROSA_OSCURO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginSystemAremi dialog = new LoginSystemAremi();
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }
}
