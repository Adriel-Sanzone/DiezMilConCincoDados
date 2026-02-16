package com.example.diezmilconcincodados.controlador;

import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.model.Jugador;
import com.example.diezmilconcincodados.model.Turno;
import com.example.diezmilconcincodados.observer.Observador;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;

import java.util.ArrayList;
import java.util.List;

public class ControladorConsolaEnJuego implements Observador
{
    private final Juego juego;
    private final VistaJuegoConsola vista;

    public ControladorConsolaEnJuego(Juego juego, VistaJuegoConsola vista)
    {
        this.juego = juego;
        this.vista = vista;
    }

    public void ejecutarLoopPartida()
    {
        juego.agregarObservador(this);
        actualizar();

        try
        {
            boolean volverAlMenu = false;
            while (!volverAlMenu && !juego.isFinalizado())
            {
                if (juego.getJugadores().isEmpty()) {
                    vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: No hay jugadores. Agrega al menos uno antes de jugar.");
                    volverAlMenu = true;
                    continue;
                }

                if (juego.getTurnoActual() == null) {
                    if (!juego.iniciarNuevoTurno()) {
                        vista.mostrarMensaje("\u001B[33mNo se pudo iniciar el turno.\u001B[0m");
                        volverAlMenu = true;
                        continue;
                    }
                }
                Jugador actual = juego.getJugadorActual();
                boolean turnoTerminado = false;
                while (!turnoTerminado && !volverAlMenu && !juego.isFinalizado())
                {
                    int opcion = vista.mostrarMenuJugador(actual.getNombre());
                    switch (opcion)
                    {
                        case 1 ->
                        {
                            Turno turno = juego.getTurnoActual();
                            if (turno == null)
                            {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: No hay turno inicializado.");
                                continue;
                            }
                            if (turno.yaLanze())
                            {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Ya lanzaste en este turno. Primero seleccioná o plantate.");
                                continue;
                            }
                            int res = juego.lanzarTurnoActual();
                            if (res < 0)
                            {
                                vista.mostrarMensaje("\u001B[31mBUST\u001B[0m: no obtuvo combinaciones. Turno perdido.");
                                turnoTerminado = true;
                            }
                        }
                        case 2 ->
                        {
                            Turno turno = juego.getTurnoActual();
                            if (turno == null || !turno.yaLanze())
                            {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Debe lanzar los dados antes de seleccionar.");
                                continue;
                            }
                            int[] indices = vista.leerIndicesSeleccion();
                            if (indices.length == 0)
                            {
                                vista.mostrarMensaje("\u001B[33mSelección cancelada.\u001B[0m");
                                continue;
                            }
                            List<Integer> seleccion = new ArrayList<>();
                            for (int idx : indices)
                            {
                                if (idx < 0 || idx >= turno.getUltimoLanzamientoCount())
                                {
                                    vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Índice fuera de rango");
                                    seleccion = null;
                                    break;
                                }
                                seleccion.add(idx);
                            }
                            int puntos = juego.aplicarSeleccionEnTurno(seleccion);
                            if (puntos < 0)
                            {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Selección inválida. Verifique que eligió combinaciones puntuables.");
                                continue;
                            }
                            vista.mostrarMensaje("Selección aplicada. Puntos ganados en esta selección: \u001B[32m" + puntos + "\u001B[0m");
                            Turno t = juego.getTurnoActual();

                        }
                        case 3 -> {
                            if (juego.getTurnoActual() == null)
                            {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: No hay turno activo para plantarse.");
                            } else
                            {
                                int ganados = juego.plantarseEnTurno();
                                if (ganados >= 0)
                                {
                                    vista.mostrarMensaje("Se plantó y se guardaron los puntos (+\u001B[32m" + ganados + "\u001B[0m). Avanza el turno.");
                                } else
                                {
                                    vista.mostrarMensaje("Se plantó. Avanza el turno.");
                                }
                                turnoTerminado = true;
                            }
                        }
                        case 4 ->
                        {
                            actualizar();
                        }
                        case 5 ->
                        {
                            volverAlMenu = true;
                            vista.mostrarMensaje("Volviendo al menú principal. \u001B[33mPartida pausada.\u001B[0m");
                        }
                        default -> vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Opción no válida.");
                    }
                }
            }
        } finally
        {
            juego.quitarObservador(this);
        }
    }


    @Override
    public void actualizar() {

        // 1. Preparar listas de jugadores y puntos
        java.util.List<String> nombres = new java.util.ArrayList<>();
        java.util.List<Integer> puntos = new java.util.ArrayList<>();

        for (Jugador j : juego.getJugadores()) {
            nombres.add(j.getNombre());
            puntos.add(j.getPuntosTotales());
        }

        // 2. Obtener datos del turno actual (si existe)
        Turno turno = juego.getTurnoActual();
        boolean hayTurno = turno != null;
        String jugadorActual = juego.getJugadorActual() != null
                ? juego.getJugadorActual().getNombre()
                : "N/A";
        int puntosTurno = hayTurno ? turno.getPuntosAcumuladosTurno() : 0;
        int dadosRest = hayTurno ? turno.getDadosRestantes() : 0;
        int[] ultLanz = hayTurno ? turno.getUltimoLanzamiento().clone() : new int[0];
        int ultCount = hayTurno ? turno.getUltimoLanzamientoCount() : 0;

        // 3. Llamar al metodo nuevo de la vista
        vista.mostrarEstadoJuegoActualizado(nombres, puntos, jugadorActual, puntosTurno, dadosRest, ultLanz, ultCount, hayTurno);

        // 4. Mostrar los dados formateados si corresponde
        if (hayTurno && ultCount > 0) {
            vista.mostrarDadosFormateados(ultLanz, ultCount);
        }
    }
}
