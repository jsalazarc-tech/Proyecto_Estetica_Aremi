import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;

public class MainApplication {
    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);
    private static final Color MORADO_SUAVE = new Color(230, 190, 255);

    public static void main(String[] args) {
        // Establecer Look & Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore
        }

        SwingUtilities.invokeLater(() -> {
            // Diálogo de Login
            LoginSystemAremi loginDialog = new LoginSystemAremi();
            loginDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            loginDialog.setVisible(true);

            if (loginDialog.isLoginSuccessful()) {
                mostrarVentanaPrincipal();
            } else {
                System.exit(0);
            }
        });
    }

    public static void mostrarVentanaPrincipal() {
        JFrame frame = new JFrame("Salón de Belleza AREMI - Sistema de Gestión");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(980, 680);
        frame.setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(new Color(250, 245, 248));

        // 1. Barra superior: Branding y Usuario activo
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(ROSA_OSCURO);
        topHeader.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Logo / Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titlePanel.setOpaque(false);

        JLabel lblLogo = cargarIconoLogo(38, 38);
        JLabel lblTitulo = new JLabel("SALÓN DE BELLEZA AREMI");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        titlePanel.add(lblLogo);
        titlePanel.add(lblTitulo);
        topHeader.add(titlePanel, BorderLayout.WEST);

        // Tarjeta de sesión de usuario
        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userBadge.setOpaque(false);

        String nombreUser = SeguridadManager.getNombreCompletoActual();
        String rolUser = SeguridadManager.getRolActual().toUpperCase();
        JLabel lblUser = new JLabel("👤 " + nombreUser + "  [" + rolUser + "]");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(Color.WHITE);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrarSesion.setBackground(Color.WHITE);
        btnCerrarSesion.setForeground(ROSA_OSCURO);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(e -> {
            frame.dispose();
            SeguridadManager.cerrarSesion();
            main(new String[]{});
        });

        userBadge.add(lblUser);
        userBadge.add(btnCerrarSesion);
        topHeader.add(userBadge, BorderLayout.EAST);

        contentPanel.add(topHeader, BorderLayout.NORTH);

        // 2. Panel Central: Tarjetas / Menú de opciones
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Banner informativo
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Color.WHITE);
        banner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1, true),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel lblSaludo = new JLabel("Panel de Control Principal - " + (SeguridadManager.esAdministrador() ? "Modo Administrador" : "Modo Empleada"));
        lblSaludo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSaludo.setForeground(new Color(120, 40, 70));
        banner.add(lblSaludo, BorderLayout.WEST);

        JLabel lblFecha = new JLabel("📅 Hoy: " + LocalDate.now());
        lblFecha.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblFecha.setForeground(Color.GRAY);
        banner.add(lblFecha, BorderLayout.EAST);
        centerPanel.add(banner, BorderLayout.NORTH);

        // Cuadrícula de botones modulares
        JPanel gridMenu = new JPanel(new GridLayout(0, 3, 20, 20));
        gridMenu.setOpaque(false);

        if (SeguridadManager.esAdministrador()) {
            // Opciones de Administrador
            gridMenu.add(createMenuCard("👥 Gestión de Clientes", "Registrar, consultar y actualizar clientes", new Color(240, 248, 255), e -> new InterfazClientes().setVisible(true)));
            gridMenu.add(createMenuCard("💅 Empleadas y Comisiones", "Administrar personal y sus porcentajes (20%, 30%, 50%)", new Color(255, 245, 248), e -> new GestionEmpleadasGUI().setVisible(true)));
            gridMenu.add(createMenuCard("✨ Catálogo de Servicios", "Catálogo de tarifas, categorías y tiempos", new Color(248, 248, 255), e -> new GestionServiciosGUI().setVisible(true)));
            gridMenu.add(createMenuCard("📅 Agenda y Citas", "Agendar citas y validar disponibilidad de horarios", new Color(245, 255, 250), e -> new AgendarCitaGUI().setVisible(true)));
            gridMenu.add(createMenuCard("✂ Servicios Realizados", "Registrar servicios efectivamente prestados", new Color(255, 250, 240), e -> new ServiciosRealizadosGUI().setVisible(true)));
            gridMenu.add(createMenuCard("💰 Liquidación de Comisiones", "Calcular y liquidar comisiones con reporte PDF", new Color(255, 240, 245), e -> new LiquidacionComisionesGUI().setVisible(true)));
            gridMenu.add(createMenuCard("💳 Facturación / Pagos", "Registro de comprobantes de pago e inventario", new Color(245, 245, 245), e -> new Pago()));
            gridMenu.add(createMenuCard("⚙ Conexión MySQL", "Configurar credenciales del servidor de base de datos", new Color(240, 255, 255), e -> new ConfiguracionDBDialog(frame).setVisible(true)));
        } else {
            // Opciones de Empleada
            gridMenu.add(createMenuCard("📅 Mi Agenda de Citas", "Consultar citas programadas asignadas", new Color(245, 255, 250), e -> new AgendarCitaGUI().setVisible(true)));
            gridMenu.add(createMenuCard("✂ Registrar Mi Servicio", "Confirmar servicio realizado para ganar comisión", new Color(255, 250, 240), e -> new ServiciosRealizadosGUI().setVisible(true)));
            gridMenu.add(createMenuCard("💵 Mi Saldo y Comisiones", "Ver servicios realizados y saldo generado este mes", new Color(255, 240, 245), e -> mostrarMiSaldo(frame)));
        }

        centerPanel.add(new JScrollPane(gridMenu), BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. Barra inferior de pie
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        JLabel lblCopy = new JLabel("Salón de Belleza Aremi © 2026 | Sistema de Gestión de Citas y Liquidación de Comisiones | Java & MySQL");
        lblCopy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopy.setForeground(Color.GRAY);
        footer.add(lblCopy);
        contentPanel.add(footer, BorderLayout.SOUTH);

        frame.setContentPane(contentPanel);
        frame.setVisible(true);
    }

    private static JPanel createMenuCard(String titulo, String descripcion, Color bgColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 210, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(ROSA_OSCURO);

        JLabel lblDesc = new JLabel("<html>" + descripcion + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(80, 80, 80));

        JButton btnOpen = new JButton("Abrir Módulo →");
        btnOpen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOpen.setBackground(ROSA_OSCURO);
        btnOpen.setForeground(Color.WHITE);
        btnOpen.setFocusPainted(false);
        btnOpen.addActionListener(action);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(btnOpen, BorderLayout.SOUTH);

        return card;
    }

    private static JLabel cargarIconoLogo(int w, int h) {
        try {
            File f = new File("src/AREMI.png");
            if (!f.exists()) f = new File("proyecto-integrador-main/src/AREMI.png");
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(scaled));
            }
        } catch (Exception e) {}
        return new JLabel("✨");
    }

    private static void mostrarMiSaldo(JFrame parent) {
        String nombreEmpleada = SeguridadManager.getNombreCompletoActual();
        String especialidad = SeguridadManager.getEspecialidadActual();
        double porcentajeComision = SeguridadManager.getComisionActual();

        int mesActual = LocalDate.now().getMonthValue();
        int anoActual = LocalDate.now().getYear();

        String sql = "SELECT COUNT(*) as servicios, COALESCE(SUM(valor_cobrado), 0) as total_ingresos, COALESCE(SUM(comision_calculada), 0) as total_comision " +
                     "FROM servicios_realizados " +
                     "WHERE (nombre_empleada = ? OR nombre_empleada LIKE ?) " +
                     "AND MONTH(fecha) = ? AND YEAR(fecha) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreEmpleada);
            pstmt.setString(2, "%" + nombreEmpleada + "%");
            pstmt.setInt(3, mesActual);
            pstmt.setInt(4, anoActual);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int servicios = rs.getInt("servicios");
                    double ingresos = rs.getDouble("total_ingresos");
                    double comision = rs.getDouble("total_comision");

                    String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                     "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

                    String msg = "═══════════════════════════════════════\n" +
                                 "       MI SALDO - " + meses[mesActual - 1] + " " + anoActual + "\n" +
                                 "═══════════════════════════════════════\n\n" +
                                 "  Empleada: " + nombreEmpleada + "\n" +
                                 "  Especialidad: " + especialidad + "\n" +
                                 "  Porcentaje configurado: " + String.format("%.0f%%", porcentajeComision * 100) + "\n\n" +
                                 "  ─────────────────────────────────────\n" +
                                 "  Servicios Realizados: " + servicios + "\n" +
                                 "  Total Facturado:      $" + String.format("%,.0f", ingresos) + "\n" +
                                 "  ─────────────────────────────────────\n" +
                                 "  COMISIÓN GENERADA:    $" + String.format("%,.0f", comision) + "\n" +
                                 "═══════════════════════════════════════\n";

                    JTextArea ta = new JTextArea(msg);
                    ta.setEditable(false);
                    ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
                    JOptionPane.showMessageDialog(parent, new JScrollPane(ta), "Mi Saldo - " + nombreEmpleada, JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Error al consultar saldo: " + e.getMessage());
        }
    }
}
