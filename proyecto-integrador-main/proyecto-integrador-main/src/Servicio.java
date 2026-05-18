public class Servicio {
    private String tipo;
    private double precio;
    private String empleada;
    private String fecha;

    public Servicio(String tipo, double precio, String empleada, String fecha) {
        this.tipo = tipo;
        this.precio = precio;
        this.empleada = empleada;
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
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
}