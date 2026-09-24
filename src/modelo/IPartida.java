package modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public interface IPartida extends IObservableRemoto {

    int PUNTOS_PARA_GANAR = 10000;
    int MIN_JUGADORES = 2;

    void agregarJugador(String nombre) throws RemoteException;

    void iniciar() throws RemoteException;

    void tirarDados(String jugador) throws RemoteException;

    void seleccionarDados(String jugador, List<Integer> indicesSeleccionados) throws RemoteException;

    void pasarTurno(String jugador) throws RemoteException;

    void plantarse(String jugador) throws RemoteException;

    boolean hayCombinacionesPosibles() throws RemoteException;

    EstadoPartida getEstado() throws RemoteException;

    List<Jugador> getJugadores() throws RemoteException;

    Jugador getJugadorActual() throws RemoteException;

    int getPuntosAcumuladosTurno() throws RemoteException;

    int getCantidadTiradasTurno() throws RemoteException;

    List<Integer> getValoresDeLosDados() throws RemoteException;

    List<Boolean> getReservasDeLosDados() throws RemoteException;

    int cantidadDadosDisponibles() throws RemoteException;

    ResultadoTirada getUltimoResultado() throws RemoteException;

    Jugador getGanador() throws RemoteException;

    FaseTurno getFaseTurno() throws RemoteException;

    void guardar(String nombreArchivo) throws RemoteException, IOException;

    List<String> listarPartidasGuardadas() throws RemoteException;
}
