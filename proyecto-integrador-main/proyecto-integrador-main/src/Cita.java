import java.sql.Date;

public class Cita {
    private int id;
    private Integer idCliente;
    private String nombreCliente;
    private String telefonoCliente;
    private Integer idServicio;
    private String nombreServicio;
    private Integer idEmpleada;
    private String nombreEmpleada;
    private Date fecha;
    private String hora;
    private String estado; // "Pendiente", "Completada", "Cancelada"
    private String notas;

    public Cita() {
        this.estado = "Pendiente";
    }

    public Cita(String nombreCliente, String telefonoCliente, String nombreServicio,
                String nombreEmpleada, Date fecha, String hora) {
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.nombreServicio = nombreServicio;
        this.nombreEmpleada = nombreEmpleada;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "Pendiente";
    }

    public Cita(int id, Integer idCliente, String nombreCliente, String telefonoCliente,
                Integer idServicio, String nombreServicio, Integer idEmpleada,
                String nombreEmpleada, Date fecha, String hora, String estado, String notas) {
        this.id = id;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.idEmpleada = idEmpleada;
        this.nombreEmpleada = nombreEmpleada;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.notas = notas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public Integer getIdEmpleada() {
        return idEmpleada;
    }

    public void setIdEmpleada(Integer idEmpleada) {
        this.idEmpleada = idEmpleada;
    }

    public String getNombreEmpleada() {
        return nombreEmpleada;
    }

    public void setNombreEmpleada(String nombreEmpleada) {
        this.nombreEmpleada = nombreEmpleada;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado != null ? estado : "Pendiente";
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas != null ? notas : "";
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return fecha + " " + hora + " - " + nombreCliente + " (" + nombreServicio + " con " + nombreEmpleada + ") [" + estado + "]";
    }
}
