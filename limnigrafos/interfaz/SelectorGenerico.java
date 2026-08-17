package limnigrafos.interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class SelectorGenerico<T> extends JPanel {
    private final JComboBox<T> comboBox;

    public SelectorGenerico(String titulo, T[] opciones, T valorInicial) {
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createTitledBorder(titulo));

        comboBox = new JComboBox<>(opciones);
        comboBox.setSelectedItem(valorInicial);

        comboBox.setUI(new BasicComboBoxUI()); 
        comboBox.setBackground(TemaOscuro.SUPERFICIE_SECUNDARIA);
        comboBox.setForeground(TemaOscuro.TEXTO);
        comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value != null) {
                    setText(value.toString());
                }
                return this;
            }
        });

        add(comboBox, BorderLayout.CENTER);
    }

    public void alCambiarValor(Consumer<T> callback) {
        comboBox.addActionListener(event -> {
            @SuppressWarnings("unchecked")
            T item = (T) comboBox.getSelectedItem();
            callback.accept(item);
        });
    }
}