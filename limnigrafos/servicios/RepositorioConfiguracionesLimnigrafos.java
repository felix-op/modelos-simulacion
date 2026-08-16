package limnigrafos.servicios;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import limnigrafos.modelos.Limnigrafo;

public class RepositorioConfiguracionesLimnigrafos {
    private final Path directorioConfiguraciones;

    public RepositorioConfiguracionesLimnigrafos() {
        this(Paths.get("limnigrafos", "configuraciones"));
    }

    public RepositorioConfiguracionesLimnigrafos(Path directorioConfiguraciones) {
        this.directorioConfiguraciones = Objects.requireNonNull(directorioConfiguraciones);
    }

    public void escribir(Limnigrafo limnigrafo) throws IOException {
        Objects.requireNonNull(limnigrafo, "El limnigrafo no puede ser nulo");
        validarCodigo(limnigrafo.getCodigo());
        Files.createDirectories(directorioConfiguraciones);

        Path archivo = obtenerArchivo(limnigrafo.getCodigo());
        try (BufferedWriter writer = Files.newBufferedWriter(
                archivo,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            escribirPropiedad(writer, "codigo", limnigrafo.getCodigo());
            escribirPropiedad(writer, "ubicacion", limnigrafo.getUbicacion());
            escribirPropiedad(writer, "tiempoRecoleccionSegundos",
                    Integer.toString(limnigrafo.getTiempoRecoleccionSegundos()));
            escribirPropiedad(writer, "tiempoEnvioSegundos",
                    Integer.toString(limnigrafo.getTiempoEnvioSegundos()));
            escribirPropiedad(writer, "bateria", Double.toString(limnigrafo.getBateria()));
        }
    }

    public Optional<Limnigrafo> leer(String codigoLimnigrafo) throws IOException {
        Path archivo = obtenerArchivo(codigoLimnigrafo);

        if (Files.notExists(archivo)) {
            return Optional.empty();
        }

        return Optional.of(leerArchivo(archivo));
    }

    public List<Limnigrafo> leerTodos() throws IOException {
        List<Limnigrafo> limnigrafos = new ArrayList<>();

        if (Files.notExists(directorioConfiguraciones)) {
            return limnigrafos;
        }

        try (DirectoryStream<Path> archivos =
                Files.newDirectoryStream(directorioConfiguraciones, "*.properties")) {
            for (Path archivo : archivos) {
                limnigrafos.add(leerArchivo(archivo));
            }
        }

        limnigrafos.sort(Comparator.comparing(Limnigrafo::getCodigo));
        return limnigrafos;
    }

    private Limnigrafo leerArchivo(Path archivo) throws IOException {
        Properties propiedades = new Properties();

        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            propiedades.load(reader);
        }

        try {
            String codigo = propiedadObligatoria(propiedades, "codigo", archivo);
            String ubicacion = propiedadObligatoria(propiedades, "ubicacion", archivo);
            int tiempoRecoleccion = Integer.parseInt(
                    propiedadObligatoria(propiedades, "tiempoRecoleccionSegundos", archivo));
            int tiempoEnvio = Integer.parseInt(
                    propiedadObligatoria(propiedades, "tiempoEnvioSegundos", archivo));
            double bateria = Double.parseDouble(
                    propiedadObligatoria(propiedades, "bateria", archivo));

            validarCodigo(codigo);
            return new Limnigrafo(codigo, ubicacion, tiempoRecoleccion, tiempoEnvio, bateria);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Configuracion invalida en " + archivo, exception);
        }
    }

    private String propiedadObligatoria(Properties propiedades, String clave, Path archivo)
            throws IOException {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new IOException("Falta la propiedad '" + clave + "' en " + archivo);
        }
        return valor;
    }

    private void escribirPropiedad(BufferedWriter writer, String clave, String valor)
            throws IOException {
        writer.write(clave);
        writer.write('=');
        writer.write(escaparValor(valor == null ? "" : valor));
        writer.newLine();
    }

    private String escaparValor(String valor) {
        return valor
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("=", "\\=")
                .replace(":", "\\:");
    }

    private Path obtenerArchivo(String codigoLimnigrafo) {
        validarCodigo(codigoLimnigrafo);
        return directorioConfiguraciones.resolve(codigoLimnigrafo + ".properties");
    }

    private void validarCodigo(String codigoLimnigrafo) {
        if (codigoLimnigrafo == null || !codigoLimnigrafo.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "El codigo del limnigrafo solo puede contener letras, numeros, guiones y guiones bajos");
        }
    }
}
