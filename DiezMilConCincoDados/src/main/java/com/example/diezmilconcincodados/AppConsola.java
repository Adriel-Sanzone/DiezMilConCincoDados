package com.example.diezmilconcincodados;

import com.example.diezmilconcincodados.controlador.ControladorConsolaMain;
import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;
import com.example.diezmilconcincodados.vista.VistaMenuConsola;

import java.io.File;

public class AppConsola {
    public static void main(String[] args) {

        File saveFile = new File("partida.b");

        Juego juego;
        if (saveFile.exists()) {
            try {
                juego = Juego.cargarPartida(saveFile);
                System.out.println("\nSe cargó la partida \"" + saveFile.getName() + "\" automáticamente...");
            } catch (Exception e) {
                System.err.println("No se pudo cargar la partida (archivo corrupto o incompatible). Se iniciará una nueva partida.");
                juego = new Juego();
            }
        } else {
            juego = new Juego();
        }

        VistaMenuConsola vistaMenu = new VistaMenuConsola();
        VistaJuegoConsola vistaJuego = new VistaJuegoConsola();

        ControladorConsolaMain controlador =
                new ControladorConsolaMain(juego, vistaMenu, vistaJuego);

        controlador.ejecutarLoopPrincipal();

        try {
            juego.guardarPartida(saveFile);
            System.out.println("Partida guardada automáticamente en " + saveFile.getName());
        } catch (Exception e) {
            System.err.println("Error al guardar la partida al salir: " + e.getMessage());
        }
    }
}


