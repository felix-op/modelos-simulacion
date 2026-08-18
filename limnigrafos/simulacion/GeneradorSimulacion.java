package limnigrafos.simulacion;

import limnigrafos.simulacion.entorno.Clima;
import limnigrafos.simulacion.entorno.Estacion;

// Genera los valores "reales" de nivel y temperatura del río cuando el
// simulador está en modo automático (sin intervención manual del operador).
// No mide nada: representa el entorno físico que después el Sensor mide con ruido.
public class GeneradorSimulacion {
    private Estacion estacion = Estacion.PRIMAVERA;
    private Clima clima = Clima.SOLEADO;

    public void setEstacion(Estacion estacion) { this.estacion = estacion; }
    public void setClima(Clima clima) { this.clima = clima; }

    public double generarNivel(int tiempo) {
        double base = clima.getNivelBase();
        double amplitud = clima.getAmplitudNivel();
        double vel = clima.getVelocidadCambio();

        // Oscilación principal del nivel del río alrededor de su base.
        double nivel = base + amplitud * Math.sin(tiempo * vel);

        if (clima == Clima.TORMENTA) {
            // Durante una tormenta se superpone un segundo componente de
            // frecuencia más alta (3.5x) para simular el oleaje/turbulencia
            // errática que no aparece con los otros climas.
            nivel += (amplitud * 0.4) * Math.cos(tiempo * vel * 3.5);
        }

        // El nivel de un río no puede ser negativo.
        return Math.max(0.0, nivel);
    }

    public double generarTemperatura(int tiempo) {
        double tempBase = estacion.getTempBase() + clima.getModTemp();
        // Ciclo térmico principal (día/noche a lo largo de la simulación).
        double variacionPrincipal = estacion.getAmplitudTermica() * Math.sin(tiempo * 0.005);
        // Segundo componente, de mucha menor amplitud (10%) y frecuencia más
        // alta, que agrega una pequeña variación de "microclima" para que la
        // curva no sea una sinusoide perfecta.
        double microBrisa = (estacion.getAmplitudTermica() * 0.1) * Math.cos(tiempo * 0.05);

        return tempBase + variacionPrincipal + microBrisa;
    }
}