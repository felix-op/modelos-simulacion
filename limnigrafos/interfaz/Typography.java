package limnigrafos.interfaz;

import java.awt.Font;

import javax.swing.JLabel;

public class Typography extends JLabel {
    public enum Variante {
        TITULO(Font.BOLD, 22),
        SUBTITULO(Font.BOLD, 15),
        CUERPO(Font.PLAIN, 13),
        DATO(Font.BOLD, 13),
        SECUNDARIO(Font.PLAIN, 12);

        private final int estilo;
        private final int tamanio;

        Variante(int estilo, int tamanio) {
            this.estilo = estilo;
            this.tamanio = tamanio;
        }
    }

    private Variante variante;

    public Typography(String texto) {
        this(texto, Variante.CUERPO);
    }

    public Typography(String texto, Variante variante) {
        super(texto);
        this.variante = variante;
        aplicar(variante);
        TemaOscuro.alCambiar(this::actualizarColor);
    }

    public void aplicar(Variante variante) {
        this.variante = variante;
        setFont(new Font(Font.SANS_SERIF, variante.estilo, variante.tamanio));
        actualizarColor();
    }

    private void actualizarColor() {
        setForeground(variante == Variante.SECUNDARIO
                ? TemaOscuro.textoSecundario()
                : TemaOscuro.texto());
    }

    public void setTexto(String formato, Object... argumentos) {
        setText(String.format(formato, argumentos));
    }
}
