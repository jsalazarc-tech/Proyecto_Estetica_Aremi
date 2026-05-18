import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class MainApplication {
    // Conexión a base de datos
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin1023";
    // Imagen de fondo cargada una sola vez (optimización de rendimiento)
    private static Image backgroundImage = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Mostrar el diálogo de inicio de sesión primero
            LoginSystemAremi loginDialog = new LoginSystemAremi();
            loginDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            loginDialog.setVisible(true);

            // Si el inicio de sesión es exitoso, se mostrará la ventana principal
            if (loginDialog.isLoginSuccessful()) {
                JFrame frame = new JFrame("Sistema de Gestión de Spa - Salón Aremi");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Cargar imagen de fondo UNA SOLA VEZ
                if (backgroundImage == null) {
                    try {
                        backgroundImage = new ImageIcon("C:\\Users\\Juan Sebastian\\IdeaProjects\\Proyecto-Integrador\\src\\6911600.jpg").getImage();
                    } catch (Exception e) {
                        System.err.println("Error cargando imagen de fondo: " + e.getMessage());
                    }
                }

                // Crear el panel principal que contendrá todo
                final Image bgImage = backgroundImage; // Variable final para uso en clase anónima
                JPanel mainPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (bgImage != null) {
                            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };

                // Crear una barra de botones en la parte superior
                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                topPanel.setOpaque(false);

                JButton btnAgendarCita = new JButton("Agendar Cita");
                btnAgendarCita.addActionListener(e -> {
                    new AgendarCitaGUI().setVisible(true);
                });

                JButton btnGestionClientes = new JButton("Gestion de Clientes");
                btnGestionClientes.addActionListener(e -> {
                    new InterfazClientes().setVisible(true);
                });

                // Botón de Nómina (solo para administradores)
                JButton btnNomina = new JButton("Nomina");
                btnNomina.addActionListener(e -> {
                    if (SeguridadManager.tienePermiso("ver_nomina")) {
                        new NominaGUI().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                            "No tiene permisos para ver la nomina",
                            "Acceso Denegado",
                            JOptionPane.WARNING_MESSAGE);
                    }
                });

                // Botón Mi Saldo (para empleadas)
                JButton btnMiSaldo = new JButton("Mi Saldo");
                btnMiSaldo.addActionListener(e -> {
                    if (SeguridadManager.esEmpleado()) {
                        mostrarMiSaldo(frame);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                            "Esta opción es solo para empleadas.\nUse 'Nomina' para ver todos los saldos.",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                });

                topPanel.add(btnAgendarCita);
                topPanel.add(btnGestionClientes);
                topPanel.add(btnNomina);
                topPanel.add(btnMiSaldo);

                // Los botones estan siempre habilitados, la validacion se hace al hacer clic

                // Crear una barra de botones en la parte inferior
                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                bottomPanel.setOpaque(false);

                JButton btnPago = new JButton("Pago");
                btnPago.addActionListener(e -> {
                    new Pago();
                });

                JButton btnGenerarFactura = new JButton("Generar Factura");
                btnGenerarFactura.addActionListener(e -> {
                    mostrarDialogoFactura(frame);
                });

                bottomPanel.add(btnPago);
                bottomPanel.add(btnGenerarFactura);

                // Agregar los paneles al mainPanel
                mainPanel.add(topPanel, BorderLayout.NORTH);
                mainPanel.add(bottomPanel, BorderLayout.SOUTH);

                // Configurar la ventana principal
                frame.setSize(900, 650);
                frame.setLocationRelativeTo(null);
                frame.add(mainPanel);
                frame.setVisible(true);

                // Cerrar sesión al cerrar la aplicación
                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        SeguridadManager.cerrarSesion();
                    }
                });
            }
        });
    }

    private static void mostrarDialogoFactura(JFrame parent) {
        // Crear un diálogo personalizado
        JDialog dialog = new JDialog(parent, "Buscar Factura", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Panel para la entrada de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Ingrese el nombre o teléfono del cliente:");
        JTextField searchField = new JTextField(20);

        searchPanel.add(label, BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Panel para los resultados
        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(resultList);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton searchButton = new JButton("Buscar");
        JButton generateButton = new JButton("Generar Factura");
        JButton cancelButton = new JButton("Cancelar");

        buttonPanel.add(searchButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);

        // Agregar todos los paneles al diálogo
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(resultPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Configurar acciones de los botones
        Facturas facturas = new Facturas();

        searchButton.addActionListener(e -> {
            String criterio = searchField.getText().trim();
            if (!criterio.isEmpty()) {
                listModel.clear();
                List<String> facturasEncontradas = facturas.buscarFacturasPorNombreOTelefono(criterio);
                if (facturasEncontradas.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "No se encontraron facturas para el criterio proporcionado.",
                            "Sin resultados",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (String facturaId : facturasEncontradas) {
                        listModel.addElement(facturaId);
                    }
                }
            }
        });

        generateButton.addActionListener(e -> {
            String selectedFactura = resultList.getSelectedValue();
            if (selectedFactura != null) {
                facturas.generarFactura(selectedFactura);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, seleccione una factura de la lista.",
                        "Selección requerida",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Configurar el diálogo
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Muestra el saldo y comisiones de la empleada actual
     */
    private static void mostrarMiSaldo(JFrame parent) {
        String nombreEmpleada = SeguridadManager.getNombreCompletoActual();
        String especialidad = SeguridadManager.getEspecialidadActual();
        double porcentajeComision = SeguridadManager.getComisionActual();

        int mesActual = LocalDate.now().getMonthValue();
        int anoActual = LocalDate.now().getYear();

        String sql = "SELECT COUNT(*) as servicios, COALESCE(SUM(s.precio), 0) as total_ingresos " +
                    "FROM servicios s " +
                    "INNER JOIN empleadas e ON s.empleada_id = e.id " +
                    "WHERE e.nombre = ? " +
                    "AND MONTH(s.fecha) = ? " +
                    "AND YEAR(s.fecha) = ?";

        try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setString(1, nombreEmpleada);
            pstmt.setInt(2, mesActual);
            pstmt.setInt(3, anoActual);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int servicios = rs.getInt("servicios");
                double ingresos = rs.getDouble("total_ingresos");
                double comision = ingresos * porcentajeComision;

                String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                 "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

                StringBuilder mensaje = new StringBuilder();
                mensaje.append("═══════════════════════════════════════\n");
                mensaje.append("         MI SALDO - ").append(meses[mesActual - 1]).append(" ").append(anoActual).append("\n");
                mensaje.append("═══════════════════════════════════════\n\n");
                mensaje.append("  Empleada: ").append(nombreEmpleada).append("\n");
                mensaje.append("  Especialidad: ").append(especialidad).append("\n");
                mensaje.append("  Porcentaje de comisión: ").append(String.format("%.0f%%", porcentajeComision * 100)).append("\n\n");
                mensaje.append("  ─────────────────────────────────────\n");
                mensaje.append("  Servicios realizados: ").append(servicios).append("\n");
                mensaje.append("  Ingresos generados:   $").append(String.format("%,.2f", ingresos)).append("\n");
                mensaje.append("  ─────────────────────────────────────\n");
                mensaje.append("  COMISIÓN GANADA:      $").append(String.format("%,.2f", comision)).append("\n");
                mensaje.append("═══════════════════════════════════════\n");

                JTextArea textArea = new JTextArea(mensaje.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setBackground(new Color(250, 250, 250));

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 350));

                JOptionPane.showMessageDialog(parent, scrollPane,
                    "Mi Saldo - " + nombreEmpleada,
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                "Error al consultar saldo: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
