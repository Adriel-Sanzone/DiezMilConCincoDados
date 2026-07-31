package modelo;

import java.io.Serializable;

public class Turno implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Jugador jugador;
    private int puntosAcumulados;
    private int cantidadDeTiradas;
    private FaseTurno fase;

    public Turno(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador del turno no puede ser nulo.");
        }
        this.jugador = jugador;
        this.puntosAcumulados = 0;
        this.cantidadDeTiradas = 0;
        this.fase = FaseTurno.INICIAL;
    }

    public void registrarTirada(int puntosObtenidos) {
        if (puntosObtenidos < 0) {
            throw new IllegalArgumentException("Los puntos obtenidos no pueden ser negativos.");
        }
        this.puntosAcumulados += puntosObtenidos;
        this.cantidadDeTiradas++;
    }

    public void perder() {
        this.puntosAcumulados = 0;
    }

    public boolean esPrimeraTirada() {
        return cantidadDeTiradas == 0;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public int getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public int getCantidadDeTiradas() {
        return cantidadDeTiradas;
    }

    public FaseTurno getFase() {
        return fase;
    }

    public void cambiarFase(FaseTurno nuevaFase) {
        if (nuevaFase == null) {
            throw new IllegalArgumentException("La fase del turno no puede ser nula.");
        }
        this.fase = nuevaFase;
    }
}
