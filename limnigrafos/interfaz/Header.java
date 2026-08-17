package limnigrafos.interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import limnigrafos.modelos.Limnigrafo;
import limnigrafos.modelos.Medicion;
import limnigrafos.simulacion.ResultadoSimulacion;

public class Header extends JPanel {
    private final Typography nivel;
    private final Typography temperatura;
    private final Typography presion;
    private final Typography densidad;
    private final Typography bateria;

    public Header(Limnigrafo limnigrafo) {
        setLayout(new BorderLayout(15, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        Typography titulo = new Typography(
                "Simulador de Limnígrafo",
                Typography.Variante.TITULO);

        Typography configuracion = new Typography(
                String.format(
                        "%s · %s · lectura: %d s · envío: %d s",
                        limnigrafo.getCodigo(),
                        limnigrafo.getUbicacion(),
                        limnigrafo.getTiempoRecoleccionSegundos(),
                        limnigrafo.getTiempoEnvioSegundos()),
                Typography.Variante.SECUNDARIO);

        JPanel identificacion = new JPanel(new GridLayout(2, 1));
        identificacion.add(titulo);
        identificacion.add(configuracion);

        BotonCambiarTema botonCambiarTema = new BotonCambiarTema();
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.add(botonCambiarTema);

        nivel = new Typography("Nivel: -- cm", Typography.Variante.DATO);
        temperatura = new Typography("Temperatura: -- °C", Typography.Variante.DATO);
        presion = new Typography("Presión: -- hPa", Typography.Variante.DATO);
        densidad = new Typography("Densidad: -- kg/m³", Typography.Variante.DATO);
        bateria = new Typography(
                String.format("Batería: %.2f V", limnigrafo.getBateria()),
                Typography.Variante.DATO);

        JPanel datos = new JPanel(new GridLayout(1, 5, 16, 0));
        datos.add(nivel);
        datos.add(temperatura);
        datos.add(presion);
        datos.add(densidad);
        datos.add(bateria);

        add(identificacion, BorderLayout.NORTH);
        add(datos, BorderLayout.CENTER);
        add(acciones, BorderLayout.EAST);
    }

    public void actualizar(ResultadoSimulacion resultado) {
        Medicion medicion = resultado.getMedicion();
        nivel.setTexto(
                "Nivel: %.2f cm (real %.2f)",
                medicion.getNivelAgua(),
                resultado.getNivelReal());
        temperatura.setTexto("Temperatura: %.2f °C", medicion.getTemperatura());
        presion.setTexto("Presión: %.2f hPa", medicion.getPresion() / 100.0);
        densidad.setTexto("Densidad: %.2f kg/m³", resultado.getDensidadMedida());
        bateria.setTexto("Batería: %.2f V", medicion.getBateria());
    }

    public void actualizarBateria(double voltaje) {
        bateria.setTexto("Batería: %.2f V", voltaje);
    }
}
