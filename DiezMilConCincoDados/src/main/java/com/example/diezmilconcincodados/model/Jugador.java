package com.example.diezmilconcincodados.model;

public class Jugador
{
    private String nombre;
    private int puntajeTotal;

    public Jugador(String nombre)
    {
        this.nombre = nombre;
        this.puntajeTotal = 0;
    }

    public String getNombre()
    {
        return nombre;
    }

    public int getPuntajeTotal()
    {
        return puntajeTotal;
    }

    public void agregarPuntos(int puntos)
    {
        puntajeTotal += puntos;
    }

}
