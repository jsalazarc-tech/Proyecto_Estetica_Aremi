public class Empleado {
    private int id;
    private String nombre;
    private String telefono;
    private String especialidad;
    private double porcentajeComision; // Ej: 20.0 para 20%
    private String username;
    private boolean activo;

    public Empleado() {
        this.activo = true;
    }

    public Empleado(String nombre, String telefono, String especialidad, double porcentajeComision) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.porcentajeComision = porcentajeComision;
        this.activo = true;
    }

    public Empleado(int id, String nombre, String telefono, String especialidad, double porcentajeComision, String username, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.porcentajeComision = porcentajeComision;
        this.username = username;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public double getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(double porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre + " (" + especialidad + " - " + String.format("%.0f%%", porcentajeComision) + ")";
    }
}
