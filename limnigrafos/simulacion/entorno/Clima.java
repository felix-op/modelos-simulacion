package limnigrafos.simulacion.entorno;

// Parámetros que usa GeneradorSimulacion para sintetizar nivel y temperatura
// del río. nivelBase/amplitudNivel/velocidadCambio a mayor climas más severos
// (más agua, más oscilación y más rápido); modTemp se suma a la temperatura
// base de la Estacion actual.
public enum Clima {
    SOLEADO("Soleado", 2.0, 45.0, 10.0, 0.005),     // Calienta un poco, río bajo y calmo
    NUBLADO("Nublado", -1.0, 50.0, 15.0, 0.008),    // Enfría un poco, río normal
    LLUVIOSO("Lluvioso", -2.0, 85.0, 35.0, 0.015),  // Río alto y oscilante
    TORMENTA("Tormenta", -4.0, 130.0, 60.0, 0.035); // Enfría mucho, río al borde del desborde y caótico

    private final String nombre;
    private final double modTemp;
    private final double nivelBase;
    private final double amplitudNivel;
    private final double velocidadCambio;

    Clima(String nombre, double modTemp, double nivelBase, double amplitudNivel, double velocidadCambio) {
        this.nombre = nombre;
        this.modTemp = modTemp;
        this.nivelBase = nivelBase;
        this.amplitudNivel = amplitudNivel;
        this.velocidadCambio = velocidadCambio;
    }

    public double getModTemp() { return modTemp; }
    public double getNivelBase() { return nivelBase; }
    public double getAmplitudNivel() { return amplitudNivel; }
    public double getVelocidadCambio() { return velocidadCambio; }
    @Override public String toString() { return nombre; }
}