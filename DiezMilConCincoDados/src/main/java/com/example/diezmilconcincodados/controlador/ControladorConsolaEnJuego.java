package com.example.diezmilconcincodados.controlador;

import com.example.diezmilconcincodados.model.IJuego;
import com.example.diezmilconcincodados.model.Juego;
import com.example.diezmilconcincodados.model.Jugador;
import com.example.diezmilconcincodados.model.Turno;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;
import ar.edu.unlu.poo.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ControladorConsolaEnJuego implements IControladorRemoto {

    private IJuego juego;
    private final VistaJuegoConsola vista;

    public ControladorConsolaEnJuego(IJuego juego, VistaJuegoConsola vista) {
        this.juego = juego;
        this.vista = vista;
    }

    public void ejecutarLoopPartida() throws RemoteException {
        // NO llamamos juego.agregarObservador(this) manualmente
        // La librería lo agrega automáticamente al conectar el cliente
        actualizar(null, null);

        try {
            boolean volverAlMenu = false;
            while (!volverAlMenu && !juego.isFinalizado()) {
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

                while (!turnoTerminado && !volverAlMenu && !juego.isFinalizado()) {
                    int opcion = vista.mostrarMenuJugador(actual.getNombre());
                    switch (opcion) {
                        case 1 -> {
                            Turno turno = juego.getTurnoActual();
                            if (turno == null) {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: No hay turno inicializado.");
                                continue;
                            }
                            if (turno.yaLanze()) {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Ya lanzaste en este turno. Primero seleccioná o plantate.");
                                continue;
                            }
                            try {
                                int res = juego.lanzarTurnoActual();
                                actualizar(null, null); // notifica automáticamente
                                if (res < 0) {
                                    vista.mostrarMensaje("\u001B[31mBUST\u001B[0m: no obtuvo combinaciones. Turno perdido.");
                                    turnoTerminado = true;
                                }
                            } catch (RemoteException e) {
                                vista.mostrarMensaje("\u001B[31mError de red al lanzar dados: " + e.getMessage() + "\u001B[0m");
                            }
                        }
                        case 2 -> {
                            Turno turno = juego.getTurnoActual();
                            if (turno == null || !turno.yaLanze()) {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Debe lanzar los dados antes de seleccionar.");
                                continue;
                            }
                            int[] indices = vista.leerIndicesSeleccion();
                            if (indices.length == 0) {
                                vista.mostrarMensaje("\u001B[33mSelección cancelada.\u001B[0m");
                                continue;
                            }
                            List<Integer> seleccion = new ArrayList<>();
                            for (int idx : indices) {
                                if (idx < 0 || idx >= turno.getUltimoLanzamientoCount()) {
                                    vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Índice fuera de rango");
                                    seleccion = null;
                                    break;
                                }
                                seleccion.add(idx);
                            }
                            if (seleccion == null) continue;
                            try {
                                int puntos = juego.aplicarSeleccionEnTurno(seleccion);
                                if (puntos < 0) {
                                    vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Selección inválida. Verifique que eligió combinaciones puntuables.");
                                    continue;
                                }
                                vista.mostrarMensaje("Selección aplicada. Puntos ganados en esta selección: \u001B[32m" + puntos + "\u001B[0m");
                            } catch (RemoteException e) {
                                vista.mostrarMensaje("\u001B[31mError de red al aplicar selección: " + e.getMessage() + "\u001B[0m");
                            }
                        }
                        case 3 -> {
                            if (juego.getTurnoActual() == null) {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: No hay turno activo para plantarse.");
                            } else {
                                try {
                                    int ganados = juego.plantarseEnTurno();
                                    if (ganados >= 0) {
                                        vista.mostrarMensaje("Se plantó y se guardaron los puntos (+\u001B[32m" + ganados + "\u001B[0m). Avanza el turno.");
                                    } else {
                                        vista.mostrarMensaje("Se plantó. Avanza el turno.");
                                    }
                                    turnoTerminado = true;
                                } catch (RemoteException e) {
                                    vista.mostrarMensaje("\u001B[31mError de red al plantarse: " + e.getMessage() + "\u001B[0m");
                                }
                            }
                            if (juego.isFinalizado()) {
                                try {
                                    Jugador ganador = juego.getGanador();
                                    if (ganador != null) {
                                        vista.mostrarMensaje("\n--------------------------------");
                                        vista.mostrarMensaje("\n\u001B[42m \u001B[1m\u001B[30m ¡GANADOR! \u001B[0m");
                                        vista.mostrarMensaje("\n¡Felicitaciones, \u001B[34m" + ganador.getNombre() + "\u001B[0m!");
                                        vista.mostrarMensaje("Llegaste a " + ganador.getPuntosTotales() + " puntos.");
                                        vista.mostrarMensaje("\n--------------------------------");
                                        vista.mostrarMensaje("\n\nLa partida ha terminado. Volviendo al menú principal...\n");
                                        vista.mostrarMensaje("\n\u001B[33mPartida reseteada. ¡Podés jugar de nuevo!\u001B[0m");
                                        juego.resetearPartida();
                                    }
                                } catch (RemoteException e) {
                                    vista.mostrarMensaje("\u001B[31mError de red al verificar ganador: " + e.getMessage() + "\u001B[0m");
                                }
                                volverAlMenu = true;
                            }
                        }
                        case 4 -> {
                            actualizar(null, null);  // refresca manualmente
                        }
                        case 5 -> {
                            volverAlMenu = true;
                            vista.mostrarMensaje("Volviendo al menú principal. \u001B[33mPartida pausada.\u001B[0m");
                        }
                        default -> vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Opción no válida.");
                    }
                }
            }
        } finally {
            // NO llamar juego.quitarObservador(this)
            // La librería maneja la desconexión
        }
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object arg) throws RemoteException {

        List<String> nombres = new ArrayList<>();
        List<Integer> puntos = new ArrayList<>();
        try {
            for (Jugador j : juego.getJugadores()) {
                nombres.add(j.getNombre());
                puntos.add(j.getPuntosTotales());
            }

            Turno turno = juego.getTurnoActual();
            boolean hayTurno = turno != null;

            String jugadorActual = juego.getJugadorActual() != null
                    ? juego.getJugadorActual().getNombre()
                    : "N/A";
            int puntosTurno = hayTurno ? turno.getPuntosAcumuladosTurno() : 0;
            int dadosRest = hayTurno ? turno.getDadosRestantes() : 0;

            int[] ultLanz = hayTurno ? turno.getUltimoLanzamiento().clone() : new int[0];
            int ultCount = hayTurno ? turno.getUltimoLanzamientoCount() : 0;

            if (ultCount > 0) {
                for (int d : ultLanz) {
                    System.out.print(d + " ");
                }
                System.out.println();
            }

            vista.mostrarEstadoJuegoActualizado(nombres, puntos, jugadorActual, puntosTurno, dadosRest, ultLanz, ultCount, hayTurno);

            if (hayTurno && ultCount > 0) {
                vista.mostrarDadosFormateados(ultLanz, ultCount);
            } else {
            }
        } catch (RemoteException e) {
            vista.mostrarMensaje("\u001B[31mError de red al actualizar vista: " + e.getMessage() + "\u001B[0m");
        }
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) {
        this.juego = (IJuego) modeloRemoto;
    }
}