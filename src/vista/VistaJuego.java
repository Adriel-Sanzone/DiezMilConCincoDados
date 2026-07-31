package vista;

import controlador.Controlador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import observer.Evento;
import observer.Observador;

import java.util.ArrayList;
import java.util.List;

public class VistaJuego implements Observador {

    private static final int CANTIDAD_DADOS_VISUALES = 5;

    private final Controlador controlador;
    private final BorderPane root;

    private final Label etiquetaTitulo;
    private final Label etiquetaJugadorActual;
    private final List<DadoView> vistasDados;
    private final ListView<String> listaJugadores;
    private final Label etiquetaPuntosTurno;
    private final Label etiquetaUltimoPuntaje;
    private final Button botonTirar;
    private final Button botonConfirmarSeleccion;
    private final Button botonPasarTurno;
    private final Button botonPlantarse;
    private final Button botonGuardarPartida;
    private final Label etiquetaMensaje;

    private final List<Integer> indicesSeleccionados;

    public VistaJuego(Controlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo.");
        }
        this.controlador = controlador;
        this.indicesSeleccionados = new ArrayList<>();

        this.etiquetaTitulo = new Label("Diez Mil con cinco dados");
        this.etiquetaTitulo.setFont(Font.font("System", FontWeight.BOLD, 22));

        this.etiquetaJugadorActual = new Label("Esperando inicio...");
        this.etiquetaJugadorActual.setFont(Font.font(16));

        this.vistasDados = new ArrayList<>();
        for (int i = 0; i < CANTIDAD_DADOS_VISUALES; i++) {
            final int indice = i;
            DadoView dv = new DadoView();
            dv.setOnMouseClicked(e -> alClickearDado(indice));
            vistasDados.add(dv);
        }

        this.listaJugadores = new ListView<>();
        this.listaJugadores.setPrefWidth(220);
        this.listaJugadores.setFocusTraversable(false);

        this.etiquetaPuntosTurno = new Label("Puntos del turno: 0");
        this.etiquetaPuntosTurno.setFont(Font.font("System", FontWeight.BOLD, 14));

        this.etiquetaUltimoPuntaje = new Label("Última tirada: -");

        this.botonTirar = new Button("Tirar dados");
        this.botonTirar.setPrefWidth(140);
        this.botonTirar.setOnAction(e -> accionTirar());

        this.botonConfirmarSeleccion = new Button("Confirmar selección");
        this.botonConfirmarSeleccion.setPrefWidth(160);
        this.botonConfirmarSeleccion.setOnAction(e -> accionConfirmarSeleccion());

        this.botonPasarTurno = new Button("Pasar turno");
        this.botonPasarTurno.setPrefWidth(140);
        this.botonPasarTurno.setOnAction(e -> accionPasarTurno());

        this.botonPlantarse = new Button("Plantarse");
        this.botonPlantarse.setPrefWidth(140);
        this.botonPlantarse.setOnAction(e -> accionPlantarse());

        this.botonGuardarPartida = new Button("Guardar partida");
        this.botonGuardarPartida.setPrefWidth(160);
        this.botonGuardarPartida.setOnAction(e -> accionGuardarPartida());

        this.etiquetaMensaje = new Label("");
        this.etiquetaMensaje.setFont(Font.font(13));

