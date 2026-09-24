import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.cliente.Cliente;
import controlador.Controlador;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vista.VistaConexion;
import vista.VistaConfiguracion;
import vista.VistaConsola;
import vista.VistaJuego;

import java.io.IOException;
import java.net.ServerSocket;

public class ClienteDiezMil extends Application {

    private static final int PUERTOS_A_PROBAR = 100;

    private final Controlador controlador = new Controlador();
    private final BorderPane contenedorPrincipal = new BorderPane();

    private Stage stagePrincipal;

    @Override
    public void start(Stage stage) {
        this.stagePrincipal = stage;

        VistaConexion vistaConexion = new VistaConexion(
                ConfiguracionRed.getDireccionServidor(), this::conectar);
        contenedorPrincipal.setCenter(vistaConexion.getRoot());

        Scene escenaPrincipal = new Scene(contenedorPrincipal, 880, 560);
        stagePrincipal.setTitle("Diez Mil con cinco dados (Grafica)");
        stagePrincipal.setScene(escenaPrincipal);
        stagePrincipal.setOnCloseRequest(e -> cerrarAplicacion());
        stagePrincipal.show();
    }

    private void conectar(String nombreJugador) throws Exception {
        String ipPropia = deducirIpPropia();
        int puertoPropio = buscarPuertoLibre(ConfiguracionRed.PUERTO_BASE_CLIENTE);

        Cliente cliente = new Cliente(ipPropia, puertoPropio,
                ConfiguracionRed.IP_SERVIDOR, ConfiguracionRed.PUERTO_SERVIDOR);
        cliente.iniciar(controlador);

        entrarAlJuego(nombreJugador);
    }

    private String deducirIpPropia() {
        if (ConfiguracionRed.IP_SERVIDOR.startsWith("127.")) {
            return "127.0.0.1";
        }
        for (String ip : Util.getIpDisponibles()) {
            if (!ip.startsWith("127.")) {
                return ip;
            }
        }
        return "127.0.0.1";
    }

    private int buscarPuertoLibre(int desde) {
        for (int puerto = desde; puerto < desde + PUERTOS_A_PROBAR; puerto++) {
            try (ServerSocket socket = new ServerSocket(puerto)) {
                return socket.getLocalPort();
            } catch (IOException ocupado) {
                continue;
            }
        }
        throw new IllegalStateException(
                "No se encontro un puerto libre para este cliente entre " + desde
                        + " y " + (desde + PUERTOS_A_PROBAR) + ".");
    }

    private void entrarAlJuego(String nombre) {
        boolean enConfiguracion = controlador.estaEnConfiguracion();
        if (enConfiguracion) {
            controlador.unirse(nombre);
        } else {
            controlador.setMiJugador(nombre);
        }

        VistaJuego vistaJuego = new VistaJuego(controlador);
        VistaConsola vistaConsola = new VistaConsola(controlador);
        VistaConfiguracion vistaConfiguracion = new VistaConfiguracion(controlador,
                () -> contenedorPrincipal.setCenter(vistaJuego.getRoot()));

        controlador.registrarVista(vistaJuego);
        controlador.registrarVista(vistaConsola);
        controlador.registrarVista(vistaConfiguracion);

        if (enConfiguracion) {
            contenedorPrincipal.setCenter(vistaConfiguracion.getRoot());
        } else {
            contenedorPrincipal.setCenter(vistaJuego.getRoot());
        }

        stagePrincipal.setTitle("Diez Mil con cinco dados - " + nombre + " (Grafica)");
        abrirVentanaConsola(vistaConsola, nombre);
    }

    private void abrirVentanaConsola(VistaConsola vistaConsola, String nombre) {
        Stage stageConsola = new Stage();
        Scene escenaConsola = new Scene(vistaConsola.getRoot(), 880, 560);
        stageConsola.setTitle("Diez Mil con cinco dados - " + nombre + " (Consola)");
        stageConsola.setScene(escenaConsola);
        stageConsola.setOnCloseRequest(e -> cerrarAplicacion());
        stageConsola.show();
    }

    private void cerrarAplicacion() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
