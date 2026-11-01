import java.sql.Date;

public class ServicioRealizado {
    private int id;
    private Integer idCita;
    private int idEmpleada;
    private String nombreEmpleada;
    private int idServicio;
    private String nombreServicio;
    private Integer idCliente;
    private String nombreCliente;
    private Date fecha;
    private double valorCobrado;
    private double porcentajeComision;
    private double comisionCalculada;
    private String estadoLiquidacion; // "Pendiente", "Liquidado"
    private Integer idLiquidacion;

    public ServicioRealizado() {
        this.estadoLiquidacion = "Pendiente";
    }

    public ServicioRealizado(Integer idCita, int idEmpleada, String nombreEmpleada,
                             int idServicio, String nombreServicio, Integer idCliente,
                             String nombreCliente, Date fecha, double valorCobrado,
                             double porcentajeComision) {
        this.idCita = idCita;
        this.idEmpleada = idEmpleada;
        this.nombreEmpleada = nombreEmpleada;
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.valorCobrado = valorCobrado;
        this.porcentajeComision = porcentajeComision;
        this.comisionCalculada = valorCobrado * (porcentajeComision / 100.0);
        this.estadoLiquidacion = "Pendiente";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
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

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(double valorCobrado) {
        this.valorCobrado = valorCobrado;
        this.comisionCalculada = valorCobrado * (this.porcentajeComision / 100.0);
    }

    public double getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(double porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
        this.comisionCalculada = this.valorCobrado * (porcentajeComision / 100.0);
    }

    public double getComisionCalculada() {
        return comisionCalculada;
    }

    public void setComisionCalculada(double comisionCalculada) {
        this.comisionCalculada = comisionCalculada;
    }

    public String getEstadoLiquidacion() {
        return estadoLiquidacion != null ? estadoLiquidacion : "Pendiente";
    }

    public void setEstadoLiquidacion(String estadoLiquidacion) {
        this.estadoLiquidacion = estadoLiquidacion;
    }

    public Integer getIdLiquidacion() {
        return idLiquidacion;
    }

    public void setIdLiquidacion(Integer idLiquidacion) {
        this.idLiquidacion = idLiquidacion;
    }
}
