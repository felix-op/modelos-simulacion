package limnigrafos.modelos;

public class Limnigrafo {
    private String codigo;
    private String ubicacion;
    private int tiempoRecoleccionSegundos;
    private int tiempoEnvioSegundos;
    private double bateria;

    public Limnigrafo(String codigo, String ubicacion, int tiempoRecoleccionSegundos, int tiempoEnvioSegundos,
            double bateria) {
        this.codigo = codigo;
        this.ubicacion = ubicacion;
        this.tiempoRecoleccionSegundos = tiempoRecoleccionSegundos;
        this.tiempoEnvioSegundos = tiempoEnvioSegundos;
        this.bateria = bateria;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public int getTiempoRecoleccionSegundos() {
        return tiempoRecoleccionSegundos;
    }
    public void setTiempoRecoleccionSegundos(int tiempoRecoleccionSegundos) {
        this.tiempoRecoleccionSegundos = tiempoRecoleccionSegundos;
    }
    public int getTiempoEnvioSegundos() {
        return tiempoEnvioSegundos;
    }
    public void setTiempoEnvioSegundos(int tiempoEnvioSegundos) {
        this.tiempoEnvioSegundos = tiempoEnvioSegundos;
    }
    public double getBateria() {
        return bateria;
    }
    public void setBateria(double bateria) {
        this.bateria = bateria;
    }
}
