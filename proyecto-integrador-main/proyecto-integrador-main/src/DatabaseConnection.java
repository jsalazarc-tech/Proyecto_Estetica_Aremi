// DatabaseConnection.java
import java.sql.*;

/**
 * Clase de utilidad para gestionar conexiones a la base de datos.
 * AHORA OPTIMIZADO: Utiliza ConnectionPool para mejorar el rendimiento.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String USER = "root";
    private static final String PASSWORD = "admin1023";

    /**
     * Obtiene una conexión del pool (MÉTODO RECOMENDADO)
     * IMPORTANTE: Debe llamarse releaseConnection() cuando termine de usarla
     */
    public static Connection getConnection() throws SQLException {
        return ConnectionPool.getPooledConnection();
    }

    /**
     * Libera una conexión para que vuelva al pool
     */
    public static void releaseConnection(Connection conn) {
        ConnectionPool.releasePooledConnection(conn);
    }

    public static void guardarCita(String nombre, String telefono, String servicio,
                                   Date fecha, String hora) throws SQLException {
        String sql = "INSERT INTO citas (nombre, telefono, servicio, fecha, hora) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, servicio);
            pstmt.setDate(4, fecha);
            pstmt.setString(5, hora);

            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { }
            if (conn != null) releaseConnection(conn);
        }
    }

    // Método para probar la conexión
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) releaseConnection(conn);
        }
    }
}