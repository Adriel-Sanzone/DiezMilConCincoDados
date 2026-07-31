package persistencia;

import modelo.Partida;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class GestorPartida {

    public static final String CARPETA_PARTIDAS = "partidas";
    public static final String EXTENSION = ".dat";

    private GestorPartida() {
    }

    public static void guardar(Partida partida, String nombreArchivo) throws IOException {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }
        validarNombreArchivo(nombreArchivo);
        asegurarCarpeta();
        Path ruta = rutaArchivo(nombreArchivo);
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(ruta.toFile()))) {
            out.writeObject(partida);
        }
    }

    public static Partida cargar(String nombreArchivo) throws IOException, ClassNotFoundException {
        validarNombreArchivo(nombreArchivo);
        Path ruta = rutaArchivo(nombreArchivo);
        if (!Files.exists(ruta)) {
            throw new FileNotFoundException("No existe la partida: " + nombreArchivo);
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(ruta.toFile()))) {
            Object obj = in.readObject();
            if (!(obj instanceof Partida)) {
                throw new IOException("El archivo no contiene una partida válida.");
            }
            return (Partida) obj;
        }
    }

    public static List<String> listarPartidasGuardadas() {
        asegurarCarpeta();
        List<String> nombres = new ArrayList<>();
        try (Stream<Path> stream = Files.list(Paths.get(CARPETA_PARTIDAS))) {
            stream.filter(p -> p.toString().endsWith(EXTENSION))
                    .forEach(p -> nombres.add(nombreSinExtension(p.getFileName().toString())));
        } catch (IOException e) {
            return new ArrayList<>();
        }
        Collections.sort(nombres);
        return nombres;
    }

    private static void asegurarCarpeta() {
        try {
            Files.createDirectories(Paths.get(CARPETA_PARTIDAS));
        } catch (IOException ignored) {
        }
    }

    private static Path rutaArchivo(String nombre) {
        String nombreNormalizado = nombre.endsWith(EXTENSION) ? nombre : nombre + EXTENSION;
        return Paths.get(CARPETA_PARTIDAS, nombreNormalizado);
    }

    private static String nombreSinExtension(String nombreArchivo) {
        if (nombreArchivo.endsWith(EXTENSION)) {
            return nombreArchivo.substring(0, nombreArchivo.length() - EXTENSION.length());
        }
        return nombreArchivo;
    }

    private static void validarNombreArchivo(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío.");
        }
        if (nombre.contains("/") || nombre.contains("\\") || nombre.contains("..")) {
            throw new IllegalArgumentException(
                    "El nombre del archivo no puede contener separadores ni '..': " + nombre);
        }
    }
}
