import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Pago {
    private JPanel panel1;
    private JRadioButton efectivoRadioButton;
    private JRadioButton tarjetaDeCreditoDebitoRadioButton;
    private JRadioButton tranferenciaRadioButton;
    private JButton regresarButton;
    private JButton pagarButton;

    public Pago() {
        JFrame frame = new JFrame("Pago");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        panel1 = new JPanel(new BorderLayout()); // Usar BorderLayout para panel1
        panel1.setBackground(new Color(255, 224, 224)); // Rosa suave
        panel1.setBorder(new EmptyBorder(20, 20, 20, 20));
        frame.setContentPane(panel1);

        // Agregar el ícono
        String iconPath = "C:\\Users\\Juan Sebastian\\IdeaProjects\\Proyecto-Integrador\\src\\1811.png_860.png";
        ImageIcon icon = new ImageIcon(iconPath);
        frame.setIconImage(icon.getImage());

        // Agregar el logo AREMI.png
        String logoPath = "C:\\Users\\Juan Sebastian\\IdeaProjects\\Proyecto-Integrador\\src\\AREMI.png";
        ImageIcon logoIcon = new ImageIcon(logoPath);
        Image logoImage = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
        panel1.add(logoLabel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("Seleccione un método de pago", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(219, 112, 147)); // Rosa oscuro
        panel1.add(titleLabel, BorderLayout.NORTH);

        JPanel radioPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        radioPanel.setBackground(new Color(255, 224, 224)); // Rosa suave

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(efectivoRadioButton);
        grupo.add(tarjetaDeCreditoDebitoRadioButton);
        grupo.add(tranferenciaRadioButton);

        efectivoRadioButton = new JRadioButton("Efectivo");
        tarjetaDeCreditoDebitoRadioButton = new JRadioButton("Tarjeta de Crédito/Débito");
        tranferenciaRadioButton = new JRadioButton("Transferencia");

        radioPanel.add(efectivoRadioButton);
        radioPanel.add(tarjetaDeCreditoDebitoRadioButton);
        radioPanel.add(tranferenciaRadioButton);

        panel1.add(radioPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(255, 224, 224)); // Rosa suave

        regresarButton = new JButton("Regresar");
        regresarButton.setBackground(new Color(219, 112, 147)); // Rosa oscuro
        regresarButton.setForeground(Color.WHITE);
        regresarButton.setFocusPainted(false);
        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Pedido(); // Asegúrate de tener esta clase
            }
        });

        pagarButton = new JButton("Pagar");
        pagarButton.setBackground(new Color(219, 112, 147)); // Rosa oscuro
        pagarButton.setForeground(Color.WHITE);
        pagarButton.setFocusPainted(false);
        pagarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarSeleccionPago()) {
                    PagoDetailsDialog dialog = new PagoDetailsDialog(frame);
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Por favor seleccione un método de pago",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(regresarButton);
        buttonPanel.add(pagarButton);

        panel1.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private boolean validarSeleccionPago() {
        return efectivoRadioButton.isSelected() ||
                tarjetaDeCreditoDebitoRadioButton.isSelected() ||
                tranferenciaRadioButton.isSelected();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Pago();
            }
        });
    }
}
