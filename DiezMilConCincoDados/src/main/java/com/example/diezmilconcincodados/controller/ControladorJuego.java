package com.example.diezmilconcincodados.controller;

import com.example.diezmilconcincodados.model.JuegoDiezMil;
import com.example.diezmilconcincodados.model.Jugador;
import com.example.diezmilconcincodados.view.VistaConsola;

import java.util.List;

public class ControladorJuego {
    private JuegoDiezMil modelo;
    private VistaConsola vista;

    public ControladorJuego(JuegoDiezMil modelo, VistaConsola vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void iniciar() {
        vista.mostrarMensaje("=== JUEGO DIEZ MIL ===");

        while (!modelo.hayGanador()) {
            Jugador j = modelo.getJugadorActual();
            vista.mostrarMensaje("\nTurno de " + j.getNombre());

            int puntosTurno = 0;
            boolean seguir = true;

            while (seguir) {
                List<Integer> tirada = modelo.lanzarDados();
                vista.mostrarTirada(tirada);
                int puntos = modelo.calcularPuntaje(tirada);

                if (puntos == 0) {
                    vista.mostrarMensaje("No obtuviste puntos. Pierdes el turno.");
                    puntosTurno = 0;
                    break;
                }

                puntosTurno += puntos;
                vista.mostrarPuntaje(j.getNombre(), puntosTurno);

                if (puntosTurno >= 10000) break;
                seguir = vista.pedirDecision().equals("s");
            }

            j.agregarPuntos(puntosTurno);
            vista.mostrarMensaje("Puntaje total de " + j.getNombre() + ": " + j.getPuntajeTotal());
            modelo.siguienteTurno();
        }

        vista.mostrarMensaje("\n¡Ganador: " + modelo.getGanador().getNombre() + "!");
    }
}

