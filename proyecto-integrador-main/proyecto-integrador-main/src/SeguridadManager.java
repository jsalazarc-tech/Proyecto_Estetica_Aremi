import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para gestionar la seguridad y control de acceso del sistema.
 * Maneja roles de usuario y permisos de forma centralizada.
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class SeguridadManager {

    // Roles del sistema
    public static final String ROL_ADMINISTRADOR = "administrador";
    public static final String ROL_EMPLEADO = "empleado";

    // Usuario actual en sesión (patrón Singleton simple)
    private static String usuarioActual = null;
    private static String rolActual = null;
    private static String nombreCompletoActual = null;

    // Configuración de comisiones por empleada
    private static final Map<String, Double> COMISIONES_EMPLEADAS = new HashMap<>();
    private static final Map<String, String> ESPECIALIDADES_EMPLEADAS = new HashMap<>();

    static {
        // Ashley: 20% - Uñas y Manicure
        COMISIONES_EMPLEADAS.put("Ashley", 0.20);
        ESPECIALIDADES_EMPLEADAS.put("Ashley", "Uñas y Manicure");

        // Monika: 30% - Cejas
        COMISIONES_EMPLEADAS.put("Monika", 0.30);
        ESPECIALIDADES_EMPLEADAS.put("Monika", "Cejas");

        // Veronika: 50% - Depilación
        COMISIONES_EMPLEADAS.put("Veronika", 0.50);
        ESPECIALIDADES_EMPLEADAS.put("Veronika", "Depilación");
    }

    /**
     * Establece el usuario actual en sesión
     */
    public static void setUsuarioActual(String usuario, String rol) {
        usuarioActual = usuario;
        rolActual = rol;
        nombreCompletoActual = usuario;
    }

    /**
     * Establece el usuario actual en sesión con nombre completo
     */
    public static void setUsuarioActual(String usuario, String rol, String nombreCompleto) {
        usuarioActual = usuario;
        rolActual = rol;
        nombreCompletoActual = nombreCompleto;
    }

    /**
     * Obtiene el nombre completo del usuario actual
     */
    public static String getNombreCompletoActual() {
        return nombreCompletoActual;
    }

    /**
     * Obtiene el porcentaje de comisión de una empleada
     */
    public static double getComisionEmpleada(String nombreEmpleada) {
        return COMISIONES_EMPLEADAS.getOrDefault(nombreEmpleada, 0.20);
    }

    /**
     * Obtiene la especialidad de una empleada
     */
    public static String getEspecialidadEmpleada(String nombreEmpleada) {
        return ESPECIALIDADES_EMPLEADAS.getOrDefault(nombreEmpleada, "General");
    }

    /**
     * Obtiene el porcentaje de comisión del usuario actual
     */
    public static double getComisionActual() {
        if (nombreCompletoActual == null) return 0.20;
        return COMISIONES_EMPLEADAS.getOrDefault(nombreCompletoActual, 0.20);
    }

    /**
     * Obtiene la especialidad del usuario actual
     */
    public static String getEspecialidadActual() {
        if (nombreCompletoActual == null) return "General";
        return ESPECIALIDADES_EMPLEADAS.getOrDefault(nombreCompletoActual, "General");
    }

    /**
     * Obtiene el usuario actual
     */
    public static String getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Obtiene el rol del usuario actual
     */
    public static String getRolActual() {
        return rolActual;
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public static boolean hayUsuarioAutenticado() {
        return usuarioActual != null && rolActual != null;
    }

    /**
     * Verifica si el usuario actual es administrador
     */
    public static boolean esAdministrador() {
        return ROL_ADMINISTRADOR.equals(rolActual);
    }

    /**
     * Verifica si el usuario actual es empleado
     */
    public static boolean esEmpleado() {
        return ROL_EMPLEADO.equals(rolActual);
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
        nombreCompletoActual = null;
    }

    /**
     * Hashea una contraseña usando SHA-256 (mismo método que LoginSystemAremi)
     * @param password Contraseña en texto plano
     * @return Hash SHA-256 de la contraseña
     */
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

    /**
     * Verifica si el usuario tiene permisos para una acción específica
     * @param accion Acción a verificar (ej: "ver_nomina", "gestionar_usuarios")
     * @return true si tiene permiso
     */
    public static boolean tienePermiso(String accion) {
        if (!hayUsuarioAutenticado()) return false;

        // Administrador tiene todos los permisos
        if (esAdministrador()) return true;

        // Permisos específicos para empleados
        switch (accion) {
            case "agendar_cita":
            case "registrar_servicio":
            case "registrar_pago":
                return true;
            case "ver_nomina":
            case "gestionar_usuarios":
            case "ver_reportes_financieros":
                return false;
            default:
                return false;
        }
    }
}