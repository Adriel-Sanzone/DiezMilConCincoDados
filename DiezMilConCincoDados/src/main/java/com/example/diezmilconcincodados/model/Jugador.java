package com.example.diezmilconcincodados.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final int id;
    private final String nombre;
    private int puntosTotales;

    public Jugador(String nombre) {
        this.id = NEXT_ID.getAndIncrement();
        this.nombre = nombre;
        this.puntosTotales = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public void sumarPuntos(int puntos) {
        if (puntos < 0) throw new IllegalArgumentException("puntos no puede ser negativo");
        this.puntosTotales += puntos;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", puntosTotales=" + puntosTotales +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jugador)) return false;
        Jugador jugador = (Jugador) o;
        return id == jugador.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
