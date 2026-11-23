package model;

public class Servicio {
    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private double precio;
    private int duracionMinutos;
    private boolean activo;

    public Servicio() {
        this.duracionMinutos = 60;
        this.categoria = "General";
        this.activo = true;
    }

    public Servicio(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
        this.duracionMinutos = 60;
        this.categoria = "General";
        this.activo = true;
    }

    public Servicio(int id, String nombre, String descripcion, String categoria, double precio, int duracionMinutos, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return nombre; }
    public void setTipo(String tipo) { this.nombre = tipo; }

    public String getDescripcion() { return descripcion != null ? descripcion : ""; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria != null ? categoria : "General"; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre + " ($" + String.format("%,.0f", precio) + ")";
    }
}
