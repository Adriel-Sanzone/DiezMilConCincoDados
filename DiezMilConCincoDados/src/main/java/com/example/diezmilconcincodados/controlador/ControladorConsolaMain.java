package com.example.diezmilconcincodados.controlador;

import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.model.Jugador;
import com.example.diezmilconcincodados.observer.Observador;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;
import com.example.diezmilconcincodados.vista.VistaMenuConsola;

import java.io.File;

public class ControladorConsolaMain implements Observador
{
    private Juego juego;
    private final VistaMenuConsola vistaMenu;
    private final VistaJuegoConsola vistaJuego;

    public ControladorConsolaMain(Juego juego, VistaMenuConsola vistaMenu, VistaJuegoConsola vistaJuego)
    {
        this.juego = juego;
        this.vistaMenu = vistaMenu;
        this.vistaJuego = vistaJuego;
        if (this.juego != null) this.juego.agregarObservador(() -> {});
    }

    public void ejecutarLoopPrincipal()
    {
        boolean salir = false;
        while (!salir)
        {
            int opcion = vistaMenu.mostrarMenuPrincipal();
            switch (opcion)
            {
                case 1 ->
                {
                    ControladorConsolaEnJuego enJuego = new ControladorConsolaEnJuego(juego, vistaJuego);
                    enJuego.ejecutarLoopPartida();
                }
                case 2 -> agregarJugador();
                case 3 -> eliminarJugador();
                case 4 -> vistaMenu.mostrarEstadoJuego(juego);
                case 5 -> guardarPartida();
                case 6 -> cargarPartida();
                case 7 -> vistaMenu.mostrarInstrucciones();
                case 0 -> salir = true;
                default -> vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Opción no válida.");
            }
        }
    }

    private void agregarJugador()
    {
        String nombre = vistaMenu.solicitarNombreJugador();

        if (nombre == null || nombre.isEmpty())
        {
            vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Nombre vacio.");
            return;
        }
        if (juego.getJugadores().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre)))
        {
            vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Ya existe un jugador con ese nombre.");
            return;
        }

        juego.agregarJugador(new Jugador(nombre));
        vistaMenu.mostrarMensaje("Jugador agregado: \u001B[34m" + nombre + "\u001B[0m");
    }

    private void eliminarJugador()
    {
        String nombre = vistaMenu.solicitarNombreJugadorEliminar();

        if (nombre == null || nombre.isEmpty())
        {
            vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: Nombre vacio.");
            return;
        }

        boolean ok = juego.eliminarJugador(nombre);
        if (ok)
        {
            vistaMenu.mostrarMensaje("Jugador eliminado: " + nombre);
        } else
        {
            vistaMenu.mostrarMensaje("\u001B[31mERROR\u001B[0m: No existe un jugador con ese nombre.");
        }
    }

    private void guardarPartida()
    {
        String nombre = vistaMenu.solicitarNombreArchivoGuardar();

        if (nombre == null || nombre.isEmpty())
        {
            nombre = "partida.b";
        }

        juego.guardarPartida(new File(nombre));
        vistaMenu.mostrarMensaje("Partida guardada en \u001B[33m" + nombre + "\u001B[0m");
    }

    private void cargarPartida()
    {
        String nombre = vistaMenu.solicitarNombreArchivoCargar();

        if (nombre == null || nombre.isEmpty())
        {
            nombre = "partida.b";
        }

        Juego cargado = Juego.cargarPartida(new File(nombre));

        if (cargado == null)
        {
            vistaMenu.mostrarMensaje("\u001B[31mERROR DE CARGADO DE PARTIDA\u001B[0m (archivo inexistente o corrupto).");
            return;
        }

        if (this.juego != null)
        {
            this.juego.quitarObservador(() -> {});
        }
        this.juego = cargado;
        this.juego.agregarObservador(() -> {});
        vistaMenu.mostrarMensaje("Partida cargada de \u001B[33m" + nombre + "\u001B[0m");
    }

    @Override
    public void actualizar() {
    }
}

