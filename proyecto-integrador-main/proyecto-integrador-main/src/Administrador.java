import javax.swing.*;

public class Administrador {
    private JPanel panel1;
    private JButton productoButton;

    public Administrador() {
        // Inicializar componentes
        productoButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Botón de producto presionado."));
    }

    public JPanel getPanel1() {
        return panel1;
    }

    // Método principal
    public static void main(String[] args) {
        JFrame frame = new JFrame("Administrador");
        frame.setContentPane(new Administrador().getPanel1()); // Cargar el JPanel
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Configuración de cierre
        frame.pack(); // Ajustar el tamaño de la ventana
        frame.setVisible(true); // Hacer visible la ventana
    }
}
