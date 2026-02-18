package com.example.diezmilconcincodados.vista;

import com.example.diezmilconcincodados.vista.VistaEstadoJuego;
import java.util.List;
import java.util.Scanner;

public class VistaJuegoConsola {
    private final Scanner sc = new Scanner(System.in);
    private final VistaEstadoJuego estadoView = new VistaEstadoJuego();

    private void imprimirMenuHeader(String nombreJugador) {
        System.out.println("\n--------------------------------");
        System.out.println("    \u001B[4m\u001B[1mTURNO DE \u001B[34m" + nombreJugador + "\u001B[0m");
        System.out.println("--------------------------------");
        System.out.println("\u001B[1m\u001B[32m[1]\u001B[0m Lanzar dados");
        System.out.println("\u001B[1m\u001B[32m[2]\u001B[0m Seleccionar dados puntuables");
        System.out.println("\u001B[1m\u001B[32m[3]\u001B[0m Plantarse (guardar puntos de turno)");
        System.out.println("\u001B[1m\u001B[32m[4]\u001B[0m Mostrar estado del juego");
        System.out.println("\u001B[1m\u001B[32m[5]\u001B[0m Ver instrucciones del juego");
    }

    public int mostrarMenuJugador(String nombreJugador)
    {
        imprimirMenuHeader(nombreJugador);
        System.out.print(">> ");
        return leerEnteroSeguro();
    }

    public void reimprimirMenuJugador(String nombreJugador) {
        imprimirMenuHeader(nombreJugador);
        System.out.print(">> ");
        System.out.flush();
    }

    public void mostrarDadosFormateados(int[] dados, int cantidad) {
        System.out.println("---------------------");
        System.out.println("\u001B[41m \u001B[1m\u001B[30mDADOS \u001B[0m ");

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

    public int[] leerIndicesSeleccion()
    {
        System.out.print("Ingrese índices de dados separados por coma (ej: 1,3) o vacío para cancelar:\n>> ");
        String linea = sc.nextLine().trim();

        if (linea.isEmpty()) return new int[0];

        String[] parts = linea.split(",");
        int[] indices = new int[parts.length];

        for (int i = 0; i < parts.length; i++)
        {
            try
            {
                int parsed = Integer.parseInt(parts[i].trim());
                indices[i] = parsed - 1;
            } catch (NumberFormatException e)
            {
                indices[i] = -1;
            }
        }
        return indices;
    }

    public void mostrarMensaje(String mensaje)
    {
        System.out.println(mensaje);
    }

    public void mostrarEstadoJuegoActualizado(
            List<String> nombresJugadores,
            List<Integer> puntosJugadores,
            String nombreJugadorActual,
            int puntosAcumuladosTurno,
            int dadosRestantes,
            int[] ultimoLanzamiento,
            int ultimoLanzamientoCount,
            boolean hayTurnoActivo
    ) {
        estadoView.mostrarEstadoJuego(nombresJugadores, puntosJugadores, nombreJugadorActual, puntosAcumuladosTurno, dadosRestantes, ultimoLanzamiento, ultimoLanzamientoCount, hayTurnoActivo);
    }

    public void mostrarInstrucciones() {
        System.out.println("================================================================");
        System.out.println("  \u001B[44m  \u001B[1mINSTRUCCIONES DEL JUEGO  \u001B[0m              ");
        System.out.println("El objetivo es llegar a 10000 puntos.");
        System.out.println("En tu turno lanzas cinco dados. Solo puedes sumar puntos");
        System.out.println("si seleccionas combinaciones válidas:");
        System.out.println("- Un dado con valor 1 suma 100 puntos");
        System.out.println("- Un dado con valor 5 suma 50 puntos");
        System.out.println("- Tres dados iguales suman 100 × valor del dado");
        System.out.println("- Escalera (1-2-3-4-5) o (2-3-4-5-6) otorga 2000 puntos");
        System.out.println("Cada vez que seleccionas dados, esos se apartan y reduces");
        System.out.println("la cantidad de dados restantes para el siguiente lanzamiento.");
        System.out.println("Si luego de un lanzamiento no obtienes ninguna combinación");
        System.out.println("válida, pierdes los puntos acumulados en ese turno.");
        System.out.println("Puedes plantarte en cualquier momento para conservar");
        System.out.println("los puntos del turno.");
        System.out.println("================================================================");
    }

    private int leerEnteroSeguro()
    {
        while (true)
        {
            String s = sc.nextLine();
            try
            {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException e)
            {
                System.out.print("\u001B[31mERROR\u001B[0m: Debe ingresar un numero\n>> ");
            }
        }
    }
}
