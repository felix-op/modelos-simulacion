package limnigrafos.modelos;

public class MedicionReal {
    private double temperatura;
    private double nivelAgua;

    public MedicionReal(double temperatura, double nivelAgua) {
        this.temperatura = temperatura;
        this.nivelAgua = nivelAgua;
    }

    public double getTemperatura() {
        return temperatura;
    }
    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }
    public double getNivelAgua() {
        return nivelAgua;
    }
    public void setNivelAgua(double nivelAgua) {
        this.nivelAgua = nivelAgua;
    }
}
