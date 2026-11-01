public class Usuario {
    private int id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String rol; // "administrador", "empleada"

    public Usuario() {}

    public Usuario(String username, String rol) {
        this.username = username;
        this.rol = rol;
        this.nombreCompleto = username;
    }

    public Usuario(int id, String username, String password, String nombreCompleto, String rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto != null ? nombreCompleto : username;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombre() {
        return getNombreCompleto();
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean esAdministrador() {
        return rol != null && rol.equalsIgnoreCase("administrador");
    }

    public boolean esEmpleada() {
        return rol != null && (rol.equalsIgnoreCase("empleado") || rol.equalsIgnoreCase("empleada"));
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + rol + ")";
    }
}
