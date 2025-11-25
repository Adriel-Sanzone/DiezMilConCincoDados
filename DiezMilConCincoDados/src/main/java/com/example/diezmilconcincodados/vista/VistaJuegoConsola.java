package com.example.diezmilconcincodados.vista;

import com.example.diezmilconcincodados.vista.VistaEstadoJuego;
import com.example.diezmilconcincodados.model.Juego;

import java.util.Scanner;

public class VistaJuegoConsola {
    private final Scanner sc = new Scanner(System.in);
    private final VistaEstadoJuego estadoView = new VistaEstadoJuego();

    public int mostrarMenuJugador(String nombreJugador) {
        System.out.println("\n--------------------------------");
        System.out.println("    TURNO DE " + nombreJugador);
        System.out.println("--------------------------------");
        System.out.println("[1] Lanzar dados");
        System.out.println("[2] Seleccionar dados puntuables");
        System.out.println("[3] Plantarse (guardar puntos de turno)");
        System.out.println("[4] Mostrar estado del juego");
        System.out.println("[5] Volver al menú principal (pausar partida)");
        System.out.print(">> ");
        return leerEnteroSeguro();
    }

    public void mostrarDadosFormateados(int[] dados, int cantidad) {
        System.out.println("---------------------");
        System.out.println("DADOS ");

        for (int i = 0; i < cantidad; i++) {
            System.out.print("[" + dados[i] + "]\t");
        }
        System.out.println();
        System.out.print(" ");
        for (int i = 0; i < cantidad; i++) {
            System.out.print((i + 1) + "\t ");
        }
        System.out.println("\n---------------------");
    }

    public int[] leerIndicesSeleccion() {
        System.out.print("Ingrese índices de dados separados por coma (ej: 1,3) o vacío para cancelar:\n>> ");
        String linea = sc.nextLine().trim();
        if (linea.isEmpty()) return new int[0];
        String[] parts = linea.split(",");
        int[] indices = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                int parsed = Integer.parseInt(parts[i].trim());
                indices[i] = parsed - 1;
            } catch (NumberFormatException e) {
                indices[i] = -1;
            }
        }
        return indices;
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarEstadoJuego(Juego juego) {
        estadoView.mostrarEstadoJuego(juego);
    }

    private int leerEnteroSeguro() {
        while (true) {
            String s = sc.nextLine();
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                System.out.print("Entrada no válida. Ingrese un número:\n>> ");
            }
        }
    }
}
