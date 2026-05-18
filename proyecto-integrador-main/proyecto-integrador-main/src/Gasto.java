public class Gasto {
    private String concepto;
    private double monto;
    private String fecha;

    public Gasto(String concepto, double monto, String fecha) {
        this.concepto = concepto;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}