package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Gestor Centralizado de Conexión a Base de Datos (Patrón Singleton).
 * Cumple con el principio de responsabilidad única: centraliza el ciclo de vida de la conexión JDBC
 * y elimina contraseñas sensibles quemadas en el código fuente mediante carga por archivo externo de propiedades.
 */
public class ConexionBD {
    private static ConexionBD instancia;
    private static final String ARCHIVO_CONFIG = "db.properties";
    private static boolean esquemaInicializado = false;

    // Parámetros cargados dinámicamente (por defecto H2 embebida para portabilidad)
    private boolean usarLocal = true;
    private String host = "127.0.0.1";
    private String puerto = "3306";
    private String baseDatos = "salon_aremi";
    private String usuario = "sa";
    private String password = "";

    private ConexionBD() {
        cargarConfiguracion();
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Carga configuración desde archivo db.properties si existe.
     * Si no existe, no quema credenciales root en código; usa H2 embebido por defecto.
     */
    private void cargarConfiguracion() {
        File f = new File(ARCHIVO_CONFIG);
        if (f.exists()) {
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
                p.load(fis);
                usarLocal = Boolean.parseBoolean(p.getProperty("db.usar_local", "true"));
                host = p.getProperty("db.host", "127.0.0.1");
                puerto = p.getProperty("db.puerto", "3306");
                baseDatos = p.getProperty("db.nombre", "salon_aremi");
                usuario = p.getProperty("db.usuario", usarLocal ? "sa" : "root");
                password = p.getProperty("db.password", "");
            } catch (Exception e) {
                System.err.println("Aviso: No se pudo leer db.properties. Usando modo embebido local.");
            }
        }
    }

    public void guardarConfiguracion(boolean local, String h, String port, String db, String u, String pass) {
        this.usarLocal = local;
        this.host = h;
        this.puerto = port;
        this.baseDatos = db;
        this.usuario = u;
        this.password = pass;

        Properties p = new Properties();
        p.setProperty("db.usar_local", String.valueOf(local));
        p.setProperty("db.host", h);
        p.setProperty("db.puerto", port);
        p.setProperty("db.nombre", db);
        p.setProperty("db.usuario", u);
        p.setProperty("db.password", pass);

        try (FileOutputStream fos = new FileOutputStream(ARCHIVO_CONFIG)) {
            p.store(fos, "Configuracion de Base de Datos - Salon Aremi");
        } catch (Exception e) {
            System.err.println("Error al guardar db.properties: " + e.getMessage());
        }
    }

