import java.util.ArrayList;

public class Habitacion
{

    //Atributos
    private String nombre;
    private ArrayList<Luz> luces;

    //Constructor
    public Habitacion(String nombre)
    {
        this.nombre = nombre;
        this.luces = new ArrayList<>();
    }

    //Metodos
    public void AgregarLuz(Luz luz)
    {
        luces.add(luz);
    }

    public void PrenderTodas()
    {
        for (Luz l : luces)
        {
            l.Encender();
        }
    }

    public void ApagarTodas()
    {
        for (Luz l : luces)
        {
            l.Apagar();
        }
    }


}
