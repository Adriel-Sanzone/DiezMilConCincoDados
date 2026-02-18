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

    public boolean agregarJugador(Jugador j) throws RemoteException {
        if (j == null || j.getNombre() == null) return false;

        String nuevo = j.getNombre().trim();
        if (nuevo.isEmpty()) return false;

        for (Jugador existing : jugadores) {
            if (existing.getNombre().equalsIgnoreCase(nuevo)) {
                return false;
            }
        }

        jugadores.add(j);

        if (turnoActual == null && !jugadores.isEmpty()) {
            iniciarNuevoTurnoInterno();
        }

        notificarObservadores(null);
        return true;
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
                indiceJugadorActual = indiceJugadorActual % jugadores.size();
                iniciarNuevoTurnoInterno();
            } else if (idxElim < indiceJugadorActual) {
                indiceJugadorActual--;
            }
        }

        notificarObservadores(null);
        return true;
    }

    public List<Jugador> getJugadores() throws RemoteException {
        return new ArrayList<>(jugadores);
    }

    public Jugador getJugadorActual() throws RemoteException {
        if (jugadores.isEmpty()) return null;
        return jugadores.get(indiceJugadorActual);
    }

    public Turno getTurnoActual() throws RemoteException {
        return turnoActual;
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

    public boolean iniciarNuevoTurno() throws RemoteException {
        boolean ok = iniciarNuevoTurnoInterno();
        if (ok) notificarObservadores(null);
        return ok;
    }

    private boolean iniciarNuevoTurnoInterno() {
        if (finalizado || jugadores.isEmpty()) return false;
        turnoActual = new Turno(jugadores.get(indiceJugadorActual));
        return true;
    }

    public int lanzarTurnoActual() throws RemoteException {
        if (turnoActual == null) return -1;

        boolean hay = turnoActual.lanzar(cubo);

        if (!hay) {
            turnoActual.plantarse();
            avanzarTurnoInterno();
        }

        notificarObservadores(null);
        return hay ? 1 : -1;
    }

    public int aplicarSeleccionEnTurno(List<Integer> indicesSeleccionados) throws RemoteException {
        if (turnoActual == null) return -1;

        int puntos = turnoActual.aplicarSeleccion(indicesSeleccionados);
        if (puntos >= 0) {
            notificarObservadores(null);
        }
        return puntos;
    }

    public int plantarseEnTurno() throws RemoteException {
        if (turnoActual == null) return -1;

        int ganados = turnoActual.plantarse();
        Jugador j = turnoActual.getJugador();
        j.sumarPuntos(ganados);

        if (j.getPuntosTotales() >= 10000) {
            finalizado = true;
            turnoActual = null;
        } else {
            avanzarTurnoInterno();
        }

        notificarObservadores(null);
        return ganados;
    }

    private void avanzarTurnoInterno() {
        if (jugadores.isEmpty()) {
            turnoActual = null;
            indiceJugadorActual = 0;
            return;
        }

        indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
        iniciarNuevoTurnoInterno();
    }

    public boolean isFinalizado() throws RemoteException {
        return finalizado;
    }

    public void resetearPartida() throws RemoteException {
        for (Jugador j : jugadores) {
            j.resetearPuntos();
        }
        turnoActual = null;
        indiceJugadorActual = 0;
        finalizado = false;
        notificarObservadores(null);
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

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
