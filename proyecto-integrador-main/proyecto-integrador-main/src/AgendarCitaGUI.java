import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AgendarCitaGUI extends JFrame {
    private JTextField txtNombre, txtTelefono;
    private JComboBox<String> cboServicio, cboHora;
    private JComboBox<Integer> cboDia, cboMes, cboAno;
    private JButton btnAgendar, btnCancelar;
    private Color primaryColor = new Color(219, 112, 147); // Cambiado a un rosa más suave
    private Color secondaryColor = new Color(255, 255, 255);

    public AgendarCitaGUI() {
        setTitle("💅 Agenda de Citas - Salón de Belleza");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de contenido
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Componentes
        txtNombre = createStyledTextField();
        txtTelefono = createStyledTextField();

        // Servicios actualizados según el salón
        String[] servicios = {
                "Pedicure",
                "Depilación de Cejas",
                "Depilación de Bikini",
                "Diseño de Cejas",
                "Depilación Facial",
                "Depilación de Piernas",
                "Depilación de Brazos",
                "Manicure"
        };
        cboServicio = createStyledComboBox(servicios);

        // Fecha
        JPanel fechaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        fechaPanel.setBackground(Color.WHITE);

        Integer[] dias = new Integer[31];
        for(int i = 1; i <= 31; i++) dias[i-1] = i;
        cboDia = createStyledComboBox(dias);

        Integer[] meses = new Integer[12];
        for(int i = 1; i <= 12; i++) meses[i-1] = i;
        cboMes = createStyledComboBox(meses);

        Integer[] anos = new Integer[10];
        int anoActual = java.time.Year.now().getValue();
        for(int i = 0; i < 10; i++) anos[i] = anoActual + i;
        cboAno = createStyledComboBox(anos);

        fechaPanel.add(cboDia);
        fechaPanel.add(new JLabel("/"));
        fechaPanel.add(cboMes);
        fechaPanel.add(new JLabel("/"));
        fechaPanel.add(cboAno);

        // Horario actualizado
        String[] horas = {
                "09:00", "10:00", "11:00", "12:00",
                "14:00", "15:00", "16:00", "17:00"  // Quitado 13:00 asumiendo hora de almuerzo
        };
        cboHora = createStyledComboBox(horas);

        // Añadir componentes
        addFormField(contentPanel, "Nombre:", txtNombre, gbc, 0);
        addFormField(contentPanel, "Teléfono:", txtTelefono, gbc, 1);
        addFormField(contentPanel, "Servicio:", cboServicio, gbc, 2);
        addFormField(contentPanel, "Fecha:", fechaPanel, gbc, 3);
        addFormField(contentPanel, "Hora:", cboHora, gbc, 4);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        btnAgendar = createStyledButton("Agendar Cita", new Color(219, 112, 147)); // Rosa para combinar
        btnCancelar = createStyledButton("Cancelar", new Color(128, 128, 128)); // Gris más suave

        buttonPanel.add(btnAgendar);
        buttonPanel.add(btnCancelar);

        // Eventos
        btnAgendar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarCita();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(primaryColor));
        return combo;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(33, 33, 33));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void guardarCita() {
        if (!validarCampos()) {
            return;
        }

        try {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String servicio = cboServicio.getSelectedItem().toString();
            String hora = cboHora.getSelectedItem().toString();

            String fechaStr = String.format("%d-%02d-%02d",
                    cboAno.getSelectedItem(),
                    cboMes.getSelectedItem(),
                    cboDia.getSelectedItem());
            java.sql.Date fechaSQL = java.sql.Date.valueOf(fechaStr);

            DatabaseConnection.guardarCita(nombre, telefono, servicio, fechaSQL, hora);

            JOptionPane.showMessageDialog(this,
                    "¡Cita agendada exitosamente! ✨",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la cita: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el nombre");
            return false;
        }
        if (txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el teléfono");
            return false;
        }

        try {
            String fechaStr = String.format("%d-%02d-%02d",
                    cboAno.getSelectedItem(),
                    cboMes.getSelectedItem(),
                    cboDia.getSelectedItem());
            java.sql.Date.valueOf(fechaStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "La fecha seleccionada no es válida");
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        cboServicio.setSelectedIndex(0);
        cboDia.setSelectedIndex(0);
        cboMes.setSelectedIndex(0);
        cboAno.setSelectedIndex(0);
        cboHora.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AgendarCitaGUI().setVisible(true);
            }
        });
    }
}