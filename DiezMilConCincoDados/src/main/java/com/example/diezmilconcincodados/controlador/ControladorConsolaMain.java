package com.example.diezmilconcincodados.controlador;

import com.example.diezmilconcincodados.model.IJuego;
import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.model.Jugador;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;
import com.example.diezmilconcincodados.vista.VistaMenuConsola;
import ar.edu.unlu.poo.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ControladorConsolaMain implements IControladorRemoto {

    private IJuego juego;
    private final VistaMenuConsola vistaMenu;
    private final VistaJuegoConsola vistaJuego;

    public ControladorConsolaMain(IJuego juego, VistaMenuConsola vistaMenu, VistaJuegoConsola vistaJuego) throws RemoteException {
        this.juego = juego;
        this.vistaMenu = vistaMenu;
        this.vistaJuego = vistaJuego;

    }

    public void ejecutarLoopPrincipal() throws RemoteException {
        boolean salir = false;
        while (!salir) {
            int opcion = vistaMenu.mostrarMenuPrincipal();
            switch (opcion) {
                case 1 -> {
                    if (juego.isFinalizado()) {
                        vistaMenu.mostrarMensaje("\u001B[33mLa partida anterior ya terminó. Se reiniciará automáticamente.\u001B[0m");
                        juego.resetearPartida();
                    } else if (juego.getJugadores().isEmpty()) {
                        vistaMenu.mostrarMensaje("\u001B[33mNo hay jugadores. Agrega al menos uno antes de jugar.\u001B[0m");
                    } else {
                        // ¡IMPORTANTE! Ya NO llamamos agregar/quitarObservador manualmente
                        // La librería lo hace automáticamente al conectar el cliente
                        ControladorConsolaEnJuego enJuego = new ControladorConsolaEnJuego(juego, vistaJuego);
                        enJuego.ejecutarLoopPartida();
                        actualizar(null, null);  // refresca al volver del juego
                    }
                }
                case 2 -> agregarJugador();
                case 3 -> eliminarJugador();
                case 4 -> mostrarEstadoPartida();
                case 5 -> guardarPartida();
                case 6 -> cargarPartida();
                case 7 -> vistaMenu.mostrarInstrucciones();
                case 0 -> salir = true;
                default -> vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Opción no válida.");
            }
        }
    }

    private void agregarJugador() {
        String nombre = vistaMenu.solicitarNombreJugador();
        if (nombre == null || nombre.isEmpty()) {
            vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Nombre vacío.");
            return;
        }
        try {
            if (juego.getJugadores().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre))) {
                vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Ya existe un jugador con ese nombre.");
                return;
            }
            juego.agregarJugador(new Jugador(nombre));
            vistaMenu.mostrarMensaje("Jugador agregado: \u001B[34m" + nombre + "\u001B[0m");
        } catch (RemoteException e) {
            vistaMenu.mostrarMensaje("\u001B[31mError de red al agregar jugador: " + e.getMessage() + "\u001B[0m");
        }
    }

    private void eliminarJugador() {
        String nombre = vistaMenu.solicitarNombreJugadorEliminar();
        if (nombre == null || nombre.isEmpty()) {
            vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Nombre vacío.");
            return;
        }
        try {
            boolean ok = juego.eliminarJugador(nombre);
            if (ok) {
                vistaMenu.mostrarMensaje("Jugador eliminado: " + nombre);
            } else {
                vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: No existe un jugador con ese nombre.");
            }
        } catch (RemoteException e) {
            vistaMenu.mostrarMensaje("\u001B[31mError de red al eliminar jugador: " + e.getMessage() + "\u001B[0m");
        }
    }

    private void guardarPartida() {
        String nombre = vistaMenu.solicitarNombreArchivoGuardar();
        if (nombre == null || nombre.isEmpty()) {
            nombre = "partida.b";
        }
        // Usa la clase concreta directamente (no el remoto)
        Juego partidaAGuardar = (Juego) juego;  // casteo seguro si es el mismo objeto
        partidaAGuardar.guardarPartida(new File(nombre));
        vistaMenu.mostrarMensaje("Partida guardada en \u001B[33m" + nombre + "\u001B[0m");
    }

    private void cargarPartida() throws RemoteException {
        String nombre = vistaMenu.solicitarNombreArchivoCargar();
        if (nombre == null || nombre.isEmpty()) {
            nombre = "partida.b";
        }
        Juego cargado = Juego.cargarPartida(new File(nombre));
        if (cargado == null) {
            vistaMenu.mostrarMensaje("\u001B[31mERROR DE CARGADO DE PARTIDA\u001B[0m (archivo inexistente o corrupto).");
            return;
        }
        this.juego = cargado;  // ← asigna el nuevo Juego local
        vistaMenu.mostrarMensaje("Partida cargada de \u001B[33m" + nombre + "\u001B[0m");
        actualizar(null, null);
    }

    private void mostrarEstadoPartida() {
        List<String> nombres = new ArrayList<>();
        List<Integer> puntos = new ArrayList<>();
        try {
            for (Jugador j : juego.getJugadores()) {
                nombres.add(j.getNombre());
                puntos.add(j.getPuntosTotales());
            }
            String jugadorActual = juego.getJugadorActual() != null
                    ? juego.getJugadorActual().getNombre()
                    : "N/A";
            vistaMenu.mostrarEstadoJuegoActualizado(
                    nombres, puntos, jugadorActual, 0, 0, new int[0], 0, false
            );
        } catch (RemoteException e) {
            vistaMenu.mostrarMensaje("\u001B[31mError de red al mostrar estado: " + e.getMessage() + "\u001B[0m");
        }
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object arg) throws RemoteException {

        mostrarEstadoPartida();
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) {
        this.juego = (IJuego) modeloRemoto;
    }
}