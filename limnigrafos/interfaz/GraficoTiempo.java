package limnigrafos.interfaz;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.BorderFactory;

public class GraficoTiempo extends JPanel {

    private final String titulo;

    private final double minimo;

    private final double maximo;

    private final List<Double> datos;

    private final int maxDatos = 100;

    public GraficoTiempo(
            String titulo,
            double minimo,
            double maximo) {

        this.titulo = titulo;
        this.minimo = minimo;
        this.maximo = maximo;

        datos = new ArrayList<>();

        actualizarEstilo();
        TemaOscuro.alCambiar(this::actualizarEstilo);
    }

    public void agregarDato(double valor) {

        datos.add(valor);

        if (datos.size() > maxDatos) {

            datos.remove(0);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();

        int margenIzquierdo = 60;
        int margenDerecho = 20;
        int margenSuperior = 35;
        int margenInferior = 30;

        int xInicio =
                margenIzquierdo;

        int xFin =
                ancho - margenDerecho;

        int yInicio =
                margenSuperior;

        int yFin =
                alto - margenInferior;

        // ==============================
        // TÍTULO
        // ==============================

        g2.setColor(TemaOscuro.texto());

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        g2.drawString(
                titulo,
                15,
                20
        );

        // ==============================
        // EJES
        // ==============================

        g2.drawLine(
                xInicio,
                yInicio,
                xInicio,
                yFin
        );

        g2.drawLine(
                xInicio,
                yFin,
                xFin,
                yFin
        );

        // ==============================
        // VALORES
        // ==============================

        g2.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );

        g2.drawString(
                String.format("%.0f", maximo),
                5,
                yInicio + 5
        );

        g2.drawString(
                String.format("%.0f", minimo),
                5,
                yFin
        );

        // ==============================
        // DATOS
        // ==============================

        if (datos.size() < 2) {
            return;
        }

        double anchoGrafico =
                xFin - xInicio;

        double altoGrafico =
                yFin - yInicio;

        for (int i = 1;
             i < datos.size();
             i++) {

            double valorAnterior =
                    datos.get(i - 1);

            double valorActual =
                    datos.get(i);

            double x1 =
                    xInicio
                            + ((double) (i - 1)
                            / (maxDatos - 1))
                            * anchoGrafico;

            double x2 =
                    xInicio
                            + ((double) i
                            / (maxDatos - 1))
                            * anchoGrafico;

            double y1 =
                    yFin
                            - ((valorAnterior - minimo)
                            / (maximo - minimo))
                            * altoGrafico;

            double y2 =
                    yFin
                            - ((valorActual - minimo)
                            / (maximo - minimo))
                            * altoGrafico;

            g2.drawLine(
                    (int) x1,
                    (int) y1,
                    (int) x2,
                    (int) y2
            );
        }

        // ==============================
        // EJE X
        // ==============================

        g2.drawString(
                "t",
                xFin - 5,
                yFin + 20
        );
    }

    private void actualizarEstilo() {
        setBackground(TemaOscuro.superficieSecundaria());
        setBorder(BorderFactory.createLineBorder(TemaOscuro.borde()));
        repaint();
    }
}
