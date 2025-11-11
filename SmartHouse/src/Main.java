import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);

        //Pruebo luces
        System.out.print("\nCreando primera lampara...\n");

        Luz lampara1 = new Luz("Living");

        lampara1.Encender();
        lampara1.Apagar();

        lampara1.CambiarColor();

        lampara1.CambiarIntensidad();
        lampara1.Encender();

        //Pruebo habitaciones
        System.out.print("\n\nCreando primera habitacion...\n");
        Habitacion sala = new Habitacion("Sala de estar");

        Luz luz1 = new Luz("Lampara de pie");
        Luz luz2 = new Luz("Luz de techo");
        Luz luz3 = new Luz("Luz de escritorio");

        sala.AgregarLuz(luz1);
        sala.AgregarLuz(luz2);
        sala.AgregarLuz(luz3);

        System.out.print("\nPrendiento todas...\n");
        sala.PrenderTodas();
        System.out.print("\n\nApagando todas...\n");
        sala.ApagarTodas();



    }
}
