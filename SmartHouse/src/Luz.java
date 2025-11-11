import java.util.Scanner;

public class Luz
{
    //Atributos
    private String nombre;
    private int potencia;
    private boolean encendida;
    private Colores color;

    //Constructor
    public Luz(String nombre)
    {
        this.nombre = nombre;
        this.potencia = 100;
        this.encendida = false;
        this.color = Colores.BLANCO;
    }

    //Metodos
    final String RESET = "\u001B[0m";

    public void Encender()
    {
        String colorActual = "";

        switch (this.color)
        {
            //Codigo de colores ANSI
            case ROJO: colorActual = "\u001B[1;31m"; break;
            case VERDE: colorActual = "\u001B[1;32m"; break;
            case AZUL: colorActual = "\u001B[1;34m"; break;
            case BLANCO: colorActual = "\u001B[1;37m"; break;
        }

        encendida = true;
        System.out.printf("\n%s %sEncendida%s (%d %%)", this.nombre, colorActual, RESET, this.potencia);
    }

    public void Apagar()
    {
        String NEGRO = "\u001B[1;30m";
        encendida = false;
        System.out.printf("\n%s %sApagada%s", nombre, NEGRO, RESET);
    }

    public void CambiarColor()
    {
        Scanner sc = new Scanner(System.in);

        System.out.print("\n\n¿A que color queres cambiar la luz? \n\n-BLANCO\n-ROJO\n-VERDE\n-AZUL\n>> ");
        String entrada = sc.nextLine().toUpperCase();  // paso a mayúsculas para que coincida con el enum

        this.color = Colores.valueOf(entrada);

        String colorActual = "";

            switch (this.color)
            {
                case ROJO:
                    colorActual = "\u001B[0;31m";
                    break;
                case VERDE:
                    colorActual = "\u001B[0;32m";
                    break;
                case AZUL:
                    colorActual = "\u001B[0;34m";
                    break;
                case BLANCO:
                    colorActual = "\u001B[0;37m";
                    break;
            }

        System.out.printf("\nColor de %s cambiada a %s%s%s", nombre, colorActual, this.color, RESET);

    }

    public void CambiarIntensidad()
    {
        Scanner sc = new Scanner(System.in);

        System.out.printf("\n¿Que nivel de intensidad desea desea establecer la lampara %s? (1-100)\n>> " , this.nombre);

        boolean bandera = true;

        while(bandera)
        {
            int entrada = sc.nextInt();
            if(entrada < 1 || entrada > 100)
            {
                System.out.printf("\nVariable fuera de rango, reintentar...\n>>  ");
            } else
            {
                this.potencia = entrada;
                bandera = false;
            }
        }
    }
}
