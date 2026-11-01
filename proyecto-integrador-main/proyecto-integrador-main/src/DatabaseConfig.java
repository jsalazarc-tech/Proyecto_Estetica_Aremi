import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "db_config.properties";

    private static boolean useLocalDb = true; // Por defecto modo local integrado sin contraseña para máxima compatibilidad
    private static String host = "127.0.0.1";
    private static String port = "3306";
    private static String dbName = "salon_aremi";
    private static String user = "root";
    private static String password = "admin1023";

    static {
        loadConfig();
    }

    public static synchronized void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                useLocalDb = Boolean.parseBoolean(props.getProperty("db.use_local", "true"));
                host = props.getProperty("db.host", host);
                port = props.getProperty("db.port", port);
                dbName = props.getProperty("db.name", dbName);
                user = props.getProperty("db.user", user);
                password = props.getProperty("db.password", password);
            } catch (Exception e) {
                System.err.println("Error leyendo " + CONFIG_FILE + ": " + e.getMessage());
            }
        }
    }

    public static synchronized void saveConfig(boolean local, String newHost, String newPort, String newDbName, String newUser, String newPass) {
        useLocalDb = local;
        host = newHost;
        port = newPort;
        dbName = newDbName;
        user = newUser;
        password = newPass;

        Properties props = new Properties();
        props.setProperty("db.use_local", String.valueOf(useLocalDb));
        props.setProperty("db.host", host);
        props.setProperty("db.port", port);
        props.setProperty("db.name", dbName);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Configuracion de base de datos - Salon Aremi");
        } catch (Exception e) {
            System.err.println("Error guardando " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    public static boolean isUseLocalDb() { return useLocalDb; }
    public static void setUseLocalDb(boolean local) { useLocalDb = local; }

    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }

    public static String getUrl() {
        if (useLocalDb) {
            return "jdbc:h2:./salon_aremi_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
        }
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true";
    }

    public static String getEffectiveUser() {
        return useLocalDb ? "sa" : user;
    }

    public static String getEffectivePassword() {
        return useLocalDb ? "" : password;
    }

    public static boolean testConnection(boolean local, String h, String p, String db, String u, String pass) {
        if (local) {
            try {
                Class.forName("org.h2.Driver");
                try (Connection c = DriverManager.getConnection("jdbc:h2:./salon_aremi_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "")) {
                    return c != null && !c.isClosed();
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            String testUrl = "jdbc:mysql://" + h + ":" + p + "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            try (Connection c = DriverManager.getConnection(testUrl, u, pass)) {
                return c != null && !c.isClosed();
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            if (!useLocalDb) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                stmt.executeUpdate("USE " + dbName);
            }

            // 1. Usuarios
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  username VARCHAR(50) NOT NULL UNIQUE," +
                "  password VARCHAR(64) NOT NULL," +
                "  nombre_completo VARCHAR(100) NOT NULL," +
                "  rol VARCHAR(20) NOT NULL DEFAULT 'empleada'," +
                "  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // 2. Empleadas
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS empleadas (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nombre VARCHAR(100) NOT NULL," +
                "  telefono VARCHAR(20)," +
                "  especialidad VARCHAR(100) NOT NULL," +
                "  porcentaje_comision DECIMAL(5,2) NOT NULL," +
                "  username VARCHAR(50) NULL," +
                "  activo TINYINT(1) DEFAULT 1" +
                ")"
            );

            // 3. Clientes
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS clientes (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nombre VARCHAR(100) NOT NULL," +
                "  telefono VARCHAR(20) NOT NULL," +
                "  direccion VARCHAR(200)," +
                "  email VARCHAR(100)," +
                "  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // 4. Servicios
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS servicios (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nombre VARCHAR(100) NOT NULL," +
                "  descripcion VARCHAR(255)," +
                "  categoria VARCHAR(50) NOT NULL," +
                "  precio DECIMAL(10,2) NOT NULL," +
                "  duracion_minutos INT DEFAULT 60," +
                "  activo TINYINT(1) DEFAULT 1" +
                ")"
            );

            // 5. Citas
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS citas (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  id_cliente INT NULL," +
                "  nombre_cliente VARCHAR(100) NOT NULL," +
                "  telefono_cliente VARCHAR(20) NOT NULL," +
                "  id_servicio INT NULL," +
                "  nombre_servicio VARCHAR(100) NOT NULL," +
                "  id_empleada INT NULL," +
                "  nombre_empleada VARCHAR(100) NOT NULL," +
                "  fecha DATE NOT NULL," +
                "  hora VARCHAR(10) NOT NULL," +
                "  estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente'," +
                "  notas VARCHAR(255)," +
                "  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // 6. Servicios Realizados
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS servicios_realizados (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  id_cita INT NULL," +
                "  id_empleada INT NOT NULL," +
                "  nombre_empleada VARCHAR(100) NOT NULL," +
                "  id_servicio INT NOT NULL," +
                "  nombre_servicio VARCHAR(100) NOT NULL," +
                "  id_cliente INT NULL," +
                "  nombre_cliente VARCHAR(100) NOT NULL," +
                "  fecha DATE NOT NULL," +
                "  valor_cobrado DECIMAL(10,2) NOT NULL," +
                "  porcentaje_comision DECIMAL(5,2) NOT NULL," +
                "  comision_calculada DECIMAL(10,2) NOT NULL," +
                "  estado_liquidacion VARCHAR(20) NOT NULL DEFAULT 'Pendiente'," +
                "  id_liquidacion INT NULL," +
                "  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // 7. Liquidaciones
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS liquidaciones (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  id_empleada INT NOT NULL," +
                "  nombre_empleada VARCHAR(100) NOT NULL," +
                "  fecha_inicio DATE NOT NULL," +
                "  fecha_fin DATE NOT NULL," +
                "  total_servicios INT NOT NULL," +
                "  total_ventas DECIMAL(10,2) NOT NULL," +
                "  porcentaje_aplicado DECIMAL(5,2) NOT NULL," +
                "  total_comision DECIMAL(10,2) NOT NULL," +
                "  total_salon DECIMAL(10,2) NOT NULL," +
                "  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  notas VARCHAR(255)" +
                ")"
            );

            // 8. Facturas
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS facturas (" +
                "  id_factura VARCHAR(20) PRIMARY KEY," +
                "  usuario VARCHAR(100)," +
                "  telefono VARCHAR(20)," +
                "  valor_venta DECIMAL(10,2)," +
                "  nombre_vendedor VARCHAR(100)," +
                "  fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            seedInitialData(conn);

        } catch (SQLException e) {
            System.err.println("Error inicializando esquema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedInitialData(Connection conn) {
        try {
            // Verificar si existen usuarios
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM usuarios")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertUser = "INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                        String[][] users = {
                            {"admin", "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", "Administrador General", "administrador"},
                            {"fernanda", "a277c3ba4d39d7a49cc6507e1818e0d6ebc3d8621ccc63daec59829a6ae52ad9", "Fernanda Administradora", "administrador"},
                            {"ashley", "2336e2ec1b97338056c1e64172acf13a6461d977ecbcb23cbec31d25de98e2ad", "Ashley", "empleada"},
                            {"monika", "07e6f0ebf1ecce152c491cb7ac864e5ad8fa9e11d8be9127e18da867e2c42fa7", "Monika", "empleada"},
                            {"veronika", "d6f1c8895277b7e7133bd4aaba2feb61d9b69f2f3a159670cd44b8dbc39a6973", "Veronika", "empleada"},
                            {"empleada1", "e03d3ec8d5035f8721f5dc64546e59ed790dbcb3b7b598fe57057ccd7b683b00", "Empleada Demo", "empleada"}
                        };
                        for (String[] u : users) {
                            ps.setString(1, u[0]);
                            ps.setString(2, u[1]);
                            ps.setString(3, u[2]);
                            ps.setString(4, u[3]);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Empleadas
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM empleadas")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertEmp = "INSERT INTO empleadas (nombre, telefono, especialidad, porcentaje_comision, username, activo) VALUES (?, ?, ?, ?, ?, 1)";
                    try (PreparedStatement ps = conn.prepareStatement(insertEmp)) {
                        ps.setString(1, "Ashley");
                        ps.setString(2, "3101234567");
                        ps.setString(3, "Uñas y Manicure");
                        ps.setDouble(4, 20.0);
                        ps.setString(5, "ashley");
                        ps.executeUpdate();

                        ps.setString(1, "Monika");
                        ps.setString(2, "3119876543");
                        ps.setString(3, "Cejas");
                        ps.setDouble(4, 30.0);
                        ps.setString(5, "monika");
                        ps.executeUpdate();

                        ps.setString(1, "Veronika");
                        ps.setString(2, "3125556677");
                        ps.setString(3, "Depilación");
                        ps.setDouble(4, 50.0);
                        ps.setString(5, "veronika");
                        ps.executeUpdate();
                    }
                }
            }

            // Servicios
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM servicios")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertServ = "INSERT INTO servicios (nombre, descripcion, categoria, precio, duracion_minutos, activo) VALUES (?, ?, ?, ?, ?, 1)";
                    try (PreparedStatement ps = conn.prepareStatement(insertServ)) {
                        Object[][] servs = {
                            {"Manicure Tradicional", "Limpieza, limado, cutícula y esmaltado tradicional", "Uñas y Manicure", 25000.0, 45},
                            {"Uñas Acrílicas Esculpidas", "Extensión y diseño artístico con acrílico", "Uñas y Manicure", 65000.0, 90},
                            {"Pedicure Spa", "Exfoliación, hidratación profunda y esmaltado", "Uñas y Manicure", 35000.0, 60},
                            {"Diseño de Cejas con Henna", "Diseño personalizado y pigmentación con henna", "Cejas", 30000.0, 45},
                            {"Micropigmentación de Cejas", "Técnica pelo a pelo semipermanente", "Cejas", 120000.0, 120},
                            {"Depilación Facial Completa", "Cera suave para cejas, bozo y mentón", "Depilación", 25000.0, 30},
                            {"Depilación Piernas Completas", "Depilación con cera miel nutritiva", "Depilación", 45000.0, 45},
                            {"Depilación Bikini Completo", "Cera especializada para zona íntima", "Depilación", 40000.0, 40}
                        };
                        for (Object[] row : servs) {
                            ps.setString(1, (String) row[0]);
                            ps.setString(2, (String) row[1]);
                            ps.setString(3, (String) row[2]);
                            ps.setDouble(4, (Double) row[3]);
                            ps.setInt(5, (Integer) row[4]);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Clientes
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM clientes")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertCli = "INSERT INTO clientes (nombre, telefono, direccion, email) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertCli)) {
                        ps.setString(1, "Valentina Gómez");
                        ps.setString(2, "3154829103");
                        ps.setString(3, "Calle 5 # 34-12");
                        ps.setString(4, "valentina.gomez@gmail.com");
                        ps.executeUpdate();

                        ps.setString(1, "Camila Torres");
                        ps.setString(2, "3187214950");
                        ps.setString(3, "Av. Roosevelt # 28-40");
                        ps.setString(4, "camila.torres@hotmail.com");
                        ps.executeUpdate();

                        ps.setString(1, "Mariana Restrepo");
                        ps.setString(2, "3119043218");
                        ps.setString(3, "Carrera 66 # 10-05");
                        ps.setString(4, "mariana.restrepo@yahoo.com");
                        ps.executeUpdate();
                    }
                }
            }

            // Citas
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM citas")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insCita = "INSERT INTO citas (id_cliente, nombre_cliente, telefono_cliente, id_servicio, nombre_servicio, id_empleada, nombre_empleada, fecha, hora, estado, notas) " +
                                     "VALUES (1, 'Valentina Gómez', '3154829103', 1, 'Manicure Tradicional', 1, 'Ashley', CURRENT_DATE, '10:00', 'Pendiente', 'Cliente regular')";
                    s.executeUpdate(insCita);
                }
            }

        } catch (SQLException e) {
            System.err.println("Aviso sembrando datos: " + e.getMessage());
        }
    }
}
