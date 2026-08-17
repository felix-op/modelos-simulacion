package limnigrafos.interfaz;

import javax.swing.JButton;

/** Botón que alterna entre los temas claro y oscuro de la aplicación. */
public class BotonCambiarTema extends JButton {
    public BotonCambiarTema() {
        setFocusable(false);
        addActionListener(event -> TemaOscuro.alternar());
        TemaOscuro.alCambiar(this::actualizarTexto);
        actualizarTexto();
    }

    private void actualizarTexto() {
        boolean oscuro = TemaOscuro.esOscuro();
        setText(oscuro ? "Usar tema claro" : "Usar tema oscuro");
        setToolTipText("Alternar entre tema claro y tema oscuro");
        getAccessibleContext().setAccessibleName(getText());
    }
}
