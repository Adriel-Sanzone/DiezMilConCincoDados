package com.example.diezmilconcincodados.rmi;

import ar.edu.unlu.poo.rmimvc.servidor.Servidor;
import com.example.diezmilconcincodados.model.Juego;
import java.rmi.RemoteException;

public class JuegoServidor {

    public static void main(String[] args) {

        Juego modelo = new Juego();

        Servidor servidorRMI = new Servidor("localhost", 1099);

        try {
            servidorRMI.iniciar(modelo);
            System.out.println("Servidor RMI iniciado en localhost:1099");
            System.out.println("Listo para que los clientes se conecten...");

        } catch (RemoteException | ar.edu.unlu.poo.rmimvc.RMIMVCException e) {
            System.err.println("Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
