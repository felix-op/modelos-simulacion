package limnigrafos.modelos;

import java.time.LocalDateTime;

public class Medicion {
    private double temperatura;
    private double presion;
    private double nivelAgua;
    private double bateria;
    private LocalDateTime fechaHora;

    public Medicion(double temperatura, double presion, double nivelAgua, double bateria,
            LocalDateTime fechaHora) {
        this.temperatura = temperatura;
        this.presion = presion;
        this.nivelAgua = nivelAgua;
        this.bateria = bateria;
        this.fechaHora = fechaHora;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getPresion() {
        return presion;
    }

    public void setPresion(double presion) {
        this.presion = presion;
    }

    public double getNivelAgua() {
        return nivelAgua;
    }

    public void setNivelAgua(double nivelAgua) {
        this.nivelAgua = nivelAgua;
    }

    public double getBateria() {
        return bateria;
    }

    public void setBateria(double bateria) {
        this.bateria = bateria;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
