package com.example.diezmilconcincodados.controlador;

import com.example.diezmilconcincodados.model.IJuego;
import com.example.diezmilconcincodados.model.Jugador;
import com.example.diezmilconcincodados.model.Turno;
import com.example.diezmilconcincodados.vista.VistaJuegoConsola;
import ar.edu.unlu.poo.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.poo.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ControladorConsolaEnJuego implements IControladorRemoto {

    private IJuego juego;
    private final VistaJuegoConsola vista;
    private String nombreLocal;

    private final AtomicBoolean enInteraccion = new AtomicBoolean(false);

    private String lastJugadorActualSeen = null;

    public ControladorConsolaEnJuego(IJuego juego, VistaJuegoConsola vista) {
        this.juego = juego;
        this.vista = vista;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }

    public boolean registrarJugadorLocal(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        try {
            boolean ok = juego.agregarJugador(new com.example.diezmilconcincodados.model.Jugador(nombre.trim()));
            return ok;
        } catch (RemoteException e) {
            vista.mostrarMensaje("\u001B[31mError de red al registrar jugador: " + e.getMessage() + "\u001B[0m");
            return false;
        }
    }

    public void ejecutarLoopPartida() throws RemoteException {
        actualizar(null, null);
        try {
            while (!juego.isFinalizado()) {
                if (juego.getJugadores().isEmpty() || juego.getTurnoActual() == null) {
                    lastJugadorActualSeen = null;
                    vista.mostrarMensaje("\u001B[33mEsperando jugadores...\u001B[0m");
                    try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                    continue;
                }

                Jugador actual = juego.getJugadorActual();
                if (actual == null) {
                    try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                    continue;
                }

                if (nombreLocal == null || !actual.getNombre().equalsIgnoreCase(nombreLocal)) {
                    String nombreActual = actual.getNombre();
                    if (lastJugadorActualSeen == null || !lastJugadorActualSeen.equals(nombreActual)) {
                        vista.mostrarMensaje("\u001B[36mNo es tu turno. Esperando a " + nombreActual + "...\u001B[0m");
                        lastJugadorActualSeen = nombreActual;
                    }
                    try { Thread.sleep(700); } catch (InterruptedException ignored) {}
                    continue;
                }

                boolean turnoTerminado = false;
                while (!turnoTerminado && !juego.isFinalizado()) {
                    Turno turno = juego.getTurnoActual();
                    if (turno == null) break;

                    enInteraccion.set(true);
                    int opcion = vista.mostrarMenuJugador(actual.getNombre());
                    enInteraccion.set(false);

                    switch (opcion) {
                        case 1 -> {
                            if (turno.yaLanze()) {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Ya lanzaste en este turno. Primero seleccioná o plantate.");
                                continue;
                            }
                            try {
                                int res = juego.lanzarTurnoActual();
                                if (res < 0) {
                                    vista.mostrarMensaje("\u001B[31mBUST\u001B[0m: no obtuvo combinaciones. Turno perdido.");
                                    turnoTerminado = true;
                                } else {
                                    lastJugadorActualSeen = null;
                                }
                            } catch (RemoteException e) {
                                vista.mostrarMensaje("\u001B[31mError de red al lanzar dados: " + e.getMessage() + "\u001B[0m");
                                turnoTerminado = true;
                            }
                        }
                        case 2 -> {
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
                                    vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Selección inválida.");
                                    continue;
                                }
                                vista.mostrarMensaje("Selección aplicada. Puntos ganados en esta selección: \u001B[32m" + puntos + "\u001B[0m");
                            } catch (RemoteException e) {
                                vista.mostrarMensaje("\u001B[31mError de red al aplicar selección: " + e.getMessage() + "\u001B[0m");
                                turnoTerminado = true;
                            }
                        }
                        case 3 -> {
                            if (turno == null) {
                                vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: No hay turno activo para plantarse.");
                            } else {
                                try {
                                    int ganados = juego.plantarseEnTurno();
                                    vista.mostrarMensaje("Se plantó y se guardaron los puntos (+\u001B[32m" + ganados + "\u001B[0m). Avanza el turno.");
                                    turnoTerminado = true;
                                    lastJugadorActualSeen = null;
                                } catch (RemoteException e) {
                                    vista.mostrarMensaje("\u001B[31mError de red al plantarse: " + e.getMessage() + "\u001B[0m");
                                    turnoTerminado = true;
                                }
                            }
                        }
                        case 4 -> {
                            actualizar(null, null);
                        }
                        case 5 -> {
                            vista.mostrarInstrucciones();
                        }
                        default -> vista.mostrarMensaje("\u001B[31mERROR\u001B[0m: Opción no válida.");
                    }
                }
            }

            try {
                Jugador ganador = juego.getGanador();
                if (ganador != null) {
                    vista.mostrarMensaje("\n\u001B[42m \u001B[1m\u001B[30m ¡GANADOR! \u001B[0m");
                    vista.mostrarMensaje("¡Felicitaciones, " + ganador.getNombre() + "!");
                } else {
                    vista.mostrarMensaje("\u001B[33mLa partida terminó.\u001B[0m");
                }
            } catch (RemoteException e) {
                vista.mostrarMensaje("\u001B[31mError de red al verificar ganador: " + e.getMessage() + "\u001B[0m");
            }

        } finally {}
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object arg) throws RemoteException {

        List<String> nombres = new ArrayList<>();
        List<Integer> puntos = new ArrayList<>();

        if (juego.isFinalizado())
        {
            Jugador ganador = juego.getGanador();
            if (ganador != null) {
                vista.mostrarMensaje("\n================================");
                vista.mostrarMensaje("  \u001B[44m  \u001B[GANADOR  \u001B[0m              ");
                vista.mostrarMensaje("================================");
                vista.mostrarMensaje("GANADOR: \u001B[34m" + ganador.getNombre() + "\u001B[0m");
                vista.mostrarMensaje("================================\n");
            }
            return;
        }

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

            if (enInteraccion.get()) {
                vista.mostrarEstadoJuegoActualizado(nombres, puntos, jugadorActual, puntosTurno, dadosRest, ultLanz, ultCount, hayTurno);
                if (hayTurno && ultCount > 0) {
                    vista.mostrarDadosFormateados(ultLanz, ultCount);
                }

                if (nombreLocal != null && jugadorActual.equalsIgnoreCase(nombreLocal)) {
                    vista.reimprimirMenuJugador(nombreLocal);
                }

                if (nombreLocal == null || !jugadorActual.equalsIgnoreCase(nombreLocal)) {
                    lastJugadorActualSeen = jugadorActual;
                } else {
                    lastJugadorActualSeen = null;
                }
            } else {

                vista.mostrarEstadoJuegoActualizado(nombres, puntos, jugadorActual, puntosTurno, dadosRest, ultLanz, ultCount, hayTurno);
                if (hayTurno && ultCount > 0) {
                    vista.mostrarDadosFormateados(ultLanz, ultCount);
                }
                if (nombreLocal == null || !jugadorActual.equalsIgnoreCase(nombreLocal)) {
                    lastJugadorActualSeen = jugadorActual;
                } else {
                    lastJugadorActualSeen = null;
                }
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
