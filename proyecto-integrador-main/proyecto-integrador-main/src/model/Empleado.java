package model;

public class Empleado {
    private int id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String email;
    private String especialidad;
    private double porcentajeComision; // Ej: 20.0 para 20%
    private String username;
    private String rol = "empleada";   // "empleada" o "administrador"
    private boolean activo;

    public Empleado() {
        this.activo = true;
    }

    public Empleado(String nombre, String telefono, String especialidad, double porcentajeComision) {
        this(0, nombre, telefono, especialidad, porcentajeComision, "", true, "", "", "empleada");
    }

    public Empleado(int id, String nombre, String telefono, String especialidad, double porcentajeComision, String username, boolean activo) {
        this(id, nombre, telefono, especialidad, porcentajeComision, username, activo, "", "", "empleada");
    }

    public Empleado(int id, String nombre, String telefono, String especialidad, double porcentajeComision, String username, boolean activo, String direccion, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.porcentajeComision = porcentajeComision;
        this.username = username;
        this.activo = activo;
        this.direccion = direccion != null ? direccion : "";
        this.email = email != null ? email : "";
        this.rol = (rol != null && !rol.trim().isEmpty()) ? rol.toLowerCase().trim() : "empleada";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion != null ? direccion : ""; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEmail() { return email != null ? email : ""; }
    public void setEmail(String email) { this.email = email; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public double getPorcentajeComision() { return porcentajeComision; }
    public void setPorcentajeComision(double porcentajeComision) { this.porcentajeComision = porcentajeComision; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRol() { return rol != null ? rol : "empleada"; }
    public void setRol(String rol) { this.rol = (rol != null && !rol.trim().isEmpty()) ? rol.toLowerCase().trim() : "empleada"; }

    public boolean esAdministrador() {
        return "administrador".equalsIgnoreCase(this.rol) || "admin".equalsIgnoreCase(this.rol);
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public double calcularComision(double valorServicio) {
        return valorServicio * (this.porcentajeComision / 100.0);
    }

    @Override
    public String toString() {
        return nombre + " (" + especialidad + " - " + String.format("%.0f%%", porcentajeComision) + ")";
    }
}
