public class Usuario {
    private String nombre;
    private String rol; // Ej. "administrador", "usuario"

    public Usuario(String nombre, String rol) {
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }
}

