import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SeguridadManager {
    public static final String ROL_ADMINISTRADOR = "administrador";
    public static final String ROL_EMPLEADA = "empleada";
    public static final String ROL_EMPLEADO = "empleado";

    private static String usuarioActual = null;
    private static String rolActual = null;
    private static String nombreCompletoActual = null;
    private static Integer idEmpleadaActual = null;

    public static void setUsuarioActual(String usuario, String rol) {
        setUsuarioActual(usuario, rol, usuario);
    }

    public static void setUsuarioActual(String usuario, String rol, String nombreCompleto) {
        usuarioActual = usuario;
        rolActual = (rol != null) ? rol.toLowerCase().trim() : "empleada";
        nombreCompletoActual = nombreCompleto != null ? nombreCompleto : usuario;
        cargarIdEmpleadaSiAplica();
    }

    private static void cargarIdEmpleadaSiAplica() {
        idEmpleadaActual = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT id, nombre FROM empleadas WHERE username = ? OR nombre LIKE ? LIMIT 1")) {
            ps.setString(1, usuarioActual);
            ps.setString(2, "%" + nombreCompletoActual + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idEmpleadaActual = rs.getInt("id");
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public static String getUsuarioActual() {
        return usuarioActual;
    }

    public static String getRolActual() {
        return rolActual;
    }

    public static String getNombreCompletoActual() {
        return nombreCompletoActual != null ? nombreCompletoActual : "Usuario";
    }

    public static Integer getIdEmpleadaActual() {
        return idEmpleadaActual;
    }

    public static boolean esAdministrador() {
        return rolActual != null && (rolActual.equals("administrador") || rolActual.equals("admin"));
    }

    public static boolean esEmpleado() {
        return rolActual != null && (rolActual.equals("empleada") || rolActual.equals("empleado"));
    }

    public static boolean tienePermiso(String accion) {
        if (esAdministrador()) return true;

        switch (accion.toLowerCase()) {
            case "ver_mi_agenda":
            case "registrar_mi_servicio":
            case "ver_mi_saldo":
            case "agendar_citas":
                return true;
            case "ver_nomina":
            case "liquidar_comisiones":
            case "gestionar_empleadas":
            case "gestionar_servicios":
                return false;
            default:
                return true;
        }
    }

    public static double getComisionEmpleada(String nombreEmpleada) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT porcentaje_comision FROM empleadas WHERE nombre = ? LIMIT 1")) {
            ps.setString(1, nombreEmpleada);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("porcentaje_comision") / 100.0;
                }
            }
        } catch (Exception e) {
            // fallback
        }
        if ("Ashley".equalsIgnoreCase(nombreEmpleada)) return 0.20;
        if ("Monika".equalsIgnoreCase(nombreEmpleada)) return 0.30;
        if ("Veronika".equalsIgnoreCase(nombreEmpleada)) return 0.50;
        return 0.20;
    }

    public static String getEspecialidadEmpleada(String nombreEmpleada) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT especialidad FROM empleadas WHERE nombre = ? LIMIT 1")) {
            ps.setString(1, nombreEmpleada);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("especialidad");
                }
            }
        } catch (Exception e) {
            // fallback
        }
        if ("Ashley".equalsIgnoreCase(nombreEmpleada)) return "Uñas y Manicure";
        if ("Monika".equalsIgnoreCase(nombreEmpleada)) return "Cejas";
        if ("Veronika".equalsIgnoreCase(nombreEmpleada)) return "Depilación";
        return "General";
    }

    public static double getComisionActual() {
        return getComisionEmpleada(getNombreCompletoActual());
    }

    public static String getEspecialidadActual() {
        return getEspecialidadEmpleada(getNombreCompletoActual());
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
        nombreCompletoActual = null;
        idEmpleadaActual = null;
    }
}
