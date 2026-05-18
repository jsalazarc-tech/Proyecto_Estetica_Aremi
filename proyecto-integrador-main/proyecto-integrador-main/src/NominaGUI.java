import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Interfaz gráfica para gestión de nómina de empleadas.
 * Calcula comisiones, bonificaciones y genera reportes.
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class NominaGUI extends JFrame {
    // Colores del tema
    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);

    // Componentes
    private JTable tablaNomina;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cboMes;
    private JComboBox<Integer> cboAno;
    private JButton btnCalcular, btnGenerarReporte;
    private JTextArea txtResumen;
    private JTextField txtSalarioBase, txtBonificacion;

    // Base de datos
    private Connection conexion;
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin1023";

    // Configuración de nómina
    private static final double SALARIO_BASE_DEFAULT = 1300000; // Salario mínimo Colombia 2024

    public NominaGUI() {
        conectarDB();
        initComponents();
    }

    private void conectarDB() {
        try {
            conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error de conexión: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setTitle("💰 Gestión de Nómina - Salón Aremi");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior - Filtros
        JPanel topPanel = createTopPanel();

        // Panel central - Tabla
        JPanel centerPanel = createCenterPanel();

        // Panel inferior - Resumen
        JPanel bottomPanel = createBottomPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Calcular automáticamente al abrir
        calcularNomina();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarConexion();
            }
        });
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);

        // Selector de mes
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                         "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        cboMes = new JComboBox<>(meses);
        cboMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        // Selector de año
        Integer[] anos = new Integer[5];
        int anoActual = LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) {
            anos[i] = anoActual - 2 + i;
        }
        cboAno = new JComboBox<>(anos);
        cboAno.setSelectedItem(anoActual);

        panel.add(new JLabel("Mes:"));
        panel.add(cboMes);
        panel.add(new JLabel("Año:"));
        panel.add(cboAno);

        // Campos de configuración
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Salario Base:"));
        txtSalarioBase = new JTextField(String.valueOf(SALARIO_BASE_DEFAULT), 10);
        panel.add(txtSalarioBase);

        panel.add(new JLabel("Bonificación:"));
        txtBonificacion = new JTextField("0", 8);
        panel.add(txtBonificacion);

        // Botones
        btnCalcular = createStyledButton("Calcular Nómina", ROSA_OSCURO);
        btnCalcular.addActionListener(e -> calcularNomina());
        panel.add(btnCalcular);

        btnGenerarReporte = createStyledButton("Generar Reporte", new Color(34, 139, 34));
        btnGenerarReporte.addActionListener(e -> generarReporte());
        panel.add(btnGenerarReporte);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columnas = {"Empleada", "Especialidad", "Servicios", "Ingresos",
                           "% Comisión", "Comisión", "Salario Base", "Total a Pagar"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaNomina = new JTable(modeloTabla);
        tablaNomina.setRowHeight(25);
        tablaNomina.getTableHeader().setBackground(ROSA_OSCURO);
        tablaNomina.getTableHeader().setForeground(Color.WHITE);
        tablaNomina.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tablaNomina);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_OSCURO, 2),
            "Resumen General",
            0, 0, new Font("Arial", Font.BOLD, 12), ROSA_OSCURO
        ));

        txtResumen = new JTextArea(6, 40);
        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResumen.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(txtResumen);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Calcula la nómina del mes seleccionado
     */
    private void calcularNomina() {
        modeloTabla.setRowCount(0);

        int mes = cboMes.getSelectedIndex() + 1;
        int ano = (Integer) cboAno.getSelectedItem();

        double salarioBase = 0;
        double bonificacion = 0;

        try {
            salarioBase = Double.parseDouble(txtSalarioBase.getText().trim());
            bonificacion = Double.parseDouble(txtBonificacion.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error en valores numéricos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Consulta para obtener servicios por empleada en el mes seleccionado
        String sql = "SELECT e.nombre as empleada, COUNT(*) as servicios, SUM(s.precio) as total_ingresos " +
                    "FROM servicios s " +
                    "INNER JOIN empleadas e ON s.empleada_id = e.id " +
                    "WHERE MONTH(s.fecha) = ? " +
                    "AND YEAR(s.fecha) = ? " +
                    "GROUP BY e.nombre";

        Map<String, Double[]> datosNomina = new HashMap<>();
        double totalComisiones = 0;
        double totalSalarios = 0;
        int totalServicios = 0;

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, mes);
            pstmt.setInt(2, ano);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String empleada = rs.getString("empleada");
                int servicios = rs.getInt("servicios");
                double ingresosGenerados = rs.getDouble("total_ingresos");

                // Obtener comisión personalizada de SeguridadManager
                double porcentajeComision = SeguridadManager.getComisionEmpleada(empleada);
                String especialidad = SeguridadManager.getEspecialidadEmpleada(empleada);

                double comision = ingresosGenerados * porcentajeComision;
                double totalPagar = salarioBase + comision + bonificacion;

                Object[] fila = {
                    empleada,
                    especialidad,
                    servicios,
                    String.format("$%,.0f", ingresosGenerados),
                    String.format("%.0f%%", porcentajeComision * 100),
                    String.format("$%,.0f", comision),
                    String.format("$%,.0f", salarioBase),
                    String.format("$%,.0f", totalPagar)
                };

                modeloTabla.addRow(fila);

                totalComisiones += comision;
                totalSalarios += totalPagar;
                totalServicios += servicios;
            }

            // Actualizar resumen
            actualizarResumen(totalServicios, totalComisiones, totalSalarios, mes, ano);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al calcular nómina: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el área de resumen con los totales
     */
    private void actualizarResumen(int totalServicios, double totalComisiones,
                                   double totalSalarios, int mes, int ano) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("========================================================\n");
        resumen.append(String.format("  RESUMEN DE NOMINA - %s %d%n",
            cboMes.getSelectedItem(), ano));
        resumen.append("========================================================\n");
        resumen.append(String.format("  Total de Empleadas:        %-5d%n",
            modeloTabla.getRowCount()));
        resumen.append(String.format("  Total de Servicios:        %-5d%n",
            totalServicios));
        resumen.append(String.format("  Total en Comisiones:       $%,.0f%n",
            totalComisiones));
        resumen.append(String.format("  Total Nomina a Pagar:      $%,.0f%n",
            totalSalarios));
        resumen.append("========================================================\n");

        txtResumen.setText(resumen.toString());
    }

    /**
     * Genera un reporte detallado en consola (puede extenderse a PDF)
     */
    private void generarReporte() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay datos para generar reporte",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder reporte = new StringBuilder();
        reporte.append("\n========================================\n");
        reporte.append("     REPORTE DE NÓMINA - SALÓN AREMI\n");
        reporte.append("========================================\n");
        reporte.append("Periodo: ").append(cboMes.getSelectedItem())
               .append(" ").append(cboAno.getSelectedItem()).append("\n");
        reporte.append("Fecha Generación: ")
               .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
               .append("\n\n");

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            reporte.append("----------------------------------------\n");
            reporte.append("Empleada: ").append(modeloTabla.getValueAt(i, 0)).append("\n");
            reporte.append("  Especialidad:         ").append(modeloTabla.getValueAt(i, 1)).append("\n");
            reporte.append("  Servicios realizados: ").append(modeloTabla.getValueAt(i, 2)).append("\n");
            reporte.append("  Ingresos generados:   ").append(modeloTabla.getValueAt(i, 3)).append("\n");
            reporte.append("  Porcentaje comisión:  ").append(modeloTabla.getValueAt(i, 4)).append("\n");
            reporte.append("  Comisión:             ").append(modeloTabla.getValueAt(i, 5)).append("\n");
            reporte.append("  Salario base:         ").append(modeloTabla.getValueAt(i, 6)).append("\n");
            reporte.append("  TOTAL A PAGAR:        ").append(modeloTabla.getValueAt(i, 7)).append("\n");
        }

        reporte.append("\n").append(txtResumen.getText());

        // Mostrar reporte
        JTextArea areaReporte = new JTextArea(reporte.toString());
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(areaReporte);
        scroll.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scroll,
            "Reporte de Nómina", JOptionPane.INFORMATION_MESSAGE);

        // Imprimir en consola
        System.out.println(reporte.toString());
    }

    private void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 32));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NominaGUI gui = new NominaGUI();
            gui.setVisible(true);
        });
    }
}