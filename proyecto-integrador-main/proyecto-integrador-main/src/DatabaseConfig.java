import java.io.*;
import java.sql.*;
import java.util.Properties;
import javax.swing.*;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "db_config.properties";

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

    public static synchronized void saveConfig(String newHost, String newPort, String newDbName, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        dbName = newDbName;
        user = newUser;
        password = newPass;

        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", port);
        props.setProperty("db.name", dbName);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Configuracion de conexion MySQL - Salon Aremi");
        } catch (Exception e) {
            System.err.println("Error guardando " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }

    public static String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true";
    }

    public static String getServerUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    }

    public static boolean testConnection(String h, String p, String db, String u, String pass) {
        String testUrl = "jdbc:mysql://" + h + ":" + p + "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try (Connection c = DriverManager.getConnection(testUrl, u, pass)) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Inicializa la base de datos completa con tablas y datos semilla
     */
    public static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("USE " + dbName);

            // 1. Usuarios
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  username VARCHAR(50) NOT NULL UNIQUE," +
                "  password VARCHAR(64) NOT NULL," +
                "  nombre_completo VARCHAR(100) NOT NULL," +
                "  rol VARCHAR(20) NOT NULL DEFAULT 'empleada'," +
                "  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
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
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
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
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
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
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
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
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
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
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
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
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            // 8. Facturas
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS facturas (" +
                "  id_factura VARCHAR(20) PRIMARY KEY," +
                "  usuario VARCHAR(100)," +
                "  telefono VARCHAR(20)," +
                "  valor_venta DECIMAL(10,2)," +
                "  nombre_vendedor VARCHAR(100)," +
                "  fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            seedInitialData(conn);

        } catch (SQLException e) {
            System.err.println("Error inicializando esquema de BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedInitialData(Connection conn) {
        try {
            // Seed Usuarios (SHA-256)
            // 'admin123' -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
            // 'fernanda123' -> 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
            // 'ashley123' -> 8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
            // 'monika123' -> 03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4
            // 'veronika123' -> 5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5
            String insertUser = "INSERT IGNORE INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                String[][] users = {
                    {"admin", "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", "Administrador Principal", "administrador"},
                    {"fernanda", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", "Fernanda Administradora", "administrador"},
                    {"ashley", "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", "Ashley", "empleada"},
                    {"monika", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", "Monika", "empleada"},
                    {"veronika", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", "Veronika", "empleada"}
                };
                for (String[] u : users) {
                    ps.setString(1, u[0]);
                    ps.setString(2, u[1]);
                    ps.setString(3, u[2]);
                    ps.setString(4, u[3]);
                    ps.executeUpdate();
                }
            }

            // Seed Empleadas según el PDF (Ashley 20% uñas, Monika 30% cejas, Veronika 50% depilación)
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

            // Seed Servicios iniciales
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM servicios")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertServ = "INSERT INTO servicios (nombre, descripcion, categoria, precio, duracion_minutos) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertServ)) {
                        Object[][] servs = {
                            {"Manicure Tradicional", "Limpieza, limado, cutícula y esmaltado", "Uñas y Manicure", 25000.0, 45},
                            {"Uñas Acrílicas Esculpidas", "Extensión y diseño artístico de uñas", "Uñas y Manicure", 65000.0, 90},
                            {"Pedicure Spa", "Exfoliación, hidratación profunda y esmaltado", "Uñas y Manicure", 35000.0, 60},
                            {"Diseño de Cejas con Henna", "Diseño personalizado, depilación y sombreado con henna", "Cejas", 30000.0, 45},
                            {"Micropigmentación de Cejas", "Técnica pelo a pelo semipermanente", "Cejas", 120000.0, 120},
                            {"Depilación Facial Completa", "Cera suave para rostro, cejas y bozo", "Depilación", 25000.0, 30},
                            {"Depilación de Piernas Completas", "Depilación con cera miel", "Depilación", 45000.0, 45},
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

            // Seed Clientes iniciales
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

        } catch (SQLException e) {
            System.err.println("Error sembrando datos iniciales: " + e.getMessage());
        }
    }
}
