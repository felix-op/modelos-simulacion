package limnigrafos.interfaz;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class Ventana extends JFrame {
    public Ventana(String titulo) {
        setTitle(titulo);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(TemaOscuro.FONDO);
    }

    public void mostrar() {
        setVisible(true);
    }

    public void alCerrar(Runnable accion) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                accion.run();
            }
        });
    }
}
