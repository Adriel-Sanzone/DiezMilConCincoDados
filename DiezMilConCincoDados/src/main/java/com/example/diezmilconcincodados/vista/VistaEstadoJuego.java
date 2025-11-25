package com.example.diezmilconcincodados.vista;

import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.model.Turno;

public class VistaEstadoJuego {

    public void mostrarEstadoJuego(Juego juego) {
        System.out.println("\n--------------------------------");
        System.out.println("      \u001B[46m  \u001B[1m\u001B[30mEstado del juego  \u001B[0m    ");
        System.out.println("--------------------------------");
        int i = 1;
        for (var j : juego.getJugadores()) {
            System.out.printf("%d) \u001B[34m%s\u001B[0m -> \u001B[32m%d\u001B[0m puntos%n", i++, j.getNombre(), j.getPuntosTotales());
        }

        Turno turno = juego.getTurnoActual();
        if (turno != null) {
            System.out.println("Turno en curso de: \u001B[34m" + juego.getJugadorActual().getNombre() + "\u001B[0m");
            System.out.println("Dados restantes en el turno: " + turno.getDadosRestantes());

            int[] ult = turno.getUltimoLanzamiento();
            int cantidad = turno.getUltimoLanzamientoCount();
            if (cantidad == 0) {
                System.out.print("(no hay lanzamiento)\n");
            } else {
                System.out.println("\u001B[41m \u001B[1m\u001B[30mDADOS \u001B[0m ");
                for (int k = 0; k < cantidad; k++) {
                    System.out.print("[" + ult[k] + "] ");
                }
                System.out.println("");
                for (int j = 0; j < cantidad; j++) {
                    System.out.print(" " + (j+1) + "  ");
                }
                System.out.println();
            }
            System.out.println("\nPuntos acumulados en turno: \u001B[32m" + turno.getPuntosAcumuladosTurno() + "\u001B[0m");
        } else {
            System.out.println("No hay turno iniciado actualmente.");
            System.out.println("Próximo jugador: " + (juego.getJugadores().isEmpty() ? "N/A" : juego.getJugadorActual().getNombre()));
        }
    }
}

