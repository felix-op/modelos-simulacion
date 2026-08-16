package limnigrafos;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import limnigrafos.interfaz.TemaOscuro;
import limnigrafos.interfaz.VentanaPrincipal;
import limnigrafos.modelos.Limnigrafo;
import limnigrafos.servicios.ClienteApiScarh;
import limnigrafos.servicios.RepositorioConfiguracionesLimnigrafos;
import limnigrafos.servicios.RepositorioMedicionesCsv;
import limnigrafos.simulacion.GeneradorSimulacion;
import limnigrafos.simulacion.ModeloFisico;
import limnigrafos.simulacion.Sensor;
import limnigrafos.simulacion.Simulador;

public class Main {
    private static final String CODIGO_LIMNIGRAFO = "0xA10F2C";

    public static void main(String[] args) {
        TemaOscuro.aplicar();

        try {
            RepositorioConfiguracionesLimnigrafos configuraciones =
                    new RepositorioConfiguracionesLimnigrafos();
            Limnigrafo limnigrafo = configuraciones.leer(CODIGO_LIMNIGRAFO)
                    .orElseThrow(() -> new IllegalStateException(
                            "No existe la configuracion del limnigrafo " + CODIGO_LIMNIGRAFO));

            SwingUtilities.invokeLater(() -> iniciarInterfaz(limnigrafo));
        } catch (IOException | RuntimeException exception) {
            mostrarErrorInicio(exception);
        }
    }

    private static void iniciarInterfaz(Limnigrafo limnigrafo) {
        RepositorioMedicionesCsv repositorioMediciones = new RepositorioMedicionesCsv();
        ClienteApiScarh clienteApi = new ClienteApiScarh();
        Simulador simulador = new Simulador(
                new ModeloFisico(),
                new GeneradorSimulacion(),
                new Sensor(50.0, 0.1),
                limnigrafo.getBateria());

        VentanaPrincipal ventana = new VentanaPrincipal(limnigrafo);
        ventana.configurarControles(
                simulador::setNivelAutomatico,
                simulador::setNivelManual,
                simulador::setTemperaturaAutomatica,
                simulador::setTemperaturaManual,
                simulador::setBateria);
        ventana.alCerrar(simulador::detener);
        ventana.mostrar();

        simulador.iniciar(
                limnigrafo.getTiempoRecoleccionSegundos(),
                limnigrafo.getTiempoEnvioSegundos(),
                resultado -> SwingUtilities.invokeLater(() -> ventana.actualizar(resultado)),
                medicion -> repositorioMediciones.escribir(limnigrafo.getCodigo(), medicion),
                mediciones -> clienteApi.enviar(limnigrafo, mediciones),
                bateria -> SwingUtilities.invokeLater(() -> {
                    ventana.actualizarBateria(bateria);
                    if (bateria <= 0.0) {
                        ventana.marcarCorte(LocalDateTime.now());
                    }
                }));
    }

    private static void mostrarErrorInicio(Exception exception) {
        System.err.println("No se pudo iniciar el simulador: " + exception.getMessage());
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                exception.getMessage(),
                "Error al iniciar",
                JOptionPane.ERROR_MESSAGE));
    }
}
