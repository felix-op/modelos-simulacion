package limnigrafos.simulacion;

public class ModeloFisico {
    private static final double GRAVEDAD = 9.81;

    public double calcularDensidad(double temperatura) {

        if (temperatura <= 0.0) {
            return 916.8; // Densidad aproximada del hielo
        }
        
        // a 4 °C, esto devuelve exactamente 1000.0 kg/m³.
        return 1000.0 - 0.005 * Math.pow(temperatura - 4.0, 2);
    }

    public double calcularPresion(double nivelCentimetros, double densidad, double temperatura) {
        // el congelamiento debería colapsa el cabezal del sensor.
        // Emulamos esto propagando una falla estructural si T <= 0.
        if (temperatura <= 0.0) {
            return -1000.0; //dato que significa falla, en la especificación del sensor lo diría.
        }
        
        double alturaMetros = nivelCentimetros / 100.0;
        return densidad * GRAVEDAD * alturaMetros;
    }

    public double calcularNivel(double presion, double densidad) {
        // Si recibimos un código de error de presión negativa, lo propagamos
        if (presion < 0.0) {
            return -1.0; 
        }
        
        double alturaMetros = presion / (densidad * GRAVEDAD);
        return alturaMetros * 100.0;
    }
}