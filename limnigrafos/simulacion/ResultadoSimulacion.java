package limnigrafos.simulacion;

import limnigrafos.modelos.Medicion;

public class ResultadoSimulacion {
    private final long numeroMuestra;
    private final double nivelReal;
    private final double temperaturaReal;
    private final double densidadReal;
    private final double presionReal;
    private final double densidadMedida;
    private final Medicion medicion;

    public ResultadoSimulacion(
            long numeroMuestra,
            double nivelReal,
            double temperaturaReal,
            double densidadReal,
            double presionReal,
            double densidadMedida,
            Medicion medicion) {
        this.numeroMuestra = numeroMuestra;
        this.nivelReal = nivelReal;
        this.temperaturaReal = temperaturaReal;
        this.densidadReal = densidadReal;
        this.presionReal = presionReal;
        this.densidadMedida = densidadMedida;
        this.medicion = medicion;
    }

    public long getNumeroMuestra() {
        return numeroMuestra;
    }

    public double getNivelReal() {
        return nivelReal;
    }

    public double getTemperaturaReal() {
        return temperaturaReal;
    }

    public double getDensidadReal() {
        return densidadReal;
    }

    public double getPresionReal() {
        return presionReal;
    }

    public double getDensidadMedida() {
        return densidadMedida;
    }

    public Medicion getMedicion() {
        return medicion;
    }
}

