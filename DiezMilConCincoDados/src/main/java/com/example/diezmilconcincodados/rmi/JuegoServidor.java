package com.example.diezmilconcincodados.rmi;  // o tu paquete preferido

import ar.edu.unlu.poo.rmimvc.servidor.Servidor;
import com.example.diezmilconcincodados.model.Juego;
import java.rmi.RemoteException;

public class JuegoServidor {  // ← nombre nuevo

    public static void main(String[] args) {
        // Crea el modelo (servidor tiene la instancia real)
        Juego modelo = new Juego();

        // Crea el servidor RMI de la librería (usa el nombre completo para evitar conflicto)
        Servidor servidorRMI = new Servidor("localhost", 1099);  // puerto por defecto de RMI

        try {
            servidorRMI.iniciar(modelo);  // expone el modelo por RMI
            System.out.println("Servidor RMI iniciado en localhost:1099");
            System.out.println("Listo para que los clientes se conecten...");
            // El servidor queda escuchando indefinidamente
        } catch (RemoteException | ar.edu.unlu.poo.rmimvc.RMIMVCException e) {
            System.err.println("Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
