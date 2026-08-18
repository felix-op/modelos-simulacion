package limnigrafos.simulacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import limnigrafos.modelos.Medicion;

// Orquesta el ciclo de vida completo de un limnígrafo simulado: dispara
// mediciones periódicas, las persiste, agrupa las pendientes para enviarlas
// por lote y descuenta batería en cada medición hasta que se agota.
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
        // Si arrancamos con la batería ya en 0 (o negativa) no hay una carga
        // "completa" real de la que partir para calcular el consumo por
        // medición; se usa el voltaje de referencia como base para que el
        // simulador siga funcionando en vez de dividir por una carga inválida.
        double bateriaCompleta = bateriaInicial > 0 ? bateriaInicial : VOLTAJE_REFERENCIA;
        this.consumoBateriaPorMedicion =
                bateriaCompleta / medicionesPorBateriaCompleta;
        this.medicionesPendientes = new ArrayList<>();
        this.ejecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread hilo = new Thread(runnable, "simulador-limnigrafo");
            // Hilo daemon: si la aplicación se cierra sin llamar a detener(),
            // no debe impedir que la JVM termine.
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

        // Dos tareas periódicas independientes en el mismo hilo único: la
        // lectura del sensor (cada intervaloLecturaSegundos, empieza ya) y el
        // envío por lote de lo acumulado (cada intervaloEnvioSegundos). Al no
        // solaparse, no hace falta sincronizar el acceso a medicionesPendientes.
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
            // Sin batería el dispositivo real no puede energizar el sensor:
            // se corta el ciclo de medición en vez de simular una lectura.
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

            // Solo se acumula para envío y se descuenta batería si la
            // medición efectivamente se pudo persistir; si no se guardó, se
            // reintentará en el próximo ciclo en vez de perderla o cobrar
            // batería por una medición que no quedó registrada.
            if (guardada) {
                medicionesPendientes.add(medicion);
                bateria = bateria <= consumoBateriaPorMedicion
                        ? 0.0
                        : bateria - consumoBateriaPorMedicion;
                ejecutarCallback(alActualizarBateria, bateria, "actualizar la bateria");
            }
        } catch (RuntimeException exception) {
            // Se ejecuta en un ScheduledExecutorService: si esta tarea propaga
            // una excepción, el scheduler la cancela silenciosamente y el
            // limnígrafo dejaría de medir para siempre. Se atrapa y loguea
            // para que el ciclo siga vivo en el próximo intervalo.
            System.err.println("No se pudo generar la medicion: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private ResultadoSimulacion generarMedicion() {
        // numeroMuestra ya viene acotado a un long no negativo creciente; el
        // clamp a Integer.MAX_VALUE es solo para que los generadores sinusoidales
        // (que reciben un int) no rebalsen en simulaciones extremadamente largas.
        int tiempo = Math.toIntExact(Math.min(numeroMuestra, Integer.MAX_VALUE));
        double nivelReal = nivelAutomatico
                ? generador.generarNivel(tiempo)
                : nivelManual;
        double temperaturaReal = temperaturaAutomatica
                ? generador.generarTemperatura(tiempo)
                : temperaturaManual;

        // "Real" = lo que efectivamente hay en el río (entorno físico).
        // "Medida" = lo que el sensor termina reportando, calculado
        // volviendo a pasar por el modelo físico pero con la temperatura y
        // presión ya contaminadas por el ruido del Sensor. Así el nivel final
        // reportado arrastra el mismo error de medición que tendría el
        // hardware real, en vez de ser el nivel real "perfecto".
        double densidadReal = modeloFisico.calcularDensidad(temperaturaReal);
        double presionReal = modeloFisico.calcularPresion(nivelReal, densidadReal, temperaturaReal);
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

        // Se copia el lote y solo se remueven esas mismas mediciones (por
        // cantidad, no vaciando la lista entera) porque entre el momento en
        // que se armó el lote y el envío puede haberse agregado una medición
        // nueva desde ejecutarMedicionSegura(); esa no debe perderse si el
        // envío falla o si ya quedó pendiente para el próximo lote.
        List<Medicion> lote = new ArrayList<>(medicionesPendientes);
        try {
            alEnviarMediciones.ejecutar(lote);
            medicionesPendientes.subList(0, lote.size()).clear();
        } catch (Exception exception) {
            // Si el envío falla, las mediciones quedan en medicionesPendientes
            // y se reintentan en el próximo ciclo de envío.
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

    public void setEstacion(limnigrafos.simulacion.entorno.Estacion estacion) {
        this.generador.setEstacion(estacion);
    }

    public void setClima(limnigrafos.simulacion.entorno.Clima clima) {
        this.generador.setClima(clima);
    }
}
