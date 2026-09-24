import ar.edu.unlu.rmimvc.servidor.Servidor;
import modelo.Partida;
import persistencia.GestorPartida;

import java.util.List;
import java.util.Scanner;

public class ServidorDiezMil {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Servidor Diez Mil con cinco dados ===");
        Partida partida = elegirPartida(scanner);

        Servidor servidor = new Servidor(
                ConfiguracionRed.IP_SERVIDOR, ConfiguracionRed.PUERTO_SERVIDOR);
        try {
            servidor.iniciar(partida);
        } catch (Exception e) {
            System.out.println("ERROR: no se pudo iniciar el servidor: " + e.getMessage());
            return;
        }

        System.out.println();
        System.out.println("Servidor escuchando en " + ConfiguracionRed.getDireccionServidor());
        System.out.println("Los jugadores ya pueden conectarse con el cliente.");
        System.out.println("Para detener el servidor, cerra esta ventana.");
    }

    private static Partida elegirPartida(Scanner scanner) {
        List<String> guardadas = GestorPartida.listarPartidasGuardadas();
        if (guardadas.isEmpty()) {
            System.out.println("No hay partidas guardadas: se crea una partida nueva.");
            return new Partida();
        }

        System.out.println();
        System.out.println("Partidas guardadas:");
        for (int i = 0; i < guardadas.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + guardadas.get(i));
        }

        while (true) {
            System.out.print("Numero de partida a cargar (0 = partida nueva): ");
            String entrada = scanner.nextLine().trim();
            try {
                int indice = Integer.parseInt(entrada);
                if (indice == 0) {
                    return new Partida();
                }
                if (indice >= 1 && indice <= guardadas.size()) {
                    String nombre = guardadas.get(indice - 1);
                    try {
                        Partida cargada = GestorPartida.cargar(nombre);
                        System.out.println("Partida \"" + nombre + "\" cargada.");
                        return cargada;
                    } catch (Exception e) {
                        System.out.println("No se pudo cargar: " + e.getMessage());
                        System.out.println("Se crea una partida nueva.");
                        return new Partida();
                    }
                }
                System.out.println("Numero fuera de rango.");
            } catch (NumberFormatException e) {
                System.out.println("No es un numero valido.");
            }
        }
    }
}
