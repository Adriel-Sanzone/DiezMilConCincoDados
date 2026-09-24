package vista;

import controlador.Controlador;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import observer.Evento;
import observer.Observador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VistaConsola implements Observador {

    private enum ModoEntrada {
        ESPERANDO_OPCION,
        ESPERANDO_SELECCION_VALORES,
        ESPERANDO_NOMBRE_PARTIDA_GUARDAR
    }

    private final Controlador controlador;
    private final TextArea textArea;
    private final TextField campoEntrada;
    private final BorderPane root;
    private ModoEntrada modo;

    public VistaConsola(Controlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo.");
        }
        this.controlador = controlador;
        this.modo = ModoEntrada.ESPERANDO_OPCION;

        this.textArea = construirTextArea();
        this.campoEntrada = construirCampoEntrada();

        this.root = new BorderPane();
        this.root.setCenter(textArea);
        this.root.setBottom(construirBarraEntrada());

        imprimir("=== Consola Diez Mil con cinco dados ===");
        imprimir("Estas jugando como: " + controlador.getMiJugador());
        imprimir("Ingresa el número de la opcion.");
        imprimirMenu();
    }

    private TextArea construirTextArea() {
        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12pt;");
        return ta;
    }

    private TextField construirCampoEntrada() {
        TextField tf = new TextField();
        tf.setPromptText("Escribi tu opcion");
        tf.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12pt;");
        tf.setOnAction(e -> procesarEntrada());
        HBox.setHgrow(tf, Priority.ALWAYS);
        return tf;
    }

    private HBox construirBarraEntrada() {
        Label prompt = new Label(">");
        prompt.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 14pt; -fx-font-weight: bold;");
        HBox barra = new HBox(8, prompt, campoEntrada);
        barra.setPadding(new Insets(8));
        return barra;
    }

    public Parent getRoot() {
        return root;
    }

    @Override
    public void actualizar(Evento evento) {
        Platform.runLater(() -> {
            switch (evento) {
                case JUGADOR_AGREGADO -> manejarJugadorAgregado();
                case PARTIDA_INICIADA -> manejarPartidaIniciada();
                case DADOS_TIRADOS -> manejarDadosTirados();
                case DADOS_SELECCIONADOS -> manejarDadosSeleccionados();
                case TURNO_PERDIDO -> manejarTurnoPerdido();
                case JUGADOR_PLANTADO -> manejarJugadorPlantado();
                case CAMBIO_TURNO -> manejarCambioTurno();
                case PARTIDA_FINALIZADA -> manejarPartidaFinalizada();
            }
            resetearModoSiCorresponde();
            if (modo == ModoEntrada.ESPERANDO_OPCION) {
                imprimirMenu();
            }
        });
    }

    private void resetearModoSiCorresponde() {
        if (modo == ModoEntrada.ESPERANDO_NOMBRE_PARTIDA_GUARDAR && !controlador.estaEnCurso()) {
            modo = ModoEntrada.ESPERANDO_OPCION;
        }
        if (modo == ModoEntrada.ESPERANDO_SELECCION_VALORES
                && (!controlador.esFasePostTirada() || !controlador.esMiTurno())) {
            modo = ModoEntrada.ESPERANDO_OPCION;
        }
    }

    private void procesarEntrada() {
        String entrada = campoEntrada.getText();
        if (entrada == null) {
            return;
        }
        entrada = entrada.trim();
        campoEntrada.clear();
        if (entrada.isEmpty()) {
            return;
        }
        imprimir("> " + entrada);

        switch (modo) {
            case ESPERANDO_SELECCION_VALORES -> procesarSeleccionIngresada(entrada);
            case ESPERANDO_NOMBRE_PARTIDA_GUARDAR -> procesarNombreParaGuardar(entrada);
            default -> procesarOpcionIngresada(entrada);
        }
    }

    private void procesarOpcionIngresada(String opcion) {
        if (controlador.estaEnConfiguracion()) {
            procesarOpcionConfiguracion(opcion);
        } else if (controlador.estaEnCurso()) {
            procesarOpcionEnCurso(opcion);
        } else {
            imprimir("La partida termino.");
        }
    }

    private void procesarOpcionConfiguracion(String opcion) {
        if (opcion.equals("1")) {
            intentarIniciarPartida();
        } else {
            imprimir("Opcion invalida. La unica opcion es 1.");
            imprimirMenu();
        }
    }

    private void procesarOpcionEnCurso(String opcion) {
        if (opcion.equals("3")) {
            iniciarFlujoGuardarPartida();
            return;
        }
        if (!controlador.esMiTurno()) {
            imprimir("No es tu turno: esta jugando " + controlador.getNombreJugadorActual() + ".");
            imprimirMenu();
            return;
        }
        if (controlador.esFaseInicial()) {
            procesarOpcionFaseInicial(opcion);
        } else if (controlador.esFasePostTirada()) {
            procesarOpcionFasePostTirada(opcion);
        } else if (controlador.esFasePostSeleccion()) {
            procesarOpcionFasePostSeleccion(opcion);
        }
    }

    private void procesarOpcionFaseInicial(String opcion) {
        if (opcion.equals("1")) {
            intentarTirarDados();
        } else {
            imprimir("Opcion invalida.");
            imprimirMenu();
        }
    }

    private void procesarOpcionFasePostTirada(String opcion) {
        if (!opcion.equals("1")) {
            imprimir("Opcion invalida.");
            imprimirMenu();
            return;
        }
        if (controlador.hayCombinacionesPosibles()) {
            imprimir("Escribi los valores de los dados a reservar separados por espacio para hacer tu seleccion:");
            modo = ModoEntrada.ESPERANDO_SELECCION_VALORES;
        } else {
            intentarPasarTurno();
        }
    }

    private void procesarOpcionFasePostSeleccion(String opcion) {
        switch (opcion) {
            case "1" -> intentarTirarDados();
            case "2" -> intentarPlantarse();
            default -> {
                imprimir("Opcion invalida.");
                imprimirMenu();
            }
        }
    }

    private void procesarSeleccionIngresada(String entrada) {
        List<Integer> valores = parsearValores(entrada);
        if (valores == null) {
            modo = ModoEntrada.ESPERANDO_OPCION;
            imprimirMenu();
            return;
        }
        List<Integer> indices;
        try {
            indices = convertirValoresAIndices(valores);
        } catch (IllegalArgumentException ex) {
            imprimir("ERROR: " + ex.getMessage());
            modo = ModoEntrada.ESPERANDO_OPCION;
            imprimirMenu();
            return;
        }
        try {
            controlador.seleccionarDados(indices);
            modo = ModoEntrada.ESPERANDO_OPCION;
        } catch (RuntimeException ex) {
            imprimir("ERROR: " + ex.getMessage());
            modo = ModoEntrada.ESPERANDO_OPCION;
            imprimirMenu();
        }
    }

    private void iniciarFlujoGuardarPartida() {
        imprimir("Escribi el nombre para guardar la partida:");
        modo = ModoEntrada.ESPERANDO_NOMBRE_PARTIDA_GUARDAR;
    }

    private void procesarNombreParaGuardar(String nombre) {
        try {
            controlador.guardarPartida(nombre);
            imprimir("Partida guardada como \"" + nombre + "\" en el servidor.");
        } catch (Exception ex) {
            imprimir("ERROR al guardar: " + ex.getMessage());
        }
        modo = ModoEntrada.ESPERANDO_OPCION;
        imprimirMenu();
    }

    private List<Integer> parsearValores(String entrada) {
        String[] tokens = entrada.split("\\s+");
        List<Integer> valores = new ArrayList<>();
        for (String token : tokens) {
            try {
                int v = Integer.parseInt(token);
                if (v < 1 || v > 6) {
                    imprimir("ERROR: '" + token + "' no es un valor de dado valido (1-6).");
                    return null;
                }
                valores.add(v);
            } catch (NumberFormatException ex) {
                imprimir("ERROR: '" + token + "' no es un numero valido.");
                return null;
            }
        }
        if (valores.isEmpty()) {
            imprimir("ERROR: Se debe ingresar al menos un valor.");
            return null;
        }
        return valores;
    }

    private List<Integer> convertirValoresAIndices(List<Integer> valoresPedidos) {
        List<Integer> valoresDados = controlador.getValoresDeLosDados();
        List<Boolean> reservas = controlador.getReservasDeLosDados();
        List<Integer> indices = new ArrayList<>();
        Set<Integer> indicesYaUsados = new HashSet<>();

        for (Integer valorPedido : valoresPedidos) {
            Integer indiceEncontrado = null;
            for (int i = 0; i < valoresDados.size(); i++) {
                if (reservas.get(i)) continue;
                if (indicesYaUsados.contains(i)) continue;
                if (valoresDados.get(i).equals(valorPedido)) {
                    indiceEncontrado = i;
                    break;
                }
            }
            if (indiceEncontrado == null) {
                throw new IllegalArgumentException(
                        "No hay un dado disponible con valor " + valorPedido + ".");
            }
            indices.add(indiceEncontrado);
            indicesYaUsados.add(indiceEncontrado);
        }
        return indices;
    }

    private void intentarIniciarPartida() {
        try {
            controlador.iniciarPartida();
        } catch (RuntimeException ex) {
            imprimir("ERROR: " + ex.getMessage());
            imprimirMenu();
        }
    }

    private void intentarTirarDados() {
        try {
            controlador.tirarDados();
        } catch (RuntimeException ex) {
            imprimir("ERROR: " + ex.getMessage());
            imprimirMenu();
        }
    }

    private void intentarPasarTurno() {
        try {
            controlador.pasarTurno();
        } catch (RuntimeException ex) {
            imprimir("ERROR: " + ex.getMessage());
            imprimirMenu();
        }
    }

    private void intentarPlantarse() {
        try {
            controlador.plantarse();
        } catch (RuntimeException ex) {
            imprimir("ERROR: " + ex.getMessage());
            imprimirMenu();
        }
    }

    private void imprimirMenu() {
        if (controlador.estaEnConfiguracion()) {
            imprimir("Opciones:  [1] Iniciar partida");
        } else if (controlador.estaEnCurso()) {
            imprimirMenuEnCurso();
        } else {
            imprimir("La partida termino.");
        }
    }

    private void imprimirMenuEnCurso() {
        if (!controlador.esMiTurno()) {
            imprimir("Esperando a " + controlador.getNombreJugadorActual()
                    + ".   Opciones:  [3] Guardar partida");
            return;
        }
        if (controlador.esFaseInicial()) {
            imprimir("Opciones:  [1] Tirar dados   [3] Guardar partida");
        } else if (controlador.esFasePostTirada()) {
            if (controlador.hayCombinacionesPosibles()) {
                imprimir("Opciones:  [1] Seleccionar dados   [3] Guardar partida");
            } else {
                imprimir("Opciones:  [1] Pasar turno   [3] Guardar partida   (no hay combinaciones posibles)");
            }
        } else if (controlador.esFasePostSeleccion()) {
            imprimir("Opciones:  [1] Tirar dados   [2] Plantarse   [3] Guardar partida");
        }
    }

    private void manejarJugadorAgregado() {
        List<String> nombres = controlador.getNombresJugadores();
        String ultimo = nombres.get(nombres.size() - 1);
        imprimir("[+] Jugador conectado: " + ultimo
                + " (cantidad: " + nombres.size() + ")");
    }

    private void manejarPartidaIniciada() {
        imprimir("");
        imprimir("============================");
        imprimir("   PARTIDA INICIADA");
        imprimir("============================");
        imprimir("Jugadores: " + nombresJugadoresCSV());
        imprimir("Comienza: " + controlador.getNombreJugadorActual());
        imprimir("");
    }

    private void manejarDadosTirados() {
        String nombre = controlador.getNombreJugadorActual();
        imprimir(nombre + " tira los dados.");
        mostrarEstadoCubilete();
        if (!controlador.hayCombinacionesPosibles()) {
            imprimir("  *** Sin combinaciones disponibles ***");
        }
    }

    private void manejarDadosSeleccionados() {
        if (!controlador.hayUltimoResultado()) {
            return;
        }
        String nombre = controlador.getNombreJugadorActual();
        imprimir(nombre + " selecciono: " + controlador.getValoresUltimoResultado()
                + "  (+" + controlador.getPuntosUltimoResultado() + " pts)");
        imprimir("  Acumulado en el turno: " + controlador.getPuntosAcumuladosTurno() + " pts");
        if (controlador.ultimoResultadoUsoTodosLosDados() && noQuedanDadosReservados()) {
            imprimir("  *** Todos los dados puntuaron: se liberan para volver a tirar los 5 ***");
        }
    }

    private boolean noQuedanDadosReservados() {
        for (Boolean reservado : controlador.getReservasDeLosDados()) {
            if (reservado) return false;
        }
        return true;
    }

    private void manejarTurnoPerdido() {
        String nombre = controlador.getNombreJugadorActual();
        imprimir(">>> " + nombre + " PERDIO EL TURNO (no sumo puntos).");
    }

    private void manejarJugadorPlantado() {
        imprimir(">>> " + controlador.getNombreJugadorActual() + " se planto. Puntaje total: "
                + controlador.getPuntajeJugadorActual() + " pts.");
    }

    private void manejarCambioTurno() {
        imprimir("");
        imprimir("--- Turno de " + controlador.getNombreJugadorActual()
                + " (total previo: " + controlador.getPuntajeJugadorActual() + " pts) ---");
    }

    private void manejarPartidaFinalizada() {
        imprimir("");
        imprimir("##############################");
        imprimir("   PARTIDA FINALIZADA");
        imprimir("   Ganador: " + controlador.getNombreGanador()
                + " con " + controlador.getPuntajeGanador() + " pts");
        imprimir("##############################");
    }

    private void mostrarEstadoCubilete() {
        List<Integer> valores = controlador.getValoresDeLosDados();
        List<Boolean> reservas = controlador.getReservasDeLosDados();
        List<Integer> disponibles = new ArrayList<>();
        List<Integer> reservados = new ArrayList<>();
        for (int i = 0; i < valores.size(); i++) {
            if (reservas.get(i)) {
                reservados.add(valores.get(i));
            } else {
                disponibles.add(valores.get(i));
            }
        }
        imprimir("  Disponibles: " + disponibles + "   Reservados: " + reservados);
    }

    private String nombresJugadoresCSV() {
        StringBuilder sb = new StringBuilder();
        boolean primero = true;
        for (String nombre : controlador.getNombresJugadores()) {
            if (!primero) {
                sb.append(", ");
            }
            sb.append(nombre);
            primero = false;
        }
        return sb.toString();
    }

    private void imprimir(String mensaje) {
        textArea.appendText(mensaje + System.lineSeparator());
    }
}
