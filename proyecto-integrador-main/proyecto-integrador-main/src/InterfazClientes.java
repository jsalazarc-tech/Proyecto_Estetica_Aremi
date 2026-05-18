import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InterfazClientes extends JFrame {
    private Connection connection;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listaClientes;
    private JTextField nombreField, direccionField, telefonoField;
    private int clienteSeleccionadoId = -1;
    private java.util.ArrayList<Integer> clienteIds = new java.util.ArrayList<>();

    public InterfazClientes() {
        setTitle("Gestión de Clientes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Conectar a la base de datos
        conectarBaseDeDatos();

        // Panel para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(240, 240, 240));

        inputPanel.add(new JLabel("Nombre:"));
        nombreField = new JTextField();
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Dirección:"));
        direccionField = new JTextField();
        inputPanel.add(direccionField);
        inputPanel.add(new JLabel("Teléfono:"));
        telefonoField = new JTextField();
        inputPanel.add(telefonoField);

        // Botones para agregar, editar y eliminar cliente
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton agregarButton = new JButton("Agregar Cliente");
        agregarButton.setBackground(new Color(173, 216, 230));
        agregarButton.setForeground(Color.WHITE);
        agregarButton.setFocusPainted(false);
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarCliente();
            }
        });

        JButton editarButton = new JButton("Editar Cliente");
        editarButton.setBackground(new Color(144, 238, 144));
        editarButton.setForeground(Color.WHITE);
        editarButton.setFocusPainted(false);
        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarCliente();
            }
        });

        JButton eliminarButton = new JButton("Eliminar Cliente");
        eliminarButton.setBackground(new Color(255, 105, 97));
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.setFocusPainted(false);
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarCliente();
            }
        });

        buttonPanel.add(agregarButton);
        buttonPanel.add(editarButton);
        buttonPanel.add(eliminarButton);

        // Lista para mostrar clientes
        listaClientes = new JList<>(listModel);
        listaClientes.setBackground(new Color(250, 250, 250));
        listaClientes.setForeground(new Color(50, 50, 50));
        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = listaClientes.getSelectedIndex();
                if (selectedIndex != -1) {
                    cargarClienteSeleccionado(selectedIndex);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(listaClientes);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Añadir componentes al frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Cargar clientes desde la base de datos
        cargarClientes();
    }

    private void conectarBaseDeDatos() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/gestion_clientes";
            String user = "root";
            String password = "admin1023";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.");
        }
    }

    private void agregarCliente() {
        String nombre = nombreField.getText();
        String direccion = direccionField.getText();
        String telefono = telefonoField.getText();

        if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty()) {
            try {
                String query = "INSERT INTO clientes (nombre, direccion, telefono) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, direccion);
                preparedStatement.setString(3, telefono);
                preparedStatement.executeUpdate();

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Cliente cliente = new Cliente(nombre, direccion, telefono);
                    listModel.addElement(cliente.toString());
                    clienteIds.add(id);
                    limpiarCampos();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al agregar el cliente.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
        }
    }

    private void editarCliente() {
        if (clienteSeleccionadoId != -1) {
            String nombre = nombreField.getText();
            String direccion = direccionField.getText();
            String telefono = telefonoField.getText();

            if (!nombre.isEmpty() && !direccion.isEmpty() && !telefono.isEmpty()) {
                try {
                    String query = "UPDATE clientes SET nombre = ?, direccion = ?, telefono = ? WHERE id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, nombre);
                    preparedStatement.setString(2, direccion);
                    preparedStatement.setString(3, telefono);
                    preparedStatement.setInt(4, clienteSeleccionadoId);
                    preparedStatement.executeUpdate();

                    Cliente cliente = new Cliente(nombre, direccion, telefono);
                    listModel.set(listaClientes.getSelectedIndex(), cliente.toString());
                    limpiarCampos();
                    clienteSeleccionadoId = -1;
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al editar el cliente.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para editar.");
        }
    }

    private void eliminarCliente() {
        if (clienteSeleccionadoId != -1) {
            try {
                String query = "DELETE FROM clientes WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, clienteSeleccionadoId);
                preparedStatement.executeUpdate();

                int selectedIndex = listaClientes.getSelectedIndex();
                listModel.remove(selectedIndex);
                clienteIds.remove(selectedIndex);
                limpiarCampos();
                clienteSeleccionadoId = -1;
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.");
        }
    }

    private void cargarClientes() {
        listModel.clear();
        clienteIds.clear();
        try {
            String query = "SELECT * FROM clientes";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String direccion = resultSet.getString("direccion");
                String telefono = resultSet.getString("telefono");
                Cliente cliente = new Cliente(nombre, direccion, telefono);
                listModel.addElement(cliente.toString());
                clienteIds.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los clientes.");
        }
    }

    private void cargarClienteSeleccionado(int index) {
        if (index >= 0 && index < clienteIds.size()) {
            int realId = clienteIds.get(index);
            try {
                String query = "SELECT * FROM clientes WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, realId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    nombreField.setText(resultSet.getString("nombre"));
                    direccionField.setText(resultSet.getString("direccion"));
                    telefonoField.setText(resultSet.getString("telefono"));
                    clienteSeleccionadoId = resultSet.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar el cliente seleccionado.");
            }
        }
    }

    private void limpiarCampos() {
        nombreField.setText("");
        direccionField.setText("");
        telefonoField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InterfazClientes().setVisible(true);
            }
        });
    }
}
