public class Servicio {
    private int id;
    private String tipo; // nombre del servicio
    private String descripcion;
    private String categoria;
    private double precio;
    private int duracionMinutos;
    private String empleada;
    private String fecha;

    public Servicio() {
        this.duracionMinutos = 60;
    }

    public Servicio(String tipo, double precio) {
        this.tipo = tipo;
        this.precio = precio;
        this.duracionMinutos = 60;
        this.categoria = "General";
    }

    public Servicio(String tipo, double precio, String empleada, String fecha) {
        this.tipo = tipo;
        this.precio = precio;
        this.empleada = empleada;
        this.fecha = fecha;
        this.duracionMinutos = 60;
        this.categoria = "General";
    }

    public Servicio(int id, String tipo, String descripcion, String categoria, double precio, int duracionMinutos) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return tipo;
    }

    public void setNombre(String nombre) {
        this.tipo = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion != null ? descripcion : "";
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria != null ? categoria : "General";
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getEmpleada() {
        return empleada;
    }

    public void setEmpleada(String empleada) {
        this.empleada = empleada;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return tipo + " ($" + String.format("%,.0f", precio) + ")";
    }
}
