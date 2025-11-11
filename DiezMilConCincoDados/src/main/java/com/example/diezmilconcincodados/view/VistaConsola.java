package com.example.diezmilconcincodados.view;

import java.util.List;
import java.util.Scanner;

public class VistaConsola
{
    private Scanner sc = new Scanner(System.in);

    public void mostrarMensaje(String m)
    {
        System.out.println(m);
    }

    public void mostrarTirada(List<Integer> tirada)
    {
        System.out.print("Dados: ");
        for (int i = 0; i < tirada.size(); i++)
        {
            System.out.print("[" + tirada.get(i) + "] ");
        }
        System.out.println();
    }

    public String pedirDecision()
    {
        String respuesta = new String();
        boolean flag = true;

        while (flag)
        {
            System.out.printf("¿Deseas seguir lanzando (S/N)? \n>> ");
            respuesta = sc.nextLine().trim().toLowerCase();

            if (respuesta.equals("s") || respuesta.equals("n"))
            {
                flag = false;
            }
            System.out.println("ERROR: comando invalido");
        }

        return respuesta;
    }

    public void mostrarPuntaje(String jugador, int puntos)
    {
        System.out.println(jugador + " obtiene " + puntos + " puntos en este turno.");
    }
}

