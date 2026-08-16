package limnigrafos.servicios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import limnigrafos.modelos.Medicion;

public class RepositorioMedicionesCsv {
    private static final String ENCABEZADO =
            "fechaHora,temperatura,presion,nivelAgua,bateria";

    private final Path directorioMediciones;

    public RepositorioMedicionesCsv() {
        this(Paths.get("limnigrafos", "db", "mediciones"));
    }

    public RepositorioMedicionesCsv(Path directorioMediciones) {
        this.directorioMediciones = Objects.requireNonNull(directorioMediciones);
    }

    public void escribir(String codigoLimnigrafo, Medicion medicion) throws IOException {
        Objects.requireNonNull(medicion, "La medicion no puede ser nula");
        Objects.requireNonNull(medicion.getFechaHora(), "La fecha de la medicion no puede ser nula");

        Files.createDirectories(directorioMediciones);
        Path archivo = obtenerArchivo(codigoLimnigrafo);
        boolean escribirEncabezado = Files.notExists(archivo) || Files.size(archivo) == 0;

        try (BufferedWriter writer = Files.newBufferedWriter(
                archivo,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            if (escribirEncabezado) {
                writer.write(ENCABEZADO);
                writer.newLine();
            }

            writer.write(convertirEnLinea(medicion));
            writer.newLine();
        }
    }

    public List<Medicion> leer(String codigoLimnigrafo) throws IOException {
        Path archivo = obtenerArchivo(codigoLimnigrafo);
        List<Medicion> mediciones = new ArrayList<>();

        if (Files.notExists(archivo)) {
            return mediciones;
        }

        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;

            while ((linea = reader.readLine()) != null) {
                numeroLinea++;

                if (numeroLinea == 1 && ENCABEZADO.equals(linea)) {
                    continue;
                }
                if (linea.isBlank()) {
                    continue;
                }

                mediciones.add(convertirEnMedicion(linea, archivo, numeroLinea));
            }
        }

        return mediciones;
    }

    private String convertirEnLinea(Medicion medicion) {
        return String.join(",",
                medicion.getFechaHora().toString(),
                Double.toString(medicion.getTemperatura()),
                Double.toString(medicion.getPresion()),
                Double.toString(medicion.getNivelAgua()),
                Double.toString(medicion.getBateria()));
    }

    private Medicion convertirEnMedicion(String linea, Path archivo, int numeroLinea)
            throws IOException {
        String[] campos = linea.split(",", -1);

        if (campos.length != 5) {
            throw new IOException("Formato CSV invalido en " + archivo + ", linea " + numeroLinea);
        }

        try {
            LocalDateTime fechaHora = LocalDateTime.parse(campos[0]);
            double temperatura = Double.parseDouble(campos[1]);
            double presion = Double.parseDouble(campos[2]);
            double nivelAgua = Double.parseDouble(campos[3]);
            double bateria = Double.parseDouble(campos[4]);

            return new Medicion(temperatura, presion, nivelAgua, bateria, fechaHora);
        } catch (NumberFormatException | DateTimeParseException exception) {
            throw new IOException(
                    "Dato invalido en " + archivo + ", linea " + numeroLinea,
                    exception);
        }
    }

    private Path obtenerArchivo(String codigoLimnigrafo) {
        validarCodigo(codigoLimnigrafo);
        return directorioMediciones.resolve(codigoLimnigrafo + "-mediciones.csv");
    }

    private void validarCodigo(String codigoLimnigrafo) {
        if (codigoLimnigrafo == null || !codigoLimnigrafo.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "El codigo del limnigrafo solo puede contener letras, numeros, guiones y guiones bajos");
        }
    }
}

