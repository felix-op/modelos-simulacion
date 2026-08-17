package limnigrafos.interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class SelectorGenerico<T> extends JPanel {
    private static final int ALTO = 58;
    private final JComboBox<T> comboBox;

    public SelectorGenerico(String titulo, T[] opciones, T valorInicial) {
        setLayout(new BorderLayout(0, 6));
        setBorder(crearBordeTitulo(titulo));
        setOpaque(false);
        setMinimumSize(new Dimension(0, ALTO));
        setPreferredSize(new Dimension(0, ALTO));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO));

        comboBox = new JComboBox<>(opciones);
        comboBox.setSelectedItem(valorInicial);
        comboBox.putClientProperty("JComponent.roundRect", true);

        add(comboBox, BorderLayout.CENTER);
        TemaOscuro.alCambiar(() -> {
            setBorder(crearBordeTitulo(titulo));
        });
    }

    public void alCambiarValor(Consumer<T> callback) {
        comboBox.addActionListener(event -> {
            @SuppressWarnings("unchecked")
            T item = (T) comboBox.getSelectedItem();
            callback.accept(item);
        });
    }

    private static javax.swing.border.Border crearBordeTitulo(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                titulo);
    }
}
