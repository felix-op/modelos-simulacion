package limnigrafos.simulacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import limnigrafos.modelos.Medicion;

public class Simulador {
    private static final int MEDICIONES_POR_BATERIA_COMPLETA = 5000;
    private static final double VOLTAJE_REFERENCIA = 12.4;

    @FunctionalInterface
    public interface Callback<T> {
        void ejecutar(T valor) throws Exception;
    }

    private final ModeloFisico modeloFisico;
    private final GeneradorSimulacion generador;
    private final Sensor sensor;
    private final ScheduledExecutorService ejecutor;
    private final List<Medicion> medicionesPendientes;
    private final double consumoBateriaPorMedicion;

    private volatile boolean nivelAutomatico = true;
    private volatile boolean temperaturaAutomatica = true;
    private volatile double nivelManual = 100.0;
    private volatile double temperaturaManual = 20.0;
    private volatile double bateria;

    private long numeroMuestra;
    private Callback<ResultadoSimulacion> alActualizarInterfaz;
    private Callback<Medicion> alGuardarMedicion;
    private Callback<List<Medicion>> alEnviarMediciones;
    private Callback<Double> alActualizarBateria;

    public Simulador(
            ModeloFisico modeloFisico,
            GeneradorSimulacion generador,
            Sensor sensor,
            double bateriaInicial) {
        this(
                modeloFisico,
                generador,
                sensor,
                bateriaInicial,
                MEDICIONES_POR_BATERIA_COMPLETA);
    }

    Simulador(
            ModeloFisico modeloFisico,
            GeneradorSimulacion generador,
            Sensor sensor,
            double bateriaInicial,
            int medicionesPorBateriaCompleta) {
        if (medicionesPorBateriaCompleta <= 0) {
            throw new IllegalArgumentException("La capacidad de la bateria debe ser mayor que cero");
        }

        this.modeloFisico = Objects.requireNonNull(modeloFisico);
        this.generador = Objects.requireNonNull(generador);
        this.sensor = Objects.requireNonNull(sensor);
        this.bateria = bateriaInicial;
        double bateriaCompleta = bateriaInicial > 0 ? bateriaInicial : VOLTAJE_REFERENCIA;
        this.consumoBateriaPorMedicion =
                bateriaCompleta / medicionesPorBateriaCompleta;
        this.medicionesPendientes = new ArrayList<>();
        this.ejecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread hilo = new Thread(runnable, "simulador-limnigrafo");
            hilo.setDaemon(true);
            return hilo;
        });
    }

    public void iniciar(
            int intervaloLecturaSegundos,
            int intervaloEnvioSegundos,
            Callback<ResultadoSimulacion> alActualizarInterfaz,
            Callback<Medicion> alGuardarMedicion,
            Callback<List<Medicion>> alEnviarMediciones,
            Callback<Double> alActualizarBateria) {
        if (intervaloLecturaSegundos <= 0 || intervaloEnvioSegundos <= 0) {
            throw new IllegalArgumentException("Los intervalos deben ser mayores que cero");
        }

        this.alActualizarInterfaz = Objects.requireNonNull(alActualizarInterfaz);
        this.alGuardarMedicion = Objects.requireNonNull(alGuardarMedicion);
        this.alEnviarMediciones = Objects.requireNonNull(alEnviarMediciones);
        this.alActualizarBateria = Objects.requireNonNull(alActualizarBateria);

        ejecutor.scheduleAtFixedRate(
                this::ejecutarMedicionSegura,
                0,
                intervaloLecturaSegundos,
                TimeUnit.SECONDS);

        ejecutor.scheduleAtFixedRate(
                this::enviarPendientesSeguro,
                intervaloEnvioSegundos,
                intervaloEnvioSegundos,
                TimeUnit.SECONDS);
    }

    public void detener() {
        ejecutor.shutdownNow();
    }

    public void setNivelAutomatico(boolean nivelAutomatico) {
        this.nivelAutomatico = nivelAutomatico;
    }

    public void setTemperaturaAutomatica(boolean temperaturaAutomatica) {
        this.temperaturaAutomatica = temperaturaAutomatica;
    }

    public void setNivelManual(double nivelManual) {
        this.nivelManual = nivelManual;
    }

    public void setTemperaturaManual(double temperaturaManual) {
        this.temperaturaManual = temperaturaManual;
    }

    public void setBateria(double bateria) {
        this.bateria = Math.max(0.0, bateria);
    }

    private void ejecutarMedicionSegura() {
        try {
            if (bateria <= 0.0) {
                ejecutarCallback(alActualizarBateria, 0.0, "actualizar la bateria");
                return;
            }

            ResultadoSimulacion resultado = generarMedicion();
            Medicion medicion = resultado.getMedicion();

            ejecutarCallback(alActualizarInterfaz, resultado, "actualizar la interfaz");
            boolean guardada = ejecutarCallback(
                    alGuardarMedicion,
                    medicion,
                    "guardar la medicion");

            if (guardada) {
                medicionesPendientes.add(medicion);
                bateria = bateria <= consumoBateriaPorMedicion
                        ? 0.0
                        : bateria - consumoBateriaPorMedicion;
                ejecutarCallback(alActualizarBateria, bateria, "actualizar la bateria");
            }
        } catch (RuntimeException exception) {
            System.err.println("No se pudo generar la medicion: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private ResultadoSimulacion generarMedicion() {
        int tiempo = Math.toIntExact(Math.min(numeroMuestra, Integer.MAX_VALUE));
        double nivelReal = nivelAutomatico
                ? generador.generarNivel(tiempo)
                : nivelManual;
        double temperaturaReal = temperaturaAutomatica
                ? generador.generarTemperatura(tiempo)
                : temperaturaManual;

        double densidadReal = modeloFisico.calcularDensidad(temperaturaReal);
        double presionReal = modeloFisico.calcularPresion(nivelReal, densidadReal);
        double temperaturaMedida = sensor.medirTemperatura(temperaturaReal);
        double presionMedida = sensor.medirPresion(presionReal);
        double densidadMedida = modeloFisico.calcularDensidad(temperaturaMedida);
        double nivelMedido = modeloFisico.calcularNivel(presionMedida, densidadMedida);

        Medicion medicion = new Medicion(
                temperaturaMedida,
                presionMedida,
                nivelMedido,
                bateria,
                LocalDateTime.now());

        ResultadoSimulacion resultado = new ResultadoSimulacion(
                numeroMuestra,
                nivelReal,
                temperaturaReal,
                densidadReal,
                presionReal,
                densidadMedida,
                medicion);
        numeroMuestra++;
        return resultado;
    }

    private void enviarPendientesSeguro() {
        if (medicionesPendientes.isEmpty()) {
            return;
        }

        List<Medicion> lote = new ArrayList<>(medicionesPendientes);
        try {
            alEnviarMediciones.ejecutar(lote);
            medicionesPendientes.subList(0, lote.size()).clear();
        } catch (Exception exception) {
            System.err.println("No se pudieron enviar las mediciones: " + exception.getMessage());
        }
    }

    private <T> boolean ejecutarCallback(Callback<T> callback, T valor, String operacion) {
        try {
            callback.ejecutar(valor);
            return true;
        } catch (Exception exception) {
            System.err.println("No se pudo " + operacion + ": " + exception.getMessage());
            return false;
        }
    }
}
