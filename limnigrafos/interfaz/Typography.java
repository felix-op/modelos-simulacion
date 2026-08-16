package limnigrafos.interfaz;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class Typography extends JLabel {
    public enum Variante {
        TITULO(Font.BOLD, 22, TemaOscuro.TEXTO),
        SUBTITULO(Font.BOLD, 15, TemaOscuro.TEXTO),
        CUERPO(Font.PLAIN, 13, TemaOscuro.TEXTO),
        DATO(Font.BOLD, 13, TemaOscuro.TEXTO),
        SECUNDARIO(Font.PLAIN, 12, TemaOscuro.TEXTO_SECUNDARIO);

        private final int estilo;
        private final int tamanio;
        private final Color color;

        Variante(int estilo, int tamanio, Color color) {
            this.estilo = estilo;
            this.tamanio = tamanio;
            this.color = color;
        }
    }

    public Typography(String texto) {
        this(texto, Variante.CUERPO);
    }

    public Typography(String texto, Variante variante) {
        super(texto);
        aplicar(variante);
    }

    public void aplicar(Variante variante) {
        setFont(new Font(Font.SANS_SERIF, variante.estilo, variante.tamanio));
        setForeground(variante.color);
    }

    public void setTexto(String formato, Object... argumentos) {
        setText(String.format(formato, argumentos));
    }
}

