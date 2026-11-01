import java.sql.Date;
import java.sql.Timestamp;

public class LiquidacionComision {
    private int id;
    private int idEmpleada;
    private String nombreEmpleada;
    private Date fechaInicio;
    private Date fechaFin;
    private int totalServicios;
    private double totalVentas;
    private double porcentajeAplicado;
    private double totalComision;
    private double totalSalon;
    private Timestamp fechaRegistro;
    private String notas;

    public LiquidacionComision() {}

    public LiquidacionComision(int idEmpleada, String nombreEmpleada, Date fechaInicio,
                               Date fechaFin, int totalServicios, double totalVentas,
                               double porcentajeAplicado, double totalComision, double totalSalon) {
        this.idEmpleada = idEmpleada;
        this.nombreEmpleada = nombreEmpleada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalServicios = totalServicios;
        this.totalVentas = totalVentas;
        this.porcentajeAplicado = porcentajeAplicado;
        this.totalComision = totalComision;
        this.totalSalon = totalSalon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmpleada() {
        return idEmpleada;
    }

    public void setIdEmpleada(int idEmpleada) {
        this.idEmpleada = idEmpleada;
    }

    public String getNombreEmpleada() {
        return nombreEmpleada;
    }

    public void setNombreEmpleada(String nombreEmpleada) {
        this.nombreEmpleada = nombreEmpleada;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getTotalServicios() {
        return totalServicios;
    }

    public void setTotalServicios(int totalServicios) {
        this.totalServicios = totalServicios;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public double getPorcentajeAplicado() {
        return porcentajeAplicado;
    }

    public void setPorcentajeAplicado(double porcentajeAplicado) {
        this.porcentajeAplicado = porcentajeAplicado;
    }

    public double getTotalComision() {
        return totalComision;
    }

    public void setTotalComision(double totalComision) {
        this.totalComision = totalComision;
    }

    public double getTotalSalon() {
        return totalSalon;
    }

    public void setTotalSalon(double totalSalon) {
        this.totalSalon = totalSalon;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNotas() {
        return notas != null ? notas : "";
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
