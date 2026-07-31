package vista;

import controlador.Controlador;
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
        ESPERANDO_NOMBRE_JUGADOR,
        ESPERANDO_SELECCION_VALORES,
        ESPERANDO_NOMBRE_PARTIDA_GUARDAR,
        ESPERANDO_INDICE_PARTIDA_CARGAR
    }

    private final Controlador controlador;
    private final TextArea textArea;
    private final TextField campoEntrada;
    private final BorderPane root;
    private ModoEntrada modo;
    private List<String> ultimaListaPartidas;

    public VistaConsola(Controlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo.");
        }
        this.controlador = controlador;
        this.modo = ModoEntrada.ESPERANDO_OPCION;
        this.ultimaListaPartidas = new ArrayList<>();

        this.textArea = construirTextArea();
        this.campoEntrada = construirCampoEntrada();

        this.root = new BorderPane();
        this.root.setCenter(textArea);
        this.root.setBottom(construirBarraEntrada());

        imprimir("=== Consola Diez Mil con cinco dados ===");
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
        switch (evento) {
            case JUGADOR_AGREGADO -> manejarJugadorAgregado();
            case PARTIDA_INICIADA -> manejarPartidaIniciada();
            case DADOS_TIRADOS -> manejarDadosTirados();
            case DADOS_SELECCIONADOS -> manejarDadosSeleccionados();
            case TURNO_PERDIDO -> manejarTurnoPerdido();
            case JUGADOR_PLANTADO -> manejarJugadorPlantado();
            case CAMBIO_TURNO -> manejarCambioTurno();
            case PARTIDA_FINALIZADA -> manejarPartidaFinalizada();
            case PARTIDA_CARGADA -> manejarPartidaCargada();
        }
        resetearModoSiCorresponde();
        if (modo == ModoEntrada.ESPERANDO_OPCION) {
            imprimirMenu();
        }
    }

    private void resetearModoSiCorresponde() {
        if (modo == ModoEntrada.ESPERANDO_NOMBRE_JUGADOR && !controlador.estaEnConfiguracion()) {
            modo = ModoEntrada.ESPERANDO_OPCION;
        }
        if (modo == ModoEntrada.ESPERANDO_INDICE_PARTIDA_CARGAR && !controlador.estaEnConfiguracion()) {
            modo = ModoEntrada.ESPERANDO_OPCION;
        }
        if (modo == ModoEntrada.ESPERANDO_NOMBRE_PARTIDA_GUARDAR && !controlador.estaEnCurso()) {
            modo = ModoEntrada.ESPERANDO_OPCION;
        }
        if (modo == ModoEntrada.ESPERANDO_SELECCION_VALORES && !controlador.esFasePostTirada()) {
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
            case ESPERANDO_NOMBRE_JUGADOR -> procesarNombreJugadorIngresado(entrada);
            case ESPERANDO_SELECCION_VALORES -> procesarSeleccionIngresada(entrada);
            case ESPERANDO_NOMBRE_PARTIDA_GUARDAR -> procesarNombreParaGuardar(entrada);
            case ESPERANDO_INDICE_PARTIDA_CARGAR -> procesarIndiceParaCargar(entrada);
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
        switch (opcion) {
            case "1" -> {
                imprimir("Ingresa el nombre del jugador:");
                modo = ModoEntrada.ESPERANDO_NOMBRE_JUGADOR;
            }
            case "2" -> intentarIniciarPartida();
            case "3" -> iniciarFlujoCargarPartida();
            default -> {
                imprimir("Opcion invalida. Las opciones son 1, 2 o 3.");
                imprimirMenu();
            }
        }
    }

    private void procesarOpcionEnCurso(String opcion) {
        if (opcion.equals("3")) {
            iniciarFlujoGuardarPartida();
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

    private void procesarNombreJugadorIngresado(String nombre) {
        try {
            controlador.agregarJugador(nombre);
        } catch (RuntimeException ex) {
            imprimir("ERROR: " + ex.getMessage());
        }
        modo = ModoEntrada.ESPERANDO_OPCION;
        imprimirMenu();
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
            imprimir("Partida guardada como \"" + nombre + "\".");
        } catch (Exception ex) {
            imprimir("ERROR al guardar: " + ex.getMessage());
        }
        modo = ModoEntrada.ESPERANDO_OPCION;
        imprimirMenu();
    }

    private void iniciarFlujoCargarPartida() {
        ultimaListaPartidas = new ArrayList<>(controlador.listarPartidasGuardadas());
        if (ultimaListaPartidas.isEmpty()) {
            imprimir("No hay partidas guardadas.");
            imprimirMenu();
            return;
        }
        imprimir("Partidas guardadas:");
        for (int i = 0; i < ultimaListaPartidas.size(); i++) {
            imprimir("  [" + (i + 1) + "] " + ultimaListaPartidas.get(i));
        }
        imprimir("Escribi el numero de la partida a cargar (0 para cancelar):");
        modo = ModoEntrada.ESPERANDO_INDICE_PARTIDA_CARGAR;
    }

    private void procesarIndiceParaCargar(String entrada) {
        try {
            int indice = Integer.parseInt(entrada);
            if (indice == 0) {
                imprimir("Carga cancelada.");
                modo = ModoEntrada.ESPERANDO_OPCION;
                imprimirMenu();
                return;
            }
            if (indice < 1 || indice > ultimaListaPartidas.size()) {
                imprimir("ERROR: numero fuera de rango.");
                modo = ModoEntrada.ESPERANDO_OPCION;
                imprimirMenu();
                return;
            }
            String nombre = ultimaListaPartidas.get(indice - 1);
            try {
                controlador.cargarPartida(nombre);
            } catch (Exception ex) {
                imprimir("ERROR al cargar: " + ex.getMessage());
                modo = ModoEntrada.ESPERANDO_OPCION;
                imprimirMenu();
            }
        } catch (NumberFormatException ex) {
            imprimir("ERROR: no es un numero valido.");
            modo = ModoEntrada.ESPERANDO_OPCION;
            imprimirMenu();
        }
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
            imprimir("Opciones:  [1] Agregar jugador   [2] Iniciar partida   [3] Cargar partida guardada");
        } else if (controlador.estaEnCurso()) {
            imprimirMenuEnCurso();
        } else {
            imprimir("La partida termino.");
        }
    }

    private void imprimirMenuEnCurso() {
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
        imprimir("[+] Jugador agregado: " + ultimo
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

    private void manejarPartidaCargada() {
        imprimir("");
        imprimir("============================");
        imprimir("   PARTIDA CARGADA");
        imprimir("============================");
        imprimir("Jugadores: " + nombresJugadoresCSV());
        if (controlador.estaEnCurso()) {
            imprimir("Turno actual: " + controlador.getNombreJugadorActual());
        } else if (controlador.estaFinalizada()) {
            imprimir("La partida cargada ya estaba finalizada. Ganador: " + controlador.getNombreGanador());
        }
        imprimir("");
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
