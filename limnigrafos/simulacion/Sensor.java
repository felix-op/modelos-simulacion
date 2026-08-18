package limnigrafos.simulacion;

import java.util.Random;

// Simula el sensor físico: toma un valor "real" (calculado por ModeloFisico
// o generado por GeneradorSimulacion) y le agrega el ruido de medición que
// tendría el hardware real, salvo que el valor sea un código de falla.
public class Sensor {
    private final Random random;
    private final double desviacionPresion;
    private final double desviacionTemperatura;

    // cuando no responde o falla (ver errorAction() en limnigrafo-firmware.ino).
    private static final double CODIGO_ERROR_HARDWARE = -1000.0;

    public Sensor() {
        this(50.0, 0.1);
    }

    public Sensor(double desviacionPresion) {
        this(desviacionPresion, 0.1);
    }

    public Sensor(double desviacionPresion, double desviacionTemperatura) {
        if (desviacionPresion < 0 || desviacionTemperatura < 0) {
            throw new IllegalArgumentException("Las desviaciones no pueden ser negativas");
        }
        this.random = new Random();
        this.desviacionPresion = desviacionPresion;
        this.desviacionTemperatura = desviacionTemperatura;
    }

    public double medirPresion(double presionReal) {
        // Un código de falla no es una medición: si le sumáramos ruido
        // gaussiano dejaría de ser reconocible como error más abajo en la
        // cadena (ModeloFisico.calcularNivel), así que se propaga intacto.
        if (presionReal == CODIGO_ERROR_HARDWARE) {
            return CODIGO_ERROR_HARDWARE;
        }
        return presionReal + random.nextGaussian() * desviacionPresion;
    }

    public double medirTemperatura(double temperaturaReal) {
        // La temperatura nunca es un código de error (solo la presión lo es),
        // por eso acá el ruido gaussiano se aplica siempre.
        return temperaturaReal + random.nextGaussian() * desviacionTemperatura;
    }
}