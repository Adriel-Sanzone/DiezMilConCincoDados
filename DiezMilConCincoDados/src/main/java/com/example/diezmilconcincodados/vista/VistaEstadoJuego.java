package com.example.diezmilconcincodados.vista;

import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.model.Turno;

public class VistaEstadoJuego {

    public void mostrarEstadoJuego(Juego juego) {
        System.out.println("\n--------------------------------");
        System.out.println("        Estado del juego        ");
        System.out.println("--------------------------------");
        int i = 1;
        for (var j : juego.getJugadores()) {
            System.out.printf("%d) %s -> %d puntos%n", i++, j.getNombre(), j.getPuntosTotales());
        }

        Turno turno = juego.getTurnoActual();
        if (turno != null) {
            System.out.println("Turno en curso de: " + juego.getJugadorActual().getNombre());
            System.out.println("Dados restantes en el turno: " + turno.getDadosRestantes());

            int[] ult = turno.getUltimoLanzamiento();
            int cantidad = turno.getUltimoLanzamientoCount();
            if (cantidad == 0) {
                System.out.print("(no hay lanzamiento)\n");
            } else {
                System.out.println("Último lanzamiento:");
                // Mostrar dados formateados simples
                for (int k = 0; k < cantidad; k++) {
                    System.out.print("[" + ult[k] + "] ");
                }
                System.out.println();
            }
            System.out.println("Puntos acumulados en turno: " + turno.getPuntosAcumuladosTurno());
        } else {
            System.out.println("No hay turno iniciado actualmente.");
            System.out.println("Próximo jugador: " + (juego.getJugadores().isEmpty() ? "N/A" : juego.getJugadorActual().getNombre()));
        }
    }
}

