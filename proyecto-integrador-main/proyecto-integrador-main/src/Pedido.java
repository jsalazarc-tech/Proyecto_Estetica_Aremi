import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Pedido extends JFrame {
    private JPanel panel1;
    private JButton confirmarPedidoButton;
    private JButton regresarButton;
    private JTable table1;
    private JLabel backgroundLabel;  // Para la imagen de fondo

    public Pedido() {
        setTitle("Gestión de Pedidos - Salón de Belleza");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear y configurar el panel principal
        panel1 = new JPanel(new BorderLayout());
        setContentPane(panel1);

        // Agregar una imagen de fondo
        ImageIcon backgroundImage = new ImageIcon("ruta/de/tu/imagen.jpg");  // Cambia "ruta/de/tu/imagen.jpg" a la ubicación real de tu imagen
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new BorderLayout());
        panel1.add(backgroundLabel, BorderLayout.CENTER);

        // Crear la tabla y configurar el modelo
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Precio");
        modelo.addColumn("Total");
        table1 = new JTable(modelo);
        JScrollPane tableScrollPane = new JScrollPane(table1);
        tableScrollPane.setOpaque(false);
        tableScrollPane.getViewport().setOpaque(false);
        backgroundLabel.add(tableScrollPane, BorderLayout.CENTER);

        // Crear y configurar los botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        confirmarPedidoButton = new JButton("Confirmar Pedido");
        regresarButton = new JButton("Regresar");

        buttonPanel.add(confirmarPedidoButton);
        buttonPanel.add(regresarButton);
        backgroundLabel.add(buttonPanel, BorderLayout.SOUTH);

        // Agregar acción a los botones
        confirmarPedidoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Pedido confirmado");
        });

        regresarButton.addActionListener(e -> {
            dispose();
        });

        // Cargar datos de la base de datos
        cargarPedidosDesdeBD(modelo);
    }

    private void cargarPedidosDesdeBD(DefaultTableModel modelo) {
        String url = "jdbc:mysql://localhost:3306/salon_belleza";  // Cambia la URL según tu base de datos
        String usuario = "tu_usuario";  // Cambia a tu usuario de la BD
        String contraseña = "tu_contraseña";  // Cambia a tu contraseña de la BD

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña)) {
            String consultaSQL = "SELECT id, producto, cantidad, precio, (cantidad * precio) AS total FROM pedidos";
            Statement statement = conexion.createStatement();
            ResultSet resultado = statement.executeQuery(consultaSQL);

            // Llenar la tabla con los datos de los pedidos
            while (resultado.next()) {
                Object[] fila = {
                        resultado.getInt("id"),
                        resultado.getString("producto"),
                        resultado.getInt("cantidad"),
                        resultado.getDouble("precio"),
                        resultado.getDouble("total")
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Pedido().setVisible(true);
        });
    }
}
