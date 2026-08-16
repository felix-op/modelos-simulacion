package limnigrafos.interfaz;

import java.awt.BorderLayout;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class SliderControl extends JPanel {
    private final JCheckBox automatico;
    private final JSlider slider;
    private final Typography valorActual;
    private final String unidad;
    private final double divisor;
    private boolean actualizando;

    public SliderControl(
            String nombre,
            String unidad,
            int minimo,
            int maximo,
            int valorInicial,
            int separacionPrincipal,
            int separacionSecundaria) {
        this(
                nombre,
                unidad,
                minimo,
                maximo,
                valorInicial,
                separacionPrincipal,
                separacionSecundaria,
                1.0,
                true);
    }

    public SliderControl(
            String nombre,
            String unidad,
            int minimo,
            int maximo,
            int valorInicial,
            int separacionPrincipal,
            int separacionSecundaria,
            double divisor,
            boolean permiteAutomatico) {
        this.unidad = unidad;
        this.divisor = divisor;
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createTitledBorder(nombre));

        automatico = new JCheckBox(nombre + " automático", permiteAutomatico);
        automatico.setVisible(permiteAutomatico);
        slider = new JSlider(minimo, maximo, valorInicial);
        slider.setMajorTickSpacing(separacionPrincipal);
        slider.setMinorTickSpacing(separacionSecundaria);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setEnabled(!permiteAutomatico);

        valorActual = new Typography("", Typography.Variante.DATO);
        actualizarValor(valorInicial / divisor);

        automatico.addActionListener(event ->
                slider.setEnabled(!automatico.isSelected()));
        slider.addChangeListener(event -> {
            if (!automatico.isSelected() && !actualizando) {
                actualizarEtiqueta(slider.getValue() / divisor);
            }
        });

        if (permiteAutomatico) {
            add(automatico, BorderLayout.NORTH);
        }
        add(slider, BorderLayout.CENTER);
        add(valorActual, BorderLayout.SOUTH);
    }

    public void alCambiarModo(Consumer<Boolean> callback) {
        automatico.addActionListener(event -> callback.accept(automatico.isSelected()));
    }

    public void alCambiarValor(DoubleConsumer callback) {
        slider.addChangeListener(event -> {
            if (!automatico.isSelected() && !actualizando) {
                callback.accept(slider.getValue() / divisor);
            }
        });
    }

    public void actualizarValor(double valor) {
        actualizarEtiqueta(valor);
        int valorSlider = (int) Math.round(valor * divisor);
        valorSlider = Math.max(slider.getMinimum(), Math.min(slider.getMaximum(), valorSlider));

        actualizando = true;
        try {
            slider.setValue(valorSlider);
        } finally {
            actualizando = false;
        }
    }

    private void actualizarEtiqueta(double valor) {
        valorActual.setTexto("Valor: %.2f %s", valor, unidad);
    }
}
