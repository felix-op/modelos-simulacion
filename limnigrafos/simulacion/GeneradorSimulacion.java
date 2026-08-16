package limnigrafos.simulacion;

public class GeneradorSimulacion {

    /**
     * Genera automáticamente un valor simulado para el nivel de agua.
     *
     * El nivel se calcula utilizando una función seno, lo que permite
     * representar una variación periódica del nivel a medida que avanza
     * el tiempo.
     *
     * El valor oscila aproximadamente entre 25 cm y 125 cm:
     * - 75 cm representa el nivel medio.
     * - 50 cm representa la amplitud de la variación.
     * - 0.08 determina la velocidad con la que cambia el nivel.
     *
     * @param tiempo instante de la simulación.
     * @return nivel de agua simulado en centímetros.
     */
    public double generarNivel(int tiempo) {

        return 75
                + 50
                * Math.sin(tiempo * 0.08);
    }

    /**
     * Genera automáticamente un valor simulado para la temperatura.
     *
     * Se utiliza una función seno para simular cambios periódicos
     * de temperatura a medida que avanza el tiempo.
     *
     * El valor oscila aproximadamente entre 5 °C y 35 °C:
     * - 20 °C representa la temperatura media.
     * - 15 °C representa la amplitud de la variación.
     * - 0.03 determina la velocidad con la que cambia la temperatura.
     *
     * @param tiempo instante de la simulación.
     * @return temperatura simulada en grados Celsius.
     */
    public double generarTemperatura(int tiempo) {

        return 20
                + 15
                * Math.sin(tiempo * 0.03);
    }
}