package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Jugador implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private int puntajeTotal;

    public Jugador(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
        this.puntajeTotal = 0;
    }

    public void sumarPuntos(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException("Los puntos a sumar no pueden ser negativos.");
        }
        this.puntajeTotal += puntos;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Jugador)) return false;
        Jugador jugador = (Jugador) otro;
        return nombre.equalsIgnoreCase(jugador.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }
}
