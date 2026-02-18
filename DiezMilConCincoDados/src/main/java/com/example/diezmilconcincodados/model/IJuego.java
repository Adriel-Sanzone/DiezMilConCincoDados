package com.example.diezmilconcincodados.model;

import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;
import java.rmi.RemoteException;
import java.util.List;

public interface IJuego extends IObservableRemoto {

    boolean agregarJugador(Jugador j) throws RemoteException;
    boolean eliminarJugador(String nombre) throws RemoteException;
    List<Jugador> getJugadores() throws RemoteException;
    Jugador getJugadorActual() throws RemoteException;
    Jugador getGanador() throws RemoteException;
    Turno getTurnoActual() throws RemoteException;
    boolean iniciarNuevoTurno() throws RemoteException;
    int lanzarTurnoActual() throws RemoteException;
    int aplicarSeleccionEnTurno(List<Integer> indicesSeleccionados) throws RemoteException;
    int plantarseEnTurno() throws RemoteException;
    boolean isFinalizado() throws RemoteException;
    void resetearPartida() throws RemoteException;

}
