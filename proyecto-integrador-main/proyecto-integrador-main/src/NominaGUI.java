import javax.swing.*;
import java.awt.*;

public class NominaGUI extends JFrame {
    public NominaGUI() {
        // Redirige al nuevo módulo oficial de Liquidación de Comisiones del Salón Aremi
        SwingUtilities.invokeLater(() -> {
            LiquidacionComisionesGUI gui = new LiquidacionComisionesGUI();
            gui.setVisible(true);
            dispose();
        });
    }
}
