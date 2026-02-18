package com.example.diezmilconcincodados.rmi;

import ar.edu.unlu.poo.rmimvc.cliente.Cliente;
import com.example.diezmilconcincodados.controlador.ControladorConsolaEnJuego;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;

import java.rmi.RemoteException;
import java.util.Scanner;

public class JuegoCliente {

    public static void main(String[] args) throws RemoteException {
        VistaJuegoConsola vistaJuego = new VistaJuegoConsola();

        ControladorConsolaEnJuego controlador = new ControladorConsolaEnJuego(null, vistaJuego);

        Cliente clienteRMI = new Cliente("localhost", 0, "localhost", 1099);

        try {
            clienteRMI.iniciar(controlador);  // enlaza el controlador con el modelo remoto
            System.out.println("Cliente conectado al servidor");

            // pide nombre y registra
            Scanner sc = new Scanner(System.in);
            boolean registrado = false;
            String nombre = null;
            while (!registrado) {
                System.out.print("Ingrese su nombre de jugador: ");
                nombre = sc.nextLine().trim();
                if (nombre.isEmpty()) {
                    System.out.println("Nombre vacío. Intente de nuevo.");
                    continue;
                }
                controlador.setNombreLocal(nombre);
                registrado = controlador.registrarJugadorLocal(nombre);
                if (!registrado) {
                    System.out.println("Nombre ya en uso o error. Intente con otro nombre.");
                }
            }

            System.out.println("Registrado como: " + nombre + ". Iniciando partida...");
            controlador.ejecutarLoopPartida();


        } catch (RemoteException | ar.edu.unlu.poo.rmimvc.RMIMVCException e) {
            System.err.println("Error al conectar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
