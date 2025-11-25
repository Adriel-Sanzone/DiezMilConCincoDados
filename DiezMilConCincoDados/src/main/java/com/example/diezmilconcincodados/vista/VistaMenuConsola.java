package com.example.diezmilconcincodados.vista;

import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.vista.VistaEstadoJuego;

import java.util.Scanner;

public class VistaMenuConsola {
    private final Scanner sc = new Scanner(System.in);
    private final VistaEstadoJuego estadoView = new VistaEstadoJuego();

    public int mostrarMenuPrincipal() {
        System.out.println("\n--------------------------------");
        System.out.println("    DIEZ MIL CON CINCO DADOS    ");
        System.out.println("--------------------------------");
        System.out.println("[1] Iniciar/Continuar partida");
        System.out.println("[2] Agregar jugador");
        System.out.println("[3] Eliminar jugador");
        System.out.println("[4] Ver estado de partida");
        System.out.println("[5] Guardar partida");
        System.out.println("[6] Cargar partida");
        System.out.println("[7] Instrucciones");
        System.out.println("[0] Salir");
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

    public void mostrarEstadoJuego(Juego juego) {
        estadoView.mostrarEstadoJuego(juego);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarInstrucciones() {
        System.out.println("================================================================");
        System.out.println("  --- INSTRUCCIONES DEL JUEGO ---            ");
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
