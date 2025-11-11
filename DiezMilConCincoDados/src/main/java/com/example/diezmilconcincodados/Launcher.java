package com.example.diezmilconcincodados;

import com.example.diezmilconcincodados.controller.ControladorJuego;
import com.example.diezmilconcincodados.model.JuegoDiezMil;
import com.example.diezmilconcincodados.model.Jugador;
import com.example.diezmilconcincodados.view.VistaConsola;

import java.util.ArrayList;
import java.util.List;

public class Launcher
{
    public static void main(String[] args)
    {
        List<Jugador> jugadores = new ArrayList<>();

        jugadores.add(new Jugador("Pedro"));
        jugadores.add(new Jugador("Juan"));
        jugadores.add(new Jugador("Manuel"));

        JuegoDiezMil modelo = new JuegoDiezMil(jugadores);
        VistaConsola vista = new VistaConsola();
        ControladorJuego controlador = new ControladorJuego(modelo, vista);

        controlador.iniciar();
    }
}
