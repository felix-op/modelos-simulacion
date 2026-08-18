package limnigrafos.simulacion.entorno;

// Temperatura base y amplitud térmica diaria de cada estación del año,
// usadas por GeneradorSimulacion junto con el Clima actual.
public enum Estacion {
    INVIERNO("Invierno", 1.0, 3.0),
    OTONO("Otoño", 5.0, 4.0),
    PRIMAVERA("Primavera", 8.0, 5.0),
    VERANO("Verano", 12.0, 6.0);

    private final String nombre;
    private final double tempBase;
    private final double amplitudTermica;

    Estacion(String nombre, double tempBase, double amplitudTermica) {
        this.nombre = nombre;
        this.tempBase = tempBase;
        this.amplitudTermica = amplitudTermica;
    }

    public double getTempBase() { return tempBase; }
    public double getAmplitudTermica() { return amplitudTermica; }
    @Override public String toString() { return nombre; }
}