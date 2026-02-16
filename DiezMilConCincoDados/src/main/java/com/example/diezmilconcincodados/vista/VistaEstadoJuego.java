package com.example.diezmilconcincodados.vista;

import java.util.List;

public class VistaEstadoJuego {

    public void mostrarEstadoJuego(
            List<String> nombresJugadores,
            List<Integer> puntosJugadores,
            String nombreJugadorActual,
            int puntosAcumuladosTurno,
            int dadosRestantes,
            int[] ultimoLanzamiento,
            int ultimoLanzamientoCount,
            boolean hayTurnoActivo
    ) {
        System.out.println("\n--------------------------------");
        System.out.println(" \u001B[46m \u001B[1m\u001B[30mEstado del juego \u001B[0m ");
        System.out.println("--------------------------------");

        if (nombresJugadores.isEmpty()) {
            System.out.println("No hay jugadores aún.");
        } else {
            for (int i = 0; i < nombresJugadores.size(); i++) {
                System.out.printf("%d) \u001B[34m%s\u001B[0m -> \u001B[32m%d\u001B[0m puntos%n",
                        i + 1, nombresJugadores.get(i), puntosJugadores.get(i));
            }
        }

        if (hayTurnoActivo) {
            System.out.println("Turno en curso de: \u001B[34m" + nombreJugadorActual + "\u001B[0m");
            System.out.println("Dados restantes en el turno: " + dadosRestantes);

            if (ultimoLanzamientoCount == 0) {
                System.out.print("(no hay lanzamiento)\n");
            } else {
                System.out.println("(lanzamiento disponible - ver abajo)");
            }

            System.out.println("\nPuntos acumulados en turno: \u001B[32m" + puntosAcumuladosTurno + "\u001B[0m");
        } else {
            System.out.println("No hay turno iniciado actualmente.");
            System.out.println("Próximo jugador: " + (nombresJugadores.isEmpty() ? "N/A" : nombreJugadorActual));
        }
    }
}

