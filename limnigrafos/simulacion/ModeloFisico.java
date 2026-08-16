package limnigrafos.simulacion;


public class ModeloFisico {

    private static final double GRAVEDAD = 9.81;

    /**
     * Calcula una aproximación de la densidad
     * del agua dependiendo de la temperatura.
     */
    public double calcularDensidad(double temperatura) {

        return 1000.0
                - 0.3 * (temperatura - 4);
    }

    /**
     * Calcula la presión hidrostática.
     *
     * P = rho * g * h
     *
     * La altura se recibe en centímetros.
     * La presión se devuelve en Pascal.
     */
    public double calcularPresion(
            double nivelCentimetros,
            double densidad) {

        double alturaMetros =
                nivelCentimetros / 100.0;

        return densidad
                * GRAVEDAD
                * alturaMetros;
    }

    /**
     * Calcula el nivel a partir de la presión.
     *
     * h = P / (rho * g)
     *
     * Devuelve centímetros.
     */
    public double calcularNivel(
            double presion,
            double densidad) {

        double alturaMetros =
                presion / (densidad * GRAVEDAD);

        return alturaMetros * 100.0;
    }
}