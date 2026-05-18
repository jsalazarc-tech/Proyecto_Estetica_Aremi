import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pool de conexiones simple pero eficiente para optimizar el acceso a la base de datos.
 * Evita crear y destruir conexiones constantemente, mejorando significativamente el rendimiento.
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class ConnectionPool {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin1023";
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;

    private static ConnectionPool instance;
    private List<Connection> availableConnections;
    private List<Connection> usedConnections;

    /**
     * Constructor privado para patrón Singleton
     */
    private ConnectionPool() {
        availableConnections = new ArrayList<>(INITIAL_POOL_SIZE);
        usedConnections = new ArrayList<>();

        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Crear conexiones iniciales
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                availableConnections.add(createConnection());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al inicializar el pool de conexiones: " + e.getMessage());
        }
    }

    /**
     * Obtiene la instancia única del pool (Singleton)
     */
    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    /**
     * Crea una nueva conexión a la base de datos
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Obtiene una conexión del pool
     */
    public synchronized Connection getConnection() throws SQLException {
        // Si no hay conexiones disponibles y no hemos alcanzado el máximo
        if (availableConnections.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                availableConnections.add(createConnection());
            } else {
                throw new SQLException("Pool de conexiones agotado. Máximo alcanzado: " + MAX_POOL_SIZE);
            }
        }

        Connection connection = availableConnections.remove(availableConnections.size() - 1);

        // Verificar si la conexión sigue siendo válida
        if (connection.isClosed() || !connection.isValid(2)) {
            connection = createConnection();
        }

        usedConnections.add(connection);
        return connection;
    }

    /**
     * Devuelve una conexión al pool para ser reutilizada
     */
    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            usedConnections.remove(connection);
            availableConnections.add(connection);
        }
    }

    /**
     * Cierra todas las conexiones del pool
     */
    public synchronized void shutdown() {
        // Cerrar todas las conexiones disponibles
        for (Connection conn : availableConnections) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }

        // Cerrar todas las conexiones en uso
        for (Connection conn : usedConnections) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }

        availableConnections.clear();
        usedConnections.clear();
    }

    /**
     * Obtiene el número de conexiones disponibles
     */
    public synchronized int getAvailableConnectionsCount() {
        return availableConnections.size();
    }

    /**
     * Obtiene el número de conexiones en uso
     */
    public synchronized int getUsedConnectionsCount() {
        return usedConnections.size();
    }

    /**
     * Método de conveniencia para obtener una conexión directamente
     */
    public static Connection getPooledConnection() throws SQLException {
        return getInstance().getConnection();
    }

    /**
     * Método de conveniencia para liberar una conexión directamente
     */
    public static void releasePooledConnection(Connection connection) {
        getInstance().releaseConnection(connection);
    }
}