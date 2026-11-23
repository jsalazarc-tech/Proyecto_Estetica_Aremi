package view;

import controller.AuthController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Diálogo de Autenticación de Usuarios (MVC - Vista).
 * Delega en AuthController para validar usuario y contraseña con hash SHA-256.
 * Cero SQL en esta interfaz gráfica.
 */
public class LoginSystemAremi extends JDialog {
    private final AuthController authController;

    private JTextField usuarioTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton limpiarButton;
    private JButton btnConfigDb;
    private JLabel logoLabel;

    private boolean loginSuccessful = false;

    private static final Color ROSA_CLARO = new Color(255, 182, 193);
    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color MORADO_SUAVE = new Color(230, 190, 255);

    public LoginSystemAremi() {
        this.authController = new AuthController();
        setTitle("Salón de Belleza AREMI - Iniciar Sesión");
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        JPanel contentPane = new JPanel() {
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

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(logoLabel, gbc);

        JLabel subtitulo = new JLabel("Sistema de Gestión de Citas y Liquidaciones", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitulo.setForeground(new Color(120, 50, 80));
        gbc.gridy = 1;
        mainPanel.add(subtitulo, gbc);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setFont(labelFont);
        usuarioLabel.setForeground(ROSA_OSCURO);

        gbc.gridy = 2; gbc.gridwidth = 1;
        mainPanel.add(usuarioLabel, gbc);

        usuarioTextField = new JTextField("fernanda", 16);
        usuarioTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(usuarioTextField, gbc);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(ROSA_OSCURO);
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField("fernanda123", 16);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        loginButton = new JButton("Ingresar");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(ROSA_OSCURO);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(130, 38));

        limpiarButton = new JButton("Limpiar");
        limpiarButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        limpiarButton.setBackground(new Color(220, 220, 220));
        limpiarButton.setForeground(Color.DARK_GRAY);
        limpiarButton.setOpaque(true);
        limpiarButton.setBorderPainted(false);
        limpiarButton.setPreferredSize(new Dimension(100, 38));

        buttonPanel.add(loginButton);
        buttonPanel.add(limpiarButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        btnConfigDb = new JButton("⚙ Configurar Conexión a Base de Datos");
        btnConfigDb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnConfigDb.setForeground(new Color(100, 100, 100));
        btnConfigDb.setBorderPainted(false);
        btnConfigDb.setContentAreaFilled(false);
        btnConfigDb.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 5;
        mainPanel.add(btnConfigDb, gbc);

        contentPane.add(mainPanel, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void cargarLogo() {
        String[] posiblesRutas = {
            "src/AREMI.png",
            "proyecto-integrador-main/src/AREMI.png",
            "AREMI.png"
        };
        ImageIcon icono = null;
        for (String ruta : posiblesRutas) {
            File f = new File(ruta);
            if (f.exists()) {
                ImageIcon orig = new ImageIcon(f.getAbsolutePath());
                Image img = orig.getImage().getScaledInstance(180, 130, Image.SCALE_SMOOTH);
                icono = new ImageIcon(img);
                break;
            }
        }
        if (icono != null) {
            logoLabel = new JLabel(icono, SwingConstants.CENTER);
        } else {
            logoLabel = new JLabel("AREMI", SwingConstants.CENTER);
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            logoLabel.setForeground(ROSA_OSCURO);
        }
    }

    private void setupListeners() {
        loginButton.addActionListener(e -> intentarLogin());
        limpiarButton.addActionListener(e -> {
            usuarioTextField.setText("");
            passwordField.setText("");
            usuarioTextField.requestFocus();
        });

        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });

        btnConfigDb.addActionListener(e -> {
            ConfiguracionDBDialog dlg = new ConfiguracionDBDialog(this);
            dlg.setVisible(true);
        });
    }

    private void intentarLogin() {
        String user = usuarioTextField.getText().trim();
        String pass = new String(passwordField.getPassword());

        try {
            boolean auth = authController.iniciarSesion(user, pass);
            if (auth) {
                loginSuccessful = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos.\n\n" +
                    "Usuarios demostrativos:\n" +
                    "• fernanda / fernanda123 (Administrador)\n" +
                    "• ashley / ashley123 (Empleada)",
                    "Acceso Denegado",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isLoginSuccessful() { return loginSuccessful; }
}
