package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VistaConexion {

    private final Conector conector;
    private final VBox root;

    private final TextField campoNombre;
    private final Button botonConectar;
    private final Label etiquetaEstado;

    public VistaConexion(String direccionServidor, Conector conector) {
        if (direccionServidor == null || direccionServidor.isBlank()) {
            throw new IllegalArgumentException("La dirección del servidor no puede estar vacía.");
        }
        if (conector == null) {
            throw new IllegalArgumentException("El conector no puede ser nulo.");
        }
        this.conector = conector;

        Label titulo = new Label("Diez Mil con cinco dados");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));

        Label subtitulo = new Label("Ingresá tu nombre para entrar a la partida.");
        subtitulo.setFont(Font.font(13));

        this.campoNombre = new TextField();
        this.campoNombre.setPromptText("Tu nombre de jugador");
        this.campoNombre.setMaxWidth(240);
        this.campoNombre.setOnAction(e -> accionConectar());

        this.botonConectar = new Button("Conectar");
        this.botonConectar.setPrefWidth(180);
        this.botonConectar.setOnAction(e -> accionConectar());

        this.etiquetaEstado = new Label("Servidor: " + direccionServidor);
        this.etiquetaEstado.setWrapText(true);
        this.etiquetaEstado.setMaxWidth(420);

        this.root = new VBox(16, titulo, subtitulo, campoNombre, botonConectar, etiquetaEstado);
        this.root.setAlignment(Pos.CENTER);
        this.root.setPadding(new Insets(24));
    }

    public Parent getRoot() {
        return root;
    }

    private void accionConectar() {
        String nombre = campoNombre.getText() == null ? "" : campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            etiquetaEstado.setText("Ingresá tu nombre de jugador.");
            return;
        }

        botonConectar.setDisable(true);
        etiquetaEstado.setText("Conectando...");
        try {
            conector.conectar(nombre);
        } catch (Exception ex) {
            botonConectar.setDisable(false);
            etiquetaEstado.setText("No se pudo conectar: " + ex.getMessage());
        }
    }
}
