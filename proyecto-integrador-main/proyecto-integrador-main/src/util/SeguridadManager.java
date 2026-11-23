package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Gestor en memoria de la sesión activa del usuario y funciones criptográficas de Hash.
 * No realiza consultas SQL directas; mantiene el desacoplamiento de capas.
 */
public class SeguridadManager {
    public static final String ROL_ADMINISTRADOR = "administrador";
    public static final String ROL_EMPLEADA = "empleada";

    private static String usuarioActual = null;
    private static String rolActual = null;
    private static String nombreCompletoActual = null;
    private static Integer idEmpleadaActual = null;

    public static void setUsuarioActual(String usuario, String rol, String nombreCompleto, Integer idEmpleada) {
        usuarioActual = usuario;
        rolActual = (rol != null) ? rol.toLowerCase().trim() : "empleada";
        nombreCompletoActual = nombreCompleto != null ? nombreCompleto : usuario;
        idEmpleadaActual = idEmpleada;
    }

    public static String getUsuarioActual() { return usuarioActual; }
    public static String getRolActual() { return rolActual; }
    public static String getNombreCompletoActual() { return nombreCompletoActual != null ? nombreCompletoActual : "Usuario"; }
    public static Integer getIdEmpleadaActual() { return idEmpleadaActual; }

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

    public static String hashPassword(String password) {
        if (password == null) return null;
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
