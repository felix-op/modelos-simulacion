package limnigrafos.interfaz;

import java.awt.Color;
import java.awt.Window;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public final class TemaOscuro {
    public enum Modo {
        CLARO,
        OSCURO
    }

    private static final Color COLOR_FALLBACK = new Color(15, 23, 32);
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();
    private static Modo modo = Modo.OSCURO;

    private TemaOscuro() {
    }

    /**
     * Conserva el comportamiento anterior: inicia la aplicación en oscuro.
     */
    public static void aplicar() {
        aplicar(Modo.OSCURO);
    }

    public static void aplicar(Modo nuevoModo) {
        if (nuevoModo == null) {
            throw new IllegalArgumentException("El modo de tema no puede ser null");
        }

        try {
            UIManager.setLookAndFeel(nuevoModo == Modo.OSCURO
                    ? new FlatDarkLaf()
                    : new FlatLightLaf());
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo aplicar FlatLaf", exception);
        }

        modo = nuevoModo;
        Runnable actualizarInterfaz = () -> {
            for (Window ventana : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(ventana);
                ventana.invalidate();
                ventana.validate();
                ventana.repaint();
            }
            LISTENERS.forEach(Runnable::run);
        };

        if (SwingUtilities.isEventDispatchThread()) {
            actualizarInterfaz.run();
        } else {
            SwingUtilities.invokeLater(actualizarInterfaz);
        }
    }

    public static void alternar() {
        aplicar(esOscuro() ? Modo.CLARO : Modo.OSCURO);
    }

    public static boolean esOscuro() {
        return modo == Modo.OSCURO;
    }

    public static void alCambiar(Runnable listener) {
        if (listener != null) {
            LISTENERS.add(listener);
        }
    }

    public static Color fondo() {
        return color("Panel.background", COLOR_FALLBACK);
    }

    public static Color superficie() {
        return color("Panel.background", COLOR_FALLBACK);
    }

    public static Color superficieSecundaria() {
        return color("TextField.background", COLOR_FALLBACK);
    }

    public static Color texto() {
        return color("Label.foreground", Color.WHITE);
    }

    public static Color textoSecundario() {
        return color("Label.disabledForeground", texto());
    }

    public static Color borde() {
        return color("Component.borderColor", Color.GRAY);
    }

    public static Color acento() {
        return color("Component.focusColor", new Color(72, 190, 230));
    }

    private static Color color(String clave, Color fallback) {
        Color color = UIManager.getColor(clave);
        return color != null ? color : fallback;
    }
}
