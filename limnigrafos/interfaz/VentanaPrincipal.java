package limnigrafos.interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import limnigrafos.modelos.Limnigrafo;
import limnigrafos.simulacion.ResultadoSimulacion;

public class VentanaPrincipal extends Ventana {
    private static final double PASCALES_POR_HECTOPASCAL = 100.0;

    private final Header header;
    private final SliderControl controlNivel;
    private final SliderControl controlTemperatura;
    private final SliderControl controlBateria;
    private final GraficoJFreeChart graficoNivel;
    private final GraficoJFreeChart graficoPresion;
    private final GraficoJFreeChart graficoTemperatura;

    public VentanaPrincipal(Limnigrafo limnigrafo) {
        super("Simulador de Limnígrafo por Presión Hidrostática");

        header = new Header(limnigrafo);
        controlNivel = new SliderControl("Nivel", "cm", 0, 150, 100, 50, 10);
        controlTemperatura = new SliderControl("Temperatura", "°C", -20, 80, 20, 20, 5);
        controlBateria = new SliderControl(
                "Batería",
                "V",
                0,
                15000,
                (int) Math.round(limnigrafo.getBateria() * 1000),
                5000,
                1000,
                1000.0,
                false);
        graficoNivel = new GraficoJFreeChart(
                "Nivel del agua", "cm", 0, 150, "Real", "Medido");
        graficoPresion = new GraficoJFreeChart(
                "Presión hidrostática", "hPa", 0, 160, "Real", "Medida");
        graficoTemperatura = new GraficoJFreeChart(
                "Temperatura", "°C", -20, 80, "Real", "Medida");

        add(header, BorderLayout.NORTH);
        add(crearPanelControles(), BorderLayout.WEST);
        add(crearPanelGraficos(), BorderLayout.CENTER);
    }

    public void configurarControles(
            Consumer<Boolean> alCambiarNivelAutomatico,
            DoubleConsumer alCambiarNivelManual,
            Consumer<Boolean> alCambiarTemperaturaAutomatica,
            DoubleConsumer alCambiarTemperaturaManual,
            DoubleConsumer alCambiarBateria) {
        controlNivel.alCambiarModo(alCambiarNivelAutomatico);
        controlNivel.alCambiarValor(alCambiarNivelManual);
        controlTemperatura.alCambiarModo(alCambiarTemperaturaAutomatica);
        controlTemperatura.alCambiarValor(alCambiarTemperaturaManual);
        controlBateria.alCambiarValor(alCambiarBateria);
    }

    public void actualizar(ResultadoSimulacion resultado) {
        header.actualizar(resultado);
        controlNivel.actualizarValor(resultado.getNivelReal());
        controlTemperatura.actualizarValor(resultado.getTemperaturaReal());
        graficoNivel.agregarDato("Real", resultado.getMedicion().getFechaHora(), resultado.getNivelReal());
        graficoNivel.agregarDato(
                "Medido", resultado.getMedicion().getFechaHora(), resultado.getMedicion().getNivelAgua());
        graficoPresion.agregarDato(
                "Real",
                resultado.getMedicion().getFechaHora(),
                resultado.getPresionReal() / PASCALES_POR_HECTOPASCAL);
        graficoPresion.agregarDato(
                "Medida",
                resultado.getMedicion().getFechaHora(),
                resultado.getMedicion().getPresion() / PASCALES_POR_HECTOPASCAL);
        graficoTemperatura.agregarDato(
                "Real", resultado.getMedicion().getFechaHora(), resultado.getTemperaturaReal());
        graficoTemperatura.agregarDato(
                "Medida", resultado.getMedicion().getFechaHora(), resultado.getMedicion().getTemperatura());
    }

    public void actualizarBateria(double bateria) {
        header.actualizarBateria(bateria);
        controlBateria.actualizarValor(bateria);
    }

    public void marcarCorte(LocalDateTime fechaHora) {
        graficoNivel.agregarCorte("Real", fechaHora);
        graficoNivel.agregarCorte("Medido", fechaHora);
        graficoPresion.agregarCorte("Real", fechaHora);
        graficoPresion.agregarCorte("Medida", fechaHora);
        graficoTemperatura.agregarCorte("Real", fechaHora);
        graficoTemperatura.agregarCorte("Medida", fechaHora);
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        panel.setPreferredSize(new Dimension(340, 0));
        panel.add(controlNivel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(controlTemperatura);
        panel.add(Box.createVerticalStrut(20));
        panel.add(controlBateria);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel crearPanelGraficos() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        panel.add(graficoNivel);
        panel.add(graficoPresion);
        panel.add(graficoTemperatura);
        return panel;
    }
}
