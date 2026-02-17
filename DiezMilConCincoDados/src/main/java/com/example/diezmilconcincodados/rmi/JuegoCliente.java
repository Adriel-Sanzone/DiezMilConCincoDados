package com.example.diezmilconcincodados.rmi;

import ar.edu.unlu.poo.rmimvc.cliente.Cliente;
import com.example.diezmilconcincodados.controlador.ControladorConsolaMain;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;
import com.example.diezmilconcincodados.vista.VistaMenuConsola;
import java.rmi.RemoteException;

public class JuegoCliente {  // ← nombre nuevo

    public static void main(String[] args) throws RemoteException {
        VistaMenuConsola vistaMenu = new VistaMenuConsola();
        VistaJuegoConsola vistaJuego = new VistaJuegoConsola();

        ControladorConsolaMain controlador = new ControladorConsolaMain(null, vistaMenu, vistaJuego);

        Cliente clienteRMI = new Cliente("localhost", 0, "localhost", 1099);  // puerto cliente 0 = automático

        try {
            clienteRMI.iniciar(controlador);  // conecta y enlaza el controlador con el modelo remoto
            System.out.println("Cliente conectado al servidor");
            controlador.actualizar(null, null);
            controlador.ejecutarLoopPrincipal();
        } catch (RemoteException | ar.edu.unlu.poo.rmimvc.RMIMVCException e) {
            System.err.println("Error al conectar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
