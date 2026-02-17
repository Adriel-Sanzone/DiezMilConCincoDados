package com.example.diezmilconcincodados.model;

import com.example.diezmilconcincodados.observer.Observable;
import com.example.diezmilconcincodados.observer.Observador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Juego implements Observable, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Jugador> jugadores = new ArrayList<>();
    private transient List<Observador> observadores = new ArrayList<>();

    private int indiceJugadorActual = 0;
    private Turno turnoActual;
    private final CuboDados cubo = new CuboDados();
    private boolean finalizado = false;

    public Juego()
    {
        observadores = new ArrayList<>();
    }

    public void agregarJugador(Jugador j)
    {
        jugadores.add(j);
        notificarObservadores();
    }

    public boolean eliminarJugador(String nombre)
    {
        if (nombre == null || nombre.isEmpty()) return false;
        int idxElim = -1;

        for (int i = 0; i < jugadores.size(); i++)
        {
            if (jugadores.get(i).getNombre().equalsIgnoreCase(nombre))
            {
                idxElim = i;
                break;
            }
        }
        if (idxElim == -1) return false;

        Jugador eliminado = jugadores.get(idxElim);
        jugadores.remove(idxElim);

        if (jugadores.isEmpty())
        {
            turnoActual = null;
            indiceJugadorActual = 0;
        } else
        {
            if (turnoActual != null && turnoActual.getJugador().equals(eliminado))
            {
                turnoActual = null;
            }
            if (idxElim < indiceJugadorActual)
            {
                indiceJugadorActual = Math.max(0, indiceJugadorActual - 1);
            } else if (idxElim == indiceJugadorActual)
            {
                indiceJugadorActual = indiceJugadorActual % jugadores.size();
            }
        }
        notificarObservadores();

        return true;
    }

    public List<Jugador> getJugadores()
    {
        return new ArrayList<>(jugadores);
    }

    public Jugador getJugadorActual() {
        if (jugadores.isEmpty()) {
            return null;
        }
        return jugadores.get(indiceJugadorActual);
    }

    public Jugador getGanador()
    {
        if (!finalizado) return null;
        for (Jugador j : jugadores)
        {
            if (j.getPuntosTotales() >= 10000)
            {
                return j;
            }
        }
        return null;
    }

    public Turno getTurnoActual()
    {
        return turnoActual;
    }

    public boolean iniciarNuevoTurno() {
        if (finalizado) return false;
        if (jugadores.isEmpty()) {

            return false;
        }
        Jugador j = getJugadorActual();
        if (j == null) {
            return false;
        }
        turnoActual = new Turno(j);

        return true;
    }

    public int lanzarTurnoActual()
    {
        boolean hay = turnoActual.lanzar(cubo);

        notificarObservadores();
        if (!hay)
        {
            int perdidos = turnoActual.plantarse();
            avanzarTurnoSinSumar();
            return -1;
        }
        return 1;
    }

    public int aplicarSeleccionEnTurno(List<Integer> indicesSeleccionados)
    {
        int puntos = turnoActual.aplicarSeleccion(indicesSeleccionados);
        if (puntos >= 0) {

            notificarObservadores();
        }
        return puntos;
    }

    public int plantarseEnTurno()
    {
        int ganados = turnoActual.plantarse();
        Jugador j = turnoActual.getJugador();
        j.sumarPuntos(ganados);

        if (j.getPuntosTotales() >= 10000)
        {
            finalizado = true;
        } else
        {
            avanzarTurnoSinSumar();
        }

        notificarObservadores();
        return ganados;
    }

    private void avanzarTurnoSinSumar()
    {
        if (jugadores.isEmpty())
        {
            turnoActual = null;
            indiceJugadorActual = 0;
            notificarObservadores();
            return;
        }
        indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
        turnoActual = null;

    }

    public boolean isFinalizado()
    {
        return finalizado;
    }

    public void guardarPartida(File f)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f)))
        {
            oos.writeObject(this);
        } catch (Exception ignored) {
        }
    }

    public static Juego cargarPartida(File f)
    {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f)))
        {
            Object o = ois.readObject();
            if (!(o instanceof Juego)) return null;

            Juego j = (Juego) o;
            return j;

        } catch (Exception e)
        {
            return null;
        }
    }

    public void resetearPartida() {
        for (Jugador j : jugadores)
        {
            j.resetearPuntos();
        }
        turnoActual = null;
        indiceJugadorActual = 0;
        finalizado = false;
        notificarObservadores();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        observadores = new ArrayList<>();
    }

    @Override
    public void agregarObservador(Observador observador)
    {
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(Observador observador)
    {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores()
    {
        List<Observador> copia = new ArrayList<>(observadores);
        for (Observador o : copia)
        {
            o.actualizar();
        }
    }
}