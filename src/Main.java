import controlador.Controlador;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import modelo.Partida;
import vista.VistaConfiguracion;
import vista.VistaConsola;
import vista.VistaJuego;

public class Main extends Application {

    @Override
    public void start(Stage stagePrincipal) {
        Partida partida = new Partida();
        Controlador controlador = new Controlador(partida);

        VistaJuego vistaJuego = new VistaJuego(controlador);
        VistaConsola vistaConsola = new VistaConsola(controlador);

        BorderPane contenedorPrincipal = new BorderPane();

        VistaConfiguracion vistaConfiguracion = new VistaConfiguracion(controlador, () -> contenedorPrincipal.setCenter(vistaJuego.getRoot()));

        contenedorPrincipal.setCenter(vistaConfiguracion.getRoot());

        controlador.registrarVista(vistaJuego);
        controlador.registrarVista(vistaConsola);
        controlador.registrarVista(vistaConfiguracion);

        Scene escenaPrincipal = new Scene(contenedorPrincipal, 880, 560);
        stagePrincipal.setTitle("Diez Mil con cinco dados (Grafica)");
        stagePrincipal.setScene(escenaPrincipal);
        stagePrincipal.setOnCloseRequest(e -> Platform.exit());

        Stage stageConsola = new Stage();
        Scene escenaConsola = new Scene(vistaConsola.getRoot(), 880, 560);
        stageConsola.setTitle("Diez Mil con cinco dados (Consola)");
        stageConsola.setScene(escenaConsola);
        stageConsola.setOnCloseRequest(e -> Platform.exit());

        stagePrincipal.show();
        stageConsola.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
