import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;

public class PagoDetailsDialog extends JDialog {
    private JTextField nombreTextField;
    private JTextField montoTextField;
    private JTextField motivoTextField;
    private JComboBox<String> servicioComboBox;
    private JButton guardarButton;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin1023";

    public PagoDetailsDialog(JFrame parent) {
        super(parent, "Detalles del Pago", true);
        setLayout(new BorderLayout());
        setBackground(new Color(255, 224, 224)); // Rosa suave

        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 224, 224)); // Rosa suave

        JLabel titleLabel = new JLabel("Ingrese los detalles del pago", SwingConstants.CENTER);

        titleLabel.setForeground(new Color(219, 112, 147)); // Rosa oscuro
        add(titleLabel, BorderLayout.NORTH);

        mainPanel.add(new JLabel("Nombre:"));
        nombreTextField = new JTextField();
        mainPanel.add(nombreTextField);

        mainPanel.add(new JLabel("Monto:"));
        montoTextField = new JTextField();
        mainPanel.add(montoTextField);

        mainPanel.add(new JLabel("Motivo:"));
        motivoTextField = new JTextField();
        mainPanel.add(motivoTextField);

        mainPanel.add(new JLabel("Servicio:"));
        servicioComboBox = new JComboBox<>(new String[]{"Uñas", "Depilación de Cera", "Bikini", "Diseño de Cejas", "Manicure", "Pedicure"});
        mainPanel.add(servicioComboBox);

        guardarButton = new JButton("Guardar");
        guardarButton.setBackground(new Color(219, 112, 147)); // Rosa oscuro
        guardarButton.setForeground(Color.WHITE);
        guardarButton.setFocusPainted(false);
        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarDetalles();
            }
        });
        mainPanel.add(guardarButton);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
    }

    private void guardarDetalles() {
        String nombre = nombreTextField.getText();
        String monto = montoTextField.getText();
        String motivo = motivoTextField.getText();
        String servicio = (String) servicioComboBox.getSelectedItem();

        if (nombre.isEmpty() || monto.isEmpty() || motivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Crear la tabla si no existe
            crearTablaPagos(conn);

            String sql = "INSERT INTO pagos (nombre, monto, motivo, servicio) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setString(2, monto);
                stmt.setString(3, motivo);
                stmt.setString(4, servicio);
                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Pago registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            generarFacturaPDF(nombre, monto, motivo, servicio);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el pago: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearTablaPagos(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS pagos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "nombre VARCHAR(255) NOT NULL," +
                "monto DECIMAL(10, 2) NOT NULL," +
                "motivo VARCHAR(255) NOT NULL," +
                "servicio VARCHAR(255) NOT NULL," +
                "fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private void generarFacturaPDF(String nombre, String monto, String motivo, String servicio) {
        try {
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream("Factura.pdf"));
            documento.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            documento.add(new Paragraph("FACTURA DE COMPRA", fontTitle));
            documento.add(new Paragraph("Nombre del Comprador: " + nombre));
            documento.add(new Paragraph("Monto: " + monto));
            documento.add(new Paragraph("Motivo: " + motivo));
            documento.add(new Paragraph("Servicio: " + servicio));

            String rutaImagen = "C:\\Users\\Juan Sebastian\\IdeaProjects\\Proyecto-Integrador\\src\\AREMI.png";
            Image imagenLogo = Image.getInstance(rutaImagen);
            imagenLogo.scaleToFit(100, 100);
            imagenLogo.setAlignment(Image.ALIGN_RIGHT);
            documento.add(imagenLogo);

            documento.close();
            System.out.println("La factura se ha generado y guardado en Factura.pdf");
        } catch (DocumentException | IOException e) {
            System.out.println("Error al generar el certificado: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Pago Details Dialog");
                PagoDetailsDialog dialog = new PagoDetailsDialog(frame);
                dialog.setVisible(true);
            }
        });
    }
}
