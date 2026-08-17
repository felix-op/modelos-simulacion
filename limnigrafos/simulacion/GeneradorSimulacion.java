package limnigrafos.simulacion;

import limnigrafos.simulacion.entorno.Clima;
import limnigrafos.simulacion.entorno.Estacion;

public class GeneradorSimulacion {
    private Estacion estacion = Estacion.PRIMAVERA;
    private Clima clima = Clima.SOLEADO;

    public void setEstacion(Estacion estacion) { this.estacion = estacion; }
    public void setClima(Clima clima) { this.clima = clima; }

    public double generarNivel(int tiempo) {
        double base = clima.getNivelBase();
        double amplitud = clima.getAmplitudNivel();
        double vel = clima.getVelocidadCambio();
        
        double nivel = base + amplitud * Math.sin(tiempo * vel);
        
        if (clima == Clima.TORMENTA) {
            nivel += (amplitud * 0.4) * Math.cos(tiempo * vel * 3.5); 
        }
        
        return Math.max(0.0, nivel);
    }

    public double generarTemperatura(int tiempo) {
        double tempBase = estacion.getTempBase() + clima.getModTemp();
        double variacionPrincipal = estacion.getAmplitudTermica() * Math.sin(tiempo * 0.005);
        double microBrisa = (estacion.getAmplitudTermica() * 0.1) * Math.cos(tiempo * 0.05);
        
        return tempBase + variacionPrincipal + microBrisa;
    }
}