package vista;

import controlador.Controlador;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import observer.Evento;
import observer.Observador;

public class VistaConfiguracion implements Observador {

    private final Controlador controlador;
    private final Runnable alIniciarPartida;
    private final VBox root;

    private final Label etiquetaIdentidad;
    private final Button botonIniciar;
    private final ListView<String> listaJugadores;
    private final Label etiquetaEstado;

    public VistaConfiguracion(Controlador controlador, Runnable alIniciarPartida) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo.");
        }
        if (alIniciarPartida == null) {
            throw new IllegalArgumentException("El callback no puede ser nulo.");
        }
        this.controlador = controlador;
        this.alIniciarPartida = alIniciarPartida;

        Label titulo = new Label("Sala de espera");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 22));

        this.etiquetaIdentidad = new Label();
        this.etiquetaIdentidad.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label subtitulo = new Label("Esperá a que se conecten los demás jugadores.");
        subtitulo.setFont(Font.font(13));

        Label tituloJugadores = new Label("Jugadores conectados");
        tituloJugadores.setFont(Font.font("System", FontWeight.BOLD, 14));

        this.listaJugadores = new ListView<>();
        this.listaJugadores.setPrefHeight(180);
        this.listaJugadores.setPrefWidth(280);
        this.listaJugadores.setFocusTraversable(false);

        this.botonIniciar = new Button("Iniciar partida");
        this.botonIniciar.setOnAction(e -> accionIniciar());
        this.botonIniciar.setPrefWidth(180);

        this.etiquetaEstado = new Label();
        this.etiquetaEstado.setFont(Font.font(13));

        this.root = new VBox(10,
                titulo, etiquetaIdentidad, subtitulo, tituloJugadores,
                listaJugadores, botonIniciar, etiquetaEstado);
        this.root.setAlignment(Pos.CENTER);
        this.root.setPadding(new Insets(20));

        refrescarUI();
    }

    public Parent getRoot() {
        return root;
    }

    @Override
    public void actualizar(Evento evento) {
        Platform.runLater(() -> {
            if (evento == Evento.JUGADOR_AGREGADO) {
                refrescarUI();
            } else if (evento == Evento.PARTIDA_INICIADA) {
                alIniciarPartida.run();
            }
        });
    }

    private void refrescarUI() {
        etiquetaIdentidad.setText("Estás jugando como: " + controlador.getMiJugador());
        refrescarListaJugadores();
        actualizarBotonIniciar();
    }

    private void refrescarListaJugadores() {
        listaJugadores.getItems().clear();
        for (String nombre : controlador.getNombresJugadores()) {
            listaJugadores.getItems().add(nombre);
        }
    }

    private void actualizarBotonIniciar() {
        int cantidad = controlador.getCantidadJugadores();
        int minimo = controlador.getMinimoJugadoresParaIniciar();
        botonIniciar.setDisable(cantidad < minimo);
        if (cantidad < minimo) {
            etiquetaEstado.setText("Se necesitan al menos " + minimo
                    + " jugadores (conectados: " + cantidad + ").");
        } else {
            etiquetaEstado.setText("Listo para iniciar (" + cantidad + " jugadores).");
        }
    }

    private void accionIniciar() {
        try {
            controlador.iniciarPartida();
        } catch (RuntimeException ex) {
            mostrarAdvertencia(ex.getMessage());
        }
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alert.setHeaderText("No se pudo iniciar");
        alert.showAndWait();
    }
}
