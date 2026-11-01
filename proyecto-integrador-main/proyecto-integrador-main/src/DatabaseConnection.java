import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static boolean initialized = false;

    public static synchronized Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
            );

            if (!initialized) {
                DatabaseConfig.initializeDatabase(conn);
                initialized = true;
            }
            return conn;
        } catch (SQLException e) {
            // Si la conexión falla (por ejemplo contraseña incorrecta), intentar conectar al servidor y/o mostrar diálogo
            System.err.println("Aviso: Error de conexión inicial: " + e.getMessage());
            return handleConnectionFailure(e);
        }
    }

    private static synchronized Connection handleConnectionFailure(SQLException originalEx) throws SQLException {
        // Mostrar diálogo de configuración para que el usuario ingrese sus credenciales correctas
        ConfiguracionDBDialog dialog = new ConfiguracionDBDialog(null);
        dialog.setVisible(true);

        if (dialog.isConfiguradoConExito()) {
            Connection conn = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
            );
            DatabaseConfig.initializeDatabase(conn);
            initialized = true;
            return conn;
        }

        throw originalEx;
    }

    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void guardarCita(String nombre, String telefono, String servicio,
                                   Date fecha, String hora) throws SQLException {
        guardarCitaCompleta(null, nombre, telefono, null, servicio, null, "Sin asignar", fecha, hora, "Pendiente", "");
    }

    public static void guardarCitaCompleta(Integer idCliente, String nombre, String telefono,
                                           Integer idServicio, String servicio,
                                           Integer idEmpleada, String empleada,
                                           Date fecha, String hora, String estado, String notas) throws SQLException {
        String sql = "INSERT INTO citas (id_cliente, nombre_cliente, telefono_cliente, id_servicio, nombre_servicio, id_empleada, nombre_empleada, fecha, hora, estado, notas) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (idCliente != null) pstmt.setInt(1, idCliente); else pstmt.setNull(1, Types.INTEGER);
            pstmt.setString(2, nombre);
            pstmt.setString(3, telefono);
            if (idServicio != null) pstmt.setInt(4, idServicio); else pstmt.setNull(4, Types.INTEGER);
            pstmt.setString(5, servicio);
            if (idEmpleada != null) pstmt.setInt(6, idEmpleada); else pstmt.setNull(6, Types.INTEGER);
            pstmt.setString(7, empleada);
            pstmt.setDate(8, fecha);
            pstmt.setString(9, hora);
            pstmt.setString(10, estado != null ? estado : "Pendiente");
            pstmt.setString(11, notas != null ? notas : "");

            pstmt.executeUpdate();
        }
    }
}
