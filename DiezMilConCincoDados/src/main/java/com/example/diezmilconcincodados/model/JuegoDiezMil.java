package com.example.diezmilconcincodados.model;

import java.util.ArrayList;
import java.util.List;



public class JuegoDiezMil {
    private List<Dado> dados;
    private List<Jugador> jugadores;
    private int turnoActual;
    private final int PUNTAJE_META = 10000;

    public JuegoDiezMil(List<Jugador> jugadores)
    {
        this.jugadores = jugadores;
        this.turnoActual = 0;
        this.dados = new ArrayList<Dado>();
        for (int i = 0; i < 5; i++)
        {
            this.dados.add(new Dado());
        }
    }

    public Jugador getJugadorActual()
    {
        return jugadores.get(turnoActual);
    }

    public boolean hayGanador()
    {
        for (int i = 0; i < jugadores.size(); i++)
        {
            Jugador j = jugadores.get(i);
            if (j.getPuntajeTotal() >= PUNTAJE_META)
            {
                return true;
            }
        }
        return false;
    }

    public Jugador getGanador()
    {
        Jugador ganador = jugadores.get(0);
        for (int i = 1; i < jugadores.size(); i++)
        {
            Jugador actual = jugadores.get(i);
            if (actual.getPuntajeTotal() > ganador.getPuntajeTotal())
            {
                ganador = actual;
            }
        }
        return ganador;
    }

    public List<Integer> lanzarDados()
    {
        List<Integer> valores = new ArrayList<Integer>();
        for (int i = 0; i < dados.size(); i++)
        {
            Dado d = dados.get(i);
            d.lanzar();
            valores.add(d.getValor());
        }
        return valores;
    }

    public int calcularPuntaje(List<Integer> tirada)
    {
        int[] cont = new int[7];
        for (int i = 0; i < tirada.size(); i++)
        {
            int valor = tirada.get(i);
            cont[valor]++;
        }

        int puntos = 0;

        if (cont[1] == 5)
        {
            return 10000;
        }

        for (int i = 1; i <= 6; i++)
        {
            if (cont[i] >= 3)
            {
                if (i == 1)
                    puntos += 1000;
                else
                    puntos += i * 100;
                cont[i] -= 3;
            }
        }

        puntos += cont[1] * 100;
        puntos += cont[5] * 50;

        boolean escalera1 = true;
        boolean escalera2 = true;
        boolean escalera3 = true;

        for (int i = 1; i <= 5; i++)
        {
            if (contiene(tirada, i) == false) escalera1 = false;
        }

        for (int i = 2; i <= 6; i++)
        {
            if (contiene(tirada, i) == false) escalera2 = false;
        }

        if (!(contiene(tirada, 1) && contiene(tirada, 3) && contiene(tirada, 4) && contiene(tirada, 5) && contiene(tirada, 6)))
        {
            escalera3 = false;
        }

        if (escalera1 || escalera2 || escalera3) {
            puntos += 500;
        }

        return puntos;
    }

    private boolean contiene(List<Integer> tirada, int valor)
    {
        for (int i = 0; i < tirada.size(); i++)
        {
            if (tirada.get(i) == valor)
            {
                return true;
            }
        }
        return false;
    }

    public void siguienteTurno()
    {
        turnoActual++;
        if (turnoActual >= jugadores.size())
        {
            turnoActual = 0;
        }
    }
}
