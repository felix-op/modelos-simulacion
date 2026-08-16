package limnigrafos.interfaz;

import java.awt.Color;

import javax.swing.UIManager;

public final class TemaOscuro {
    public static final Color FONDO = new Color(15, 23, 32);
    public static final Color SUPERFICIE = new Color(24, 34, 45);
    public static final Color SUPERFICIE_SECUNDARIA = new Color(31, 43, 56);
    public static final Color TEXTO = new Color(230, 237, 243);
    public static final Color TEXTO_SECUNDARIO = new Color(155, 170, 184);
    public static final Color BORDE = new Color(58, 72, 86);
    public static final Color ACENTO = new Color(72, 190, 230);

    private TemaOscuro() {
    }

    public static void aplicar() {
        UIManager.put("Panel.background", FONDO);
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("CheckBox.background", SUPERFICIE);
        UIManager.put("CheckBox.foreground", TEXTO);
        UIManager.put("Slider.background", SUPERFICIE);
        UIManager.put("Slider.foreground", TEXTO_SECUNDARIO);
        UIManager.put("Slider.tickColor", TEXTO_SECUNDARIO);
        UIManager.put("TitledBorder.titleColor", TEXTO);
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("ToolTip.background", SUPERFICIE_SECUNDARIA);
        UIManager.put("ToolTip.foreground", TEXTO);
        UIManager.put("ToolTip.border", javax.swing.BorderFactory.createLineBorder(BORDE));
    }
}

