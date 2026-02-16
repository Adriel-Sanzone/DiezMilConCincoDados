package com.example.diezmilconcincodados.vista;

import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.vista.VistaEstadoJuego;

import java.util.List;
import java.util.Scanner;

public class VistaMenuConsola {
    private final Scanner sc = new Scanner(System.in);
    private final VistaEstadoJuego estadoView = new VistaEstadoJuego();

    public int mostrarMenuPrincipal() {
        System.out.println("\n--------------------------------");
        System.out.println("  \u001B[42m  \u001B[1m\u001B[30mDIEZ MIL CON CINCO DADOS  \u001B[0m  ");
        System.out.println("--------------------------------");
        System.out.println("\u001B[1m\u001B[32m[1]\u001B[0m Iniciar/Continuar partida");
        System.out.println("\u001B[1m\u001B[32m[2]\u001B[0m Agregar jugador");
        System.out.println("\u001B[1m\u001B[32m[3]\u001B[0m Eliminar jugador");
        System.out.println("\u001B[1m\u001B[32m[4]\u001B[0m Ver estado de partida");
        System.out.println("\u001B[1m\u001B[32m[5]\u001B[0m Guardar partida");
        System.out.println("\u001B[1m\u001B[32m[6]\u001B[0m Cargar partida");
        System.out.println("\u001B[1m\u001B[32m[7]\u001B[0m Instrucciones");
        System.out.println("\u001B[1m\u001B[32m[0]\u001B[0m Salir");
        System.out.print(">> ");
        return leerEnteroSeguro();
    }

    public String solicitarNombreJugador() {
        System.out.print("Nombre del jugador: ");
        return sc.nextLine().trim();
    }

    public String solicitarNombreJugadorEliminar() {
        System.out.print("Nombre del jugador a eliminar: ");
        return sc.nextLine().trim();
    }

    public String solicitarNombreArchivoGuardar() {
        System.out.print("Nombre archivo para guardar (ej: partida.b):\n>> ");
        String n = sc.nextLine().trim();
        if (n.isEmpty()) n = "partida.b";
        return n;
    }

    public String solicitarNombreArchivoCargar() {
        System.out.print("Nombre archivo para cargar (ej: partida.b):\n>> ");
        String n = sc.nextLine().trim();
        if (n.isEmpty()) n = "partida.b";
        return n;
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
        estadoView.mostrarEstadoJuego(
                nombresJugadores,
                puntosJugadores,
                nombreJugadorActual,
                puntosAcumuladosTurno,
                dadosRestantes,
                ultimoLanzamiento,
                ultimoLanzamientoCount,
                hayTurnoActivo
        );
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
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