        this.root = construirRoot();
        refrescarUI();
    }

    private BorderPane construirRoot() {
        VBox topBox = new VBox(6, etiquetaTitulo, etiquetaJugadorActual);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(16));

        HBox dadosBox = new HBox(15);
        dadosBox.setAlignment(Pos.CENTER);
        dadosBox.setPadding(new Insets(20));
        for (DadoView dv : vistasDados) {
            dadosBox.getChildren().add(dv);
        }

        VBox centerBox = new VBox(10, dadosBox, etiquetaUltimoPuntaje);
        centerBox.setAlignment(Pos.CENTER);

        Label tituloJugadores = new Label("Jugadores");
        tituloJugadores.setFont(Font.font("System", FontWeight.BOLD, 14));
        VBox rightBox = new VBox(8, tituloJugadores, listaJugadores);
        rightBox.setPadding(new Insets(16));
        rightBox.setAlignment(Pos.TOP_CENTER);

        HBox botonesBox = new HBox(10, botonTirar, botonConfirmarSeleccion, botonPasarTurno, botonPlantarse);
        botonesBox.setAlignment(Pos.CENTER);

        HBox botonesPersistenciaBox = new HBox(10, botonGuardarPartida);
        botonesPersistenciaBox.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(8, etiquetaPuntosTurno, botonesBox, botonesPersistenciaBox, etiquetaMensaje);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(16));

        BorderPane pane = new BorderPane();
        pane.setTop(topBox);
        pane.setCenter(centerBox);
        pane.setRight(rightBox);
        pane.setBottom(bottomBox);
        return pane;
    }

    public Parent getRoot() {
        return root;
    }

    @Override
    public void actualizar(Evento evento) {
        switch (evento) {
            case JUGADOR_AGREGADO -> mostrarMensaje("Jugador agregado.");
            case PARTIDA_INICIADA -> mostrarMensaje("¡Partida iniciada!");
            case DADOS_TIRADOS -> mostrarMensaje(mensajePostTirada());
            case DADOS_SELECCIONADOS -> mostrarMensaje(mensajeUltimaSeleccion());
            case TURNO_PERDIDO -> mostrarMensaje("¡" + controlador.getNombreJugadorActual()
                    + " perdio el turno!");
            case JUGADOR_PLANTADO -> mostrarMensaje(controlador.getNombreJugadorActual()
                    + " se planto.");
            case CAMBIO_TURNO -> mostrarMensaje("Turno de " + controlador.getNombreJugadorActual() + ".");
            case PARTIDA_FINALIZADA -> mostrarMensaje("¡Gano " + controlador.getNombreGanador() + "!");
        }
        refrescarUI();
    }

    private void refrescarUI() {
        if (!controlador.esFasePostTirada()) {
            limpiarSeleccionTemporal();
        }

        actualizarEncabezado();
        actualizarDados();
        actualizarListaJugadores();
        actualizarEtiquetasTurno();
        actualizarBotones();
    }

    private void actualizarEncabezado() {
        if (controlador.estaEnConfiguracion()) {
            etiquetaJugadorActual.setText("Configuracion: agregue jugadores e inicie la partida.");
        } else if (controlador.estaFinalizada()) {
            etiquetaJugadorActual.setText("Partida finalizada. Ganador: "
                    + controlador.getNombreGanador());
        } else {
            etiquetaJugadorActual.setText("Turno de: " + controlador.getNombreJugadorActual());
        }
    }

    private void actualizarDados() {
        List<Integer> valores = controlador.getValoresDeLosDados();
        List<Boolean> reservas = controlador.getReservasDeLosDados();
        for (int i = 0; i < CANTIDAD_DADOS_VISUALES; i++) {
            DadoView dv = vistasDados.get(i);
            dv.setValor(valores.get(i));
            dv.setReservado(reservas.get(i));
            if (reservas.get(i)) {
                dv.setSeleccionado(false);
            }
        }
    }

    private void actualizarListaJugadores() {
        listaJugadores.getItems().clear();
        List<String> nombres = controlador.getNombresJugadores();
        List<Integer> puntajes = controlador.getPuntajesJugadores();
        for (int i = 0; i < nombres.size(); i++) {
            listaJugadores.getItems().add(nombres.get(i) + " — " + puntajes.get(i) + " pts");
        }
    }

    private void actualizarEtiquetasTurno() {
        etiquetaPuntosTurno.setText("Puntos del turno: " + controlador.getPuntosAcumuladosTurno());

        if (!controlador.hayUltimoResultado()) {
            etiquetaUltimoPuntaje.setText("Ultima tirada: -");
        } else {
            etiquetaUltimoPuntaje.setText("Ultima seleccion: +" + controlador.getPuntosUltimoResultado()
                    + " pts (puntuaron: " + controlador.getValoresUltimoResultado() + ")");
        }
    }

    private void actualizarBotones() {
        boolean enCurso = controlador.estaEnCurso();
        botonGuardarPartida.setDisable(!enCurso);
        if (!enCurso) {
            botonTirar.setDisable(true);
            botonConfirmarSeleccion.setDisable(true);
            botonPasarTurno.setDisable(true);
            botonPlantarse.setDisable(true);
            return;
        }
        if (controlador.esFaseInicial()) {
            botonTirar.setDisable(false);
            botonConfirmarSeleccion.setDisable(true);
            botonPasarTurno.setDisable(true);
            botonPlantarse.setDisable(true);
        } else if (controlador.esFasePostTirada()) {
            botonTirar.setDisable(true);
            boolean haySeleccionados = !indicesSeleccionados.isEmpty();
            boolean hayCombinaciones = controlador.hayCombinacionesPosibles();
            botonConfirmarSeleccion.setDisable(!hayCombinaciones || !haySeleccionados);
            botonPasarTurno.setDisable(hayCombinaciones);
            botonPlantarse.setDisable(true);
        } else if (controlador.esFasePostSeleccion()) {
            botonTirar.setDisable(false);
            botonConfirmarSeleccion.setDisable(true);
            botonPasarTurno.setDisable(true);
            botonPlantarse.setDisable(controlador.getPuntosAcumuladosTurno() == 0);
        }
    }

    private void alClickearDado(int indice) {
        if (!controlador.esFasePostTirada()) {
            return;
        }
        DadoView dv = vistasDados.get(indice);
        if (dv.estaReservado()) {
            return;
        }
        if (indicesSeleccionados.contains(indice)) {
            indicesSeleccionados.remove(Integer.valueOf(indice));
            dv.setSeleccionado(false);
        } else {
            indicesSeleccionados.add(indice);
            dv.setSeleccionado(true);
        }
        actualizarBotones();
    }

    private void limpiarSeleccionTemporal() {
        indicesSeleccionados.clear();
        for (DadoView dv : vistasDados) {
            dv.setSeleccionado(false);
        }
    }

    private String mensajePostTirada() {
        String nombre = controlador.getNombreJugadorActual();
        if (!controlador.hayCombinacionesPosibles()) {
            return nombre + " tiro sin combinaciones posibles: debe pasar el turno.";
        }
        return nombre + " tiro los dados: selecciona para reservar y confirma.";
    }

    private String mensajeUltimaSeleccion() {
        if (!controlador.hayUltimoResultado()) {
            return "";
        }
        return controlador.getNombreJugadorActual()
                + " sumo " + controlador.getPuntosUltimoResultado() + " pts con la seleccion.";
    }

    private void accionTirar() {
        try {
            controlador.tirarDados();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void accionConfirmarSeleccion() {
        try {
            controlador.seleccionarDados(new ArrayList<>(indicesSeleccionados));
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void accionPasarTurno() {
        try {
            controlador.pasarTurno();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void accionPlantarse() {
        try {
            controlador.plantarse();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void accionGuardarPartida() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Guardar partida");
        dialogo.setHeaderText("Ingresa un nombre para la partida guardada");
        dialogo.setContentText("Nombre:");
        dialogo.showAndWait().ifPresent(nombre -> {
            String limpio = nombre == null ? "" : nombre.trim();
            if (limpio.isEmpty()) {
                mostrarError("El nombre no puede estar vacío.");
                return;
            }
            try {
                controlador.guardarPartida(limpio);
                mostrarMensaje("Partida guardada como \"" + limpio + "\".");
            } catch (Exception ex) {
                mostrarError("No se pudo guardar: " + ex.getMessage());
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        etiquetaMensaje.setText(mensaje);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alert.setHeaderText("No se pudo completar la accion");
        alert.showAndWait();
    }
}
