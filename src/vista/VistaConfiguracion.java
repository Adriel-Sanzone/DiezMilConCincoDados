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
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import observer.Evento;
import observer.Observador;

public class VistaConfiguracion implements Observador {

    private final Controlador controlador;
    private final Runnable alIniciarPartida;
    private final VBox root;

    private final TextField campoNombre;
    private final Button botonAgregar;
    private final Button botonIniciar;
    private final ListView<String> listaJugadores;
    private final Label etiquetaEstado;

    private final ListView<String> listaPartidasGuardadas;
    private final Button botonCargarPartida;
    private final Button botonRefrescarLista;

    public VistaConfiguracion(Controlador controlador, Runnable alIniciarPartida) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo.");
        }
        if (alIniciarPartida == null) {
            throw new IllegalArgumentException("El callback no puede ser nulo.");
        }
        this.controlador = controlador;
        this.alIniciarPartida = alIniciarPartida;

        Label titulo = new Label("Configuración de la partida");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label subtitulo = new Label("Cargá los jugadores y luego presioná \"Iniciar partida\".");
        subtitulo.setFont(Font.font(13));

        this.campoNombre = new TextField();
        this.campoNombre.setPromptText("Nombre del jugador");
        this.campoNombre.setPrefWidth(220);
        this.campoNombre.setOnAction(e -> accionAgregar());

        this.botonAgregar = new Button("Agregar");
        this.botonAgregar.setOnAction(e -> accionAgregar());

        HBox cargaBox = new HBox(8, campoNombre, botonAgregar);
        cargaBox.setAlignment(Pos.CENTER);

        Label tituloJugadores = new Label("Jugadores agregados");
        tituloJugadores.setFont(Font.font("System", FontWeight.BOLD, 14));

        this.listaJugadores = new ListView<>();
        this.listaJugadores.setPrefHeight(140);
        this.listaJugadores.setPrefWidth(260);
        this.listaJugadores.setFocusTraversable(false);

        this.botonIniciar = new Button("Iniciar partida");
        this.botonIniciar.setOnAction(e -> accionIniciar());
        this.botonIniciar.setPrefWidth(180);

        this.etiquetaEstado = new Label();
        this.etiquetaEstado.setFont(Font.font(13));

        Separator separador = new Separator();

        Label tituloPartidas = new Label("Partidas guardadas");
        tituloPartidas.setFont(Font.font("System", FontWeight.BOLD, 14));

        this.listaPartidasGuardadas = new ListView<>();
        this.listaPartidasGuardadas.setPrefHeight(120);
        this.listaPartidasGuardadas.setPrefWidth(260);
        this.listaPartidasGuardadas.setFocusTraversable(false);

        this.botonCargarPartida = new Button("Cargar partida seleccionada");
        this.botonCargarPartida.setPrefWidth(220);
        this.botonCargarPartida.setOnAction(e -> accionCargar());

        this.botonRefrescarLista = new Button("Refrescar lista");
        this.botonRefrescarLista.setPrefWidth(140);
        this.botonRefrescarLista.setOnAction(e -> refrescarListaPartidas());

        HBox botonesPartidasBox = new HBox(8, botonCargarPartida, botonRefrescarLista);
        botonesPartidasBox.setAlignment(Pos.CENTER);

        this.root = new VBox(10,
                titulo, subtitulo, cargaBox, tituloJugadores,
                listaJugadores, botonIniciar, etiquetaEstado,
                separador, tituloPartidas, listaPartidasGuardadas, botonesPartidasBox);
        this.root.setAlignment(Pos.CENTER);
        this.root.setPadding(new Insets(20));

        actualizarBotonIniciar();
        refrescarListaPartidas();
    }

    public Parent getRoot() {
        return root;
    }

    @Override
    public void actualizar(Evento evento) {
        if (evento == Evento.JUGADOR_AGREGADO) {
            refrescarListaJugadores();
            actualizarBotonIniciar();
        } else if (evento == Evento.PARTIDA_INICIADA) {
            alIniciarPartida.run();
        } else if (evento == Evento.PARTIDA_CARGADA) {
            if (controlador.estaEnConfiguracion()) {
                refrescarListaJugadores();
                actualizarBotonIniciar();
            } else {
                alIniciarPartida.run();
            }
        }
    }

    private void refrescarListaJugadores() {
        listaJugadores.getItems().clear();
        for (String nombre : controlador.getNombresJugadores()) {
            listaJugadores.getItems().add(nombre);
        }
    }

    private void refrescarListaPartidas() {
        listaPartidasGuardadas.getItems().clear();
        for (String nombre : controlador.listarPartidasGuardadas()) {
            listaPartidasGuardadas.getItems().add(nombre);
        }
        botonCargarPartida.setDisable(listaPartidasGuardadas.getItems().isEmpty());
    }

    private void actualizarBotonIniciar() {
        int cantidad = controlador.getCantidadJugadores();
        int minimo = controlador.getMinimoJugadoresParaIniciar();
        botonIniciar.setDisable(cantidad < minimo);
        if (cantidad < minimo) {
            etiquetaEstado.setText("Se necesitan al menos " + minimo
                    + " jugadores (cargados: " + cantidad + ").");
        } else {
            etiquetaEstado.setText("Listo para iniciar (" + cantidad + " jugadores).");
        }
    }

    private void accionAgregar() {
        String nombre = campoNombre.getText();
        if (nombre == null || nombre.isBlank()) {
            mostrarAdvertencia("El nombre no puede estar vacío.");
            return;
        }
        try {
            controlador.agregarJugador(nombre);
            campoNombre.clear();
            campoNombre.requestFocus();
        } catch (RuntimeException ex) {
            mostrarAdvertencia(ex.getMessage());
        }
    }

    private void accionIniciar() {
        try {
            controlador.iniciarPartida();
        } catch (RuntimeException ex) {
            mostrarAdvertencia(ex.getMessage());
        }
    }

    private void accionCargar() {
        String seleccionada = listaPartidasGuardadas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Seleccioná una partida de la lista.");
            return;
        }
        try {
            controlador.cargarPartida(seleccionada);
        } catch (Exception ex) {
            mostrarAdvertencia("No se pudo cargar la partida: " + ex.getMessage());
        }
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alert.setHeaderText("Configuración inválida");
        alert.showAndWait();
    }
}
