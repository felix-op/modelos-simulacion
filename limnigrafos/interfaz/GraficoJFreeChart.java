package limnigrafos.interfaz;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class GraficoJFreeChart extends JPanel {
    private static final int MAXIMO_DATOS = 100;
    private static final Color[] COLORES = {
            new Color(72, 190, 230),
            new Color(255, 176, 72),
            new Color(89, 209, 142),
            new Color(190, 135, 255)
    };

    private final Map<String, TimeSeries> series;

    public GraficoJFreeChart(
            String titulo,
            String unidad,
            double minimo,
            double maximo,
            String... nombresSeries) {
        if (nombresSeries.length == 0) {
            throw new IllegalArgumentException("El grafico debe tener al menos una serie");
        }

        setLayout(new BorderLayout());
        setBackground(TemaOscuro.SUPERFICIE);
        series = new LinkedHashMap<>();
        TimeSeriesCollection conjuntoDatos = new TimeSeriesCollection();

        for (String nombre : nombresSeries) {
            TimeSeries serie = new TimeSeries(nombre);
            serie.setMaximumItemCount(MAXIMO_DATOS);
            series.put(nombre, serie);
            conjuntoDatos.addSeries(serie);
        }

        JFreeChart grafico = ChartFactory.createTimeSeriesChart(
                titulo,
                "Hora",
                unidad,
                conjuntoDatos,
                true,
                true,
                false);

        configurarApariencia(grafico, minimo, maximo);

        ChartPanel panelGrafico = new ChartPanel(grafico);
        panelGrafico.setBackground(TemaOscuro.SUPERFICIE);
        panelGrafico.setMouseWheelEnabled(true);
        panelGrafico.setDomainZoomable(true);
        panelGrafico.setRangeZoomable(true);
        add(panelGrafico, BorderLayout.CENTER);
    }

    public void agregarDato(String nombreSerie, LocalDateTime fechaHora, double valor) {
        agregarValor(nombreSerie, fechaHora, valor);
    }

    public void agregarCorte(String nombreSerie, LocalDateTime fechaHora) {
        agregarValor(nombreSerie, fechaHora, null);
    }

    private void agregarValor(String nombreSerie, LocalDateTime fechaHora, Number valor) {
        TimeSeries serie = series.get(nombreSerie);
        if (serie == null) {
            throw new IllegalArgumentException("No existe la serie: " + nombreSerie);
        }

        Runnable actualizacion = () -> {
            Date fecha = Date.from(fechaHora.atZone(ZoneId.systemDefault()).toInstant());
            serie.addOrUpdate(new Millisecond(fecha), valor);
        };

        if (SwingUtilities.isEventDispatchThread()) {
            actualizacion.run();
        } else {
            SwingUtilities.invokeLater(actualizacion);
        }
    }

    private void configurarApariencia(JFreeChart grafico, double minimo, double maximo) {
        grafico.setBackgroundPaint(TemaOscuro.SUPERFICIE);
        grafico.getTitle().setPaint(TemaOscuro.TEXTO);
        if (grafico.getLegend() != null) {
            grafico.getLegend().setBackgroundPaint(TemaOscuro.SUPERFICIE);
            grafico.getLegend().setItemPaint(TemaOscuro.TEXTO);
            grafico.getLegend().setFrame(BlockBorder.NONE);
        }

        XYPlot area = grafico.getXYPlot();
        area.setBackgroundPaint(TemaOscuro.SUPERFICIE_SECUNDARIA);
        area.setDomainGridlinePaint(TemaOscuro.BORDE);
        area.setRangeGridlinePaint(TemaOscuro.BORDE);
        area.setOutlinePaint(TemaOscuro.BORDE);
        area.getRangeAxis().setRange(minimo, maximo);
        area.getRangeAxis().setLabelPaint(TemaOscuro.TEXTO_SECUNDARIO);
        area.getRangeAxis().setTickLabelPaint(TemaOscuro.TEXTO_SECUNDARIO);

        DateAxis ejeTiempo = (DateAxis) area.getDomainAxis();
        ejeTiempo.setDateFormatOverride(new java.text.SimpleDateFormat("HH:mm:ss"));
        ejeTiempo.setAutoRange(true);
        ejeTiempo.setLabelPaint(TemaOscuro.TEXTO_SECUNDARIO);
        ejeTiempo.setTickLabelPaint(TemaOscuro.TEXTO_SECUNDARIO);

        XYItemRenderer renderer = area.getRenderer();
        if (renderer instanceof XYLineAndShapeRenderer rendererLineas) {
            rendererLineas.setDefaultShapesVisible(true);
            rendererLineas.setDefaultShapesFilled(true);
        }

        int indice = 0;
        for (String nombre : series.keySet()) {
            renderer.setSeriesPaint(indice, COLORES[indice % COLORES.length]);
            renderer.setSeriesShape(indice, new Ellipse2D.Double(-2.5, -2.5, 5.0, 5.0));
            renderer.setSeriesStroke(indice, indice == 0
                    ? new BasicStroke(
                            1.8f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            10.0f,
                            new float[] { 6.0f, 4.0f },
                            0.0f)
                    : new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            indice++;
        }
    }
}
