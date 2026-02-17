package com.example.diezmilconcincodados.model;

import ar.edu.unlu.poo.rmimvc.observer.ObservableRemoto;
import java.rmi.RemoteException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Juego extends ObservableRemoto implements IJuego, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Jugador> jugadores = new ArrayList<>();
    private int indiceJugadorActual = 0;
    private Turno turnoActual;
    private final CuboDados cubo = new CuboDados();
    private boolean finalizado = false;

    public Juego() {
        // No necesitamos inicializar observadores manualmente
        // La librería lo hace por nosotros
    }

    public void agregarJugador(Jugador j) throws RemoteException {
        jugadores.add(j);
        notificarObservadores(null);  // ← cambio obligatorio de la librería
    }

    public boolean eliminarJugador(String nombre) throws RemoteException {
        if (nombre == null || nombre.isEmpty()) return false;
        int idxElim = -1;
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getNombre().equalsIgnoreCase(nombre)) {
                idxElim = i;
                break;
            }
        }
        if (idxElim == -1) return false;

        Jugador eliminado = jugadores.get(idxElim);
        jugadores.remove(idxElim);

        if (jugadores.isEmpty()) {
            turnoActual = null;
            indiceJugadorActual = 0;
        } else {
            if (turnoActual != null && turnoActual.getJugador().equals(eliminado)) {
                turnoActual = null;
            }
            if (idxElim < indiceJugadorActual) {
                indiceJugadorActual = Math.max(0, indiceJugadorActual - 1);
            } else if (idxElim == indiceJugadorActual) {
                indiceJugadorActual = indiceJugadorActual % jugadores.size();
            }
        }
        notificarObservadores(null);  // ← cambio obligatorio
        return true;
    }

    public List<Jugador> getJugadores() throws RemoteException {
        return new ArrayList<>(jugadores);
    }

    public Jugador getJugadorActual() throws RemoteException {
        if (jugadores.isEmpty()) {
            return null;
        }
        return jugadores.get(indiceJugadorActual);
    }

    public Jugador getGanador() throws RemoteException {
        if (!finalizado) return null;
        for (Jugador j : jugadores) {
            if (j.getPuntosTotales() >= 10000) {
                return j;
            }
        }
        return null;
    }

    public Turno getTurnoActual() throws RemoteException {
        return turnoActual;
    }

    public boolean iniciarNuevoTurno() throws RemoteException {
        if (finalizado) return false;
        if (jugadores.isEmpty()) {
            return false;
        }
        Jugador j = getJugadorActual();
        if (j == null) {
            return false;
        }
        turnoActual = new Turno(j);
        notificarObservadores(null);  // ← agregamos notificación aquí (antes no la tenía)
        return true;
    }

    public int lanzarTurnoActual() throws RemoteException {
        boolean hay = turnoActual.lanzar(cubo);
        notificarObservadores(null);  // ← cambio obligatorio
        if (!hay) {
            int perdidos = turnoActual.plantarse();
            avanzarTurnoSinSumar();
            return -1;
        }
        return 1;
    }

    public int aplicarSeleccionEnTurno(List<Integer> indicesSeleccionados) throws RemoteException {
        int puntos = turnoActual.aplicarSeleccion(indicesSeleccionados);
        if (puntos >= 0) {
            notificarObservadores(null);  // ← cambio obligatorio
        }
        return puntos;
    }

    public int plantarseEnTurno() throws RemoteException {
        int ganados = turnoActual.plantarse();
        Jugador j = turnoActual.getJugador();
        j.sumarPuntos(ganados);
        if (j.getPuntosTotales() >= 10000) {
            finalizado = true;
        } else {
            avanzarTurnoSinSumar();
        }
        notificarObservadores(null);  // ← cambio obligatorio
        return ganados;
    }

    private void avanzarTurnoSinSumar() throws RemoteException {
        if (jugadores.isEmpty()) {
            turnoActual = null;
            indiceJugadorActual = 0;
            return;
        }
        indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
        turnoActual = null;
        // NO llamamos notificar aquí → ya se llama en plantarseEnTurno() o lanzar
    }

    public boolean isFinalizado() throws RemoteException {
        return finalizado;
    }

    public void guardarPartida(File f) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(this);
        } catch (Exception ignored) {
        }
    }

    public static Juego cargarPartida(File f) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object o = ois.readObject();
            if (!(o instanceof Juego)) return null;
            return (Juego) o;
        } catch (Exception e) {
            return null;
        }
    }

    public void resetearPartida() throws RemoteException {
        for (Jugador j : jugadores) {
            j.resetearPuntos();  // asumiendo que Jugador tiene este método
        }
        turnoActual = null;
        indiceJugadorActual = 0;
        finalizado = false;
        notificarObservadores(null);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // No necesitamos reinicializar observadores → la librería lo maneja
    }
}