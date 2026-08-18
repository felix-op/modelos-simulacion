package limnigrafos.simulacion;

// Modela la física que traduce nivel de agua <-> presión hidrostática,
// incluyendo los casos límite de congelamiento que el sensor real reporta como falla.
public class ModeloFisico {
    private static final double GRAVEDAD = 9.81;

    public double calcularDensidad(double temperatura) {

        if (temperatura <= 0.0) {
            return 916.8; // Densidad aproximada del hielo
        }

        // Aproximación cuadrática de la densidad del agua alrededor de su punto
        // de máxima densidad (4 °C): a esa temperatura da exactamente 1000.0 kg/m³
        // y decrece hacia ambos lados, como ocurre con el agua real.
        return 1000.0 - 0.005 * Math.pow(temperatura - 4.0, 2);
    }

    public double calcularPresion(double nivelCentimetros, double densidad, double temperatura) {
        // El congelamiento del agua alrededor del cabezal terminaría rompiéndolo
        // físicamente. Se emula esa falla estructural devolviendo un código de
        // error en lugar de una presión válida cuando T <= 0.
        if (temperatura <= 0.0) {
            return -1000.0; // Código de falla (ver CODIGO_ERROR_HARDWARE en Sensor)
        }

        // Presión hidrostática P = densidad * gravedad * altura de la columna de agua.
        double alturaMetros = nivelCentimetros / 100.0;
        return densidad * GRAVEDAD * alturaMetros;
    }

    public double calcularNivel(double presion, double densidad) {
        // Si la presión ya viene marcada como error (ver calcularPresion/Sensor),
        // no tiene sentido despejar un nivel: propagamos un código de error propio
        // en vez de devolver un nivel físicamente imposible.
        if (presion < 0.0) {
            return -1.0;
        }

        // Despeje de la presión hidrostática para obtener la altura de la columna.
        double alturaMetros = presion / (densidad * GRAVEDAD);
        return alturaMetros * 100.0;
    }
}