package view;

import util.SeguridadManager;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Panel de Control Principal de la Aplicación Salón de Belleza AREMI.
 * Menú modular con control de acceso basado en roles (Administrador / Empleada).
 */
public class MainApplication {
    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
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

        // 1. Barra superior
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(ROSA_OSCURO);
        topHeader.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titlePanel.setOpaque(false);

        JLabel lblLogo = cargarIconoLogo(38, 38);
        JLabel lblTitulo = new JLabel("SALÓN DE BELLEZA AREMI");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        titlePanel.add(lblLogo);
        titlePanel.add(lblTitulo);
        topHeader.add(titlePanel, BorderLayout.WEST);

        // Tarjeta de sesión
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
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.addActionListener(e -> {
            frame.dispose();
            SeguridadManager.cerrarSesion();
            main(new String[]{});
        });

        userBadge.add(lblUser);
        userBadge.add(btnCerrarSesion);
        topHeader.add(userBadge, BorderLayout.EAST);

        contentPanel.add(topHeader, BorderLayout.NORTH);

        // 2. Panel Central con tarjetas modulares
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        boolean esAdmin = SeguridadManager.esAdministrador();
        JLabel lblRolSub = new JLabel("Panel de Control - Modo " + (esAdmin ? "Administrador General" : "Profesional / Empleada"));
        lblRolSub.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRolSub.setForeground(new Color(120, 50, 80));
        centerPanel.add(lblRolSub, BorderLayout.NORTH);

        JPanel gridCards = new JPanel(new GridLayout(esAdmin ? 2 : 1, esAdmin ? 3 : 2, 20, 20));
        gridCards.setOpaque(false);

        // Módulos
        if (esAdmin) {
            gridCards.add(crearCardModulo("👥 Gestión de Clientes", "Registrar, consultar y actualizar datos de clientes.", e -> new InterfazClientes().setVisible(true)));
            gridCards.add(crearCardModulo("💅 Empleadas y Comisiones", "Administrar personal y sus porcentajes (20%, 30%, 50%).", e -> new GestionEmpleadasGUI().setVisible(true)));
            gridCards.add(crearCardModulo("✨ Catálogo de Servicios", "Catálogo de tarifas, categorías y duración estimada.", e -> new GestionServiciosGUI().setVisible(true)));
        }

        gridCards.add(crearCardModulo("📅 Agenda y Citas", "Agendar citas y validar disponibilidad de horarios.", e -> new AgendarCitaGUI().setVisible(true)));
        gridCards.add(crearCardModulo("✂ Servicios Realizados", "Registrar servicios efectivamente prestados.", e -> new ServiciosRealizadosGUI().setVisible(true)));

        if (esAdmin) {
            gridCards.add(crearCardModulo("💰 Liquidación de Comisiones", "Calcular y liquidar comisiones con reporte PDF.", e -> new LiquidacionComisionesGUI().setVisible(true)));
        }

        centerPanel.add(gridCards, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        frame.setContentPane(contentPanel);
        frame.setVisible(true);
    }

    private static JPanel crearCardModulo(String titulo, String descripcion, java.awt.event.ActionListener accion) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROSA_CLARO, 1, true),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(ROSA_OSCURO);

        JLabel lblDesc = new JLabel("<html><body style='width: 170px; color: #555555;'>" + descripcion + "</body></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnAbrir = new JButton("Abrir Módulo →");
        btnAbrir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAbrir.setBackground(ROSA_OSCURO);
        btnAbrir.setForeground(Color.WHITE);
        btnAbrir.setOpaque(true);
        btnAbrir.setBorderPainted(false);
        btnAbrir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbrir.addActionListener(accion);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(btnAbrir, BorderLayout.SOUTH);

        return card;
    }

    private static JLabel cargarIconoLogo(int w, int h) {
        String[] rutas = {"src/AREMI.png", "proyecto-integrador-main/src/AREMI.png", "AREMI.png"};
        for (String r : rutas) {
            File f = new File(r);
            if (f.exists()) {
                ImageIcon orig = new ImageIcon(f.getAbsolutePath());
                Image img = orig.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        }
        return new JLabel("💅");
    }
}
