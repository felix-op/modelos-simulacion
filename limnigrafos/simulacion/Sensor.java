package limnigrafos.simulacion;

import java.util.Random;

public class Sensor {
    private final Random random;
    private final double desviacionPresion;
    private final double desviacionTemperatura;

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
        return presionReal + random.nextGaussian() * desviacionPresion;
    }

    public double medirTemperatura(double temperaturaReal) {
        return temperaturaReal + random.nextGaussian() * desviacionTemperatura;
    }
}