    public Connection getConexion() throws SQLException {
        Connection conn = null;
        try {
            if (usarLocal) {
                try {
                    Class.forName("org.h2.Driver");
                } catch (ClassNotFoundException ignored) {}
                String url = "jdbc:h2:./salon_aremi_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
                conn = DriverManager.getConnection(url, "sa", "");
            } else {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException ignored) {}
                String url = "jdbc:mysql://" + host + ":" + puerto + "/" + baseDatos +
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true";
                conn = DriverManager.getConnection(url, usuario, password);
            }

            if (!esquemaInicializado && conn != null) {
                inicializarEsquema(conn);
                esquemaInicializado = true;
            }
            return conn;

        } catch (SQLException e) {
            // Si falla conexión MySQL remota/local, fallback seguro a local H2 sin bloquear la app
            if (!usarLocal) {
                System.err.println("Aviso: Fallo al conectar a MySQL (" + e.getMessage() + "). Conectando a Base de Datos Local...");
                usarLocal = true;
                return getConexion();
            }
            throw e;
        }
    }

    public void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public boolean testConexion() {
        try (Connection c = getConexion()) {
            return c != null && !c.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUsarLocal() { return usarLocal; }
    public String getHost() { return host; }
    public String getPuerto() { return puerto; }
    public String getBaseDatos() { return baseDatos; }
    public String getUsuario() { return usuario; }

    /**
     * Inicializa las tablas del sistema si no existen y precarga los datos base.
     */
    private synchronized void inicializarEsquema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // 1. Usuarios
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  username VARCHAR(50) NOT NULL UNIQUE," +
                "  password VARCHAR(255) NOT NULL," +
                "  nombre_completo VARCHAR(100) NOT NULL," +
                "  rol VARCHAR(20) NOT NULL" +
                ")"
            );

            // 2. Empleadas
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS empleadas (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nombre VARCHAR(100) NOT NULL," +
                "  telefono VARCHAR(20) NOT NULL," +
                "  direccion VARCHAR(200) DEFAULT ''," +
                "  email VARCHAR(100) DEFAULT ''," +
                "  especialidad VARCHAR(50) NOT NULL," +
                "  porcentaje_comision DECIMAL(5,2) NOT NULL," +
                "  username VARCHAR(50) NULL," +
                "  activo TINYINT(1) DEFAULT 1" +
                ")"
            );
            try { stmt.executeUpdate("ALTER TABLE empleadas ADD COLUMN IF NOT EXISTS direccion VARCHAR(200) DEFAULT ''"); } catch (Exception ignored) {}
            try { stmt.executeUpdate("ALTER TABLE empleadas ADD COLUMN IF NOT EXISTS email VARCHAR(100) DEFAULT ''"); } catch (Exception ignored) {}

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

            precargarDatosIniciales(conn);
        } catch (SQLException e) {
            System.err.println("Error al inicializar tablas: " + e.getMessage());
        }
    }

    private void precargarDatosIniciales(Connection conn) {
        try {
            // Precargar usuarios
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM usuarios")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String sql = "INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        String[][] users = {
                            {"admin", "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", "Administrador General", "administrador"},
                            {"fernanda", "a277c3ba4d39d7a49cc6507e1818e0d6ebc3d8621ccc63daec59829a6ae52ad9", "Fernanda Administradora", "administrador"},
                            {"ashley", "2336e2ec1b97338056c1e64172acf13a6461d977ecbcb23cbec31d25de98e2ad", "Ashley", "empleada"},
                            {"monika", "07e6f0ebf1ecce152c491cb7ac864e5ad8fa9e11d8be9127e18da867e2c42fa7", "Monika", "empleada"},
                            {"veronika", "d6f1c8895277b7e7133bd4aaba2feb61d9b69f2f3a159670cd44b8dbc39a6973", "Veronika", "empleada"}
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

            // Precargar empleadas
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM empleadas")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String sql = "INSERT INTO empleadas (nombre, telefono, direccion, email, especialidad, porcentaje_comision, username, activo) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        Object[][] emps = {
                            {"Ashley", "3101234567", "Calle 10 # 45-20", "ashley@salonaremi.com", "Uñas y Manicure", 20.0, "ashley"},
                            {"Monika", "3119876543", "Av. 6N # 28-15", "monika@salonaremi.com", "Cejas", 30.0, "monika"},
                            {"Veronika", "3125556677", "Carrera 56 # 14-80", "veronika@salonaremi.com", "Depilación", 50.0, "veronika"}
                        };
                        for (Object[] emp : emps) {
                            ps.setString(1, (String) emp[0]);
                            ps.setString(2, (String) emp[1]);
                            ps.setString(3, (String) emp[2]);
                            ps.setString(4, (String) emp[3]);
                            ps.setString(5, (String) emp[4]);
                            ps.setDouble(6, (Double) emp[5]);
                            ps.setString(7, (String) emp[6]);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Precargar servicios catálogo
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM servicios")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String sql = "INSERT INTO servicios (nombre, descripcion, categoria, precio, duracion_minutos, activo) VALUES (?, ?, ?, ?, ?, 1)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        Object[][] servs = {
                            {"Manicure Tradicional", "Limpieza, limado, cutícula y esmaltado", "Uñas y Manicure", 25000.0, 45},
                            {"Pedicure Spa", "Exfoliación, hidratación profunda y esmaltado", "Uñas y Manicure", 35000.0, 60},
                            {"Uñas Acrílicas Esculpidas", "Extensión y diseño artístico con acrílico", "Uñas y Manicure", 65000.0, 90},
                            {"Diseño de Cejas con Henna", "Diseño personalizado y pigmentación temporal", "Cejas", 30000.0, 45},
                            {"Micropigmentación de Cejas", "Técnica pelo a pelo semipermanente", "Cejas", 120000.0, 120},
                            {"Depilación Facial Completa", "Cera suave para cejas, bozo y mentón", "Depilación", 25000.0, 30},
                            {"Depilación Piernas Completas", "Depilación con cera miel natural", "Depilación", 45000.0, 45},
                            {"Depilación Bikini Completo", "Cera especializada para zona íntima", "Depilación", 40000.0, 40}
                        };
                        for (Object[] srv : servs) {
                            ps.setString(1, (String) srv[0]);
                            ps.setString(2, (String) srv[1]);
                            ps.setString(3, (String) srv[2]);
                            ps.setDouble(4, (Double) srv[3]);
                            ps.setInt(5, (Integer) srv[4]);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Precargar clientes demo
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM clientes")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String sql = "INSERT INTO clientes (nombre, telefono, direccion, email) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        String[][] cls = {
                            {"Valentina Gómez", "3154829103", "Calle 15 # 45-20", "valentina.gomez@gmail.com"},
                            {"Camila Restrepo", "3187261944", "Av. Roosevelt # 34-12", "camila.restrepo@hotmail.com"},
                            {"Sofía Caicedo", "3006129845", "Carrera 80 # 12-50", "sofia.caicedo@outlook.com"}
                        };
                        for (String[] c : cls) {
                            ps.setString(1, c[0]);
                            ps.setString(2, c[1]);
                            ps.setString(3, c[2]);
                            ps.setString(4, c[3]);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Sincronizar cuentas de usuario para empleadas con username
            try (Statement sEmp = conn.createStatement();
                 ResultSet rsEmp = sEmp.executeQuery("SELECT username, nombre FROM empleadas WHERE username IS NOT NULL AND TRIM(username) != ''")) {
                while (rsEmp.next()) {
                    String uName = rsEmp.getString(1).trim().toLowerCase();
                    String uNom = rsEmp.getString(2);
                    try (PreparedStatement psCheck = conn.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE LOWER(username) = ?")) {
                        psCheck.setString(1, uName);
                        try (ResultSet rsC = psCheck.executeQuery()) {
                            if (rsC.next() && rsC.getInt(1) == 0) {
                                try (PreparedStatement psIns = conn.prepareStatement("INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, 'empleada')")) {
                                    psIns.setString(1, uName);
                                    psIns.setString(2, util.SeguridadManager.hashPassword(uName + "123"));
                                    psIns.setString(3, uNom);
                                    psIns.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Aviso al precargar datos: " + e.getMessage());
        }
    }
}
