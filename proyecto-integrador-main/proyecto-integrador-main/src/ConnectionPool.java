import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private static final int INITIAL_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 10;

    private static ConnectionPool instance;
    private List<Connection> availableConnections;
    private List<Connection> usedConnections;

    private ConnectionPool() {
        availableConnections = new ArrayList<>(INITIAL_POOL_SIZE);
        usedConnections = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                availableConnections.add(createConnection());
            }
        } catch (Exception e) {
            System.err.println("Aviso al inicializar pool: " + e.getMessage());
        }
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUser(),
            DatabaseConfig.getPassword()
        );
    }

    public synchronized Connection getConnection() throws SQLException {
        if (availableConnections.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                availableConnections.add(createConnection());
            } else {
                return createConnection();
            }
        }

        Connection conn = availableConnections.remove(availableConnections.size() - 1);
        try {
            if (conn.isClosed()) {
                conn = createConnection();
            }
        } catch (SQLException e) {
            conn = createConnection();
        }
        usedConnections.add(conn);
        return conn;
    }

    public synchronized void releaseConnection(Connection conn) {
        if (conn != null) {
            usedConnections.remove(conn);
            try {
                if (!conn.isClosed() && availableConnections.size() < MAX_POOL_SIZE) {
                    availableConnections.add(conn);
                } else {
                    conn.close();
                }
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    public static Connection getPooledConnection() throws SQLException {
        return getInstance().getConnection();
    }

    public static void releasePooledConnection(Connection conn) {
        getInstance().releaseConnection(conn);
    }
}
