package modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;
import observer.Evento;
import persistencia.GestorPartida;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Partida extends ObservableRemoto implements IPartida, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Jugador> jugadores;
    private final Cubilete cubilete;
    private final EvaluadorPuntaje evaluador;

    private EstadoPartida estado;
    private int indiceJugadorActual;
    private Turno turnoActual;
    private ResultadoTirada ultimoResultado;
    private Jugador ganador;

    public Partida() {
        super();
        this.jugadores = new ArrayList<>();
        this.cubilete = new Cubilete();
        this.evaluador = new EvaluadorPuntaje();
        this.estado = EstadoPartida.CONFIGURACION;
        this.indiceJugadorActual = -1;
        this.turnoActual = null;
        this.ultimoResultado = null;
        this.ganador = null;
    }

    @Override
    public void agregarJugador(String nombre) throws RemoteException {
        if (estado != EstadoPartida.CONFIGURACION) {
            throw new IllegalStateException(
                    "Solo se pueden agregar jugadores antes de iniciar la partida.");
        }
        Jugador nuevo = new Jugador(nombre);
        if (jugadores.contains(nuevo)) {
            throw new IllegalArgumentException(
                    "Ya existe un jugador llamado '" + nuevo.getNombre() + "'.");
        }
        jugadores.add(nuevo);
        notificarObservadores(Evento.JUGADOR_AGREGADO);
    }

    @Override
    public void iniciar() throws RemoteException {
        if (estado != EstadoPartida.CONFIGURACION) {
            throw new IllegalStateException("La partida ya fue iniciada o ya termino.");
        }
        if (jugadores.size() < MIN_JUGADORES) {
            throw new IllegalStateException(
                    "Se requieren al menos " + MIN_JUGADORES + " jugadores para iniciar.");
        }
        cubilete.liberarTodos();
        indiceJugadorActual = 0;
        turnoActual = new Turno(jugadores.get(indiceJugadorActual));
        ultimoResultado = null;
        estado = EstadoPartida.EN_CURSO;
        notificarObservadores(Evento.PARTIDA_INICIADA);
    }

    private void validarTurnoDe(String jugador) {
        if (jugador == null || jugador.isBlank()) {
            throw new IllegalArgumentException(
                    "No se indico que jugador realiza la accion.");
        }
        if (turnoActual == null) {
            throw new IllegalStateException("Todavia no hay un turno en juego.");
        }
        if (!turnoActual.getJugador().getNombre().equalsIgnoreCase(jugador.trim())) {
            throw new IllegalStateException(
                    "No es el turno de " + jugador.trim() + ": esta jugando "
                            + turnoActual.getJugador().getNombre() + ".");
        }
    }

    @Override
    public void tirarDados(String jugador) throws RemoteException {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se pueden tirar los dados con la partida en curso.");
        }
        validarTurnoDe(jugador);
        FaseTurno fase = turnoActual.getFase();
        if (fase == FaseTurno.POST_TIRADA) {
            throw new IllegalStateException(
                    "Debe seleccionar dados o pasar el turno antes de tirar de nuevo.");
        }

        cubilete.tirar();
        this.ultimoResultado = null;
        turnoActual.cambiarFase(FaseTurno.POST_TIRADA);
        notificarObservadores(Evento.DADOS_TIRADOS);
    }

    @Override
    public void seleccionarDados(String jugador, List<Integer> indicesSeleccionados) throws RemoteException {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se pueden seleccionar dados con la partida en curso.");
        }
        validarTurnoDe(jugador);
        if (turnoActual.getFase() != FaseTurno.POST_TIRADA) {
            throw new IllegalStateException(
                    "Solo se pueden seleccionar dados después de tirar.");
        }
        if (indicesSeleccionados == null || indicesSeleccionados.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionarse al menos un dado.");
        }

        List<Integer> valoresSeleccionados = new ArrayList<>();
        for (Integer indice : indicesSeleccionados) {
            if (indice == null) {
                throw new IllegalArgumentException("Un índice no puede ser nulo.");
            }
            if (cubilete.estaReservadoEnIndice(indice)) {
                throw new IllegalArgumentException(
                        "El dado en el índice " + indice + " ya está reservado.");
            }
            valoresSeleccionados.add(cubilete.getValorEnIndice(indice));
        }

        boolean esPrimera = turnoActual.esPrimeraTirada();
        ResultadoTirada resultado = evaluador.evaluar(valoresSeleccionados, esPrimera);
        if (!resultado.usoTodosLosDados()) {
            throw new IllegalArgumentException(
                    "Selección inválida: hay dados elegidos que no forman parte de una combinación que puntúa.");
        }

        cubilete.reservarPorIndices(indicesSeleccionados);
        turnoActual.registrarTirada(resultado.getPuntos());
        this.ultimoResultado = resultado;

        if (cubilete.todosReservados()) {
            cubilete.liberarTodos();
        }

        turnoActual.cambiarFase(FaseTurno.POST_SELECCION);
        notificarObservadores(Evento.DADOS_SELECCIONADOS);
    }

    @Override
    public void pasarTurno(String jugador) throws RemoteException {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se puede pasar el turno con la partida en curso.");
        }
        validarTurnoDe(jugador);
        if (turnoActual.getFase() != FaseTurno.POST_TIRADA) {
            throw new IllegalStateException(
                    "Solo se puede pasar el turno después de tirar los dados.");
        }
        if (hayCombinacionesPosibles()) {
            throw new IllegalStateException(
                    "No se puede pasar el turno: hay combinaciones disponibles para seleccionar.");
        }
        turnoActual.perder();
        notificarObservadores(Evento.TURNO_PERDIDO);
        avanzarTurno();
    }

    @Override
    public void plantarse(String jugador) throws RemoteException {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se puede plantar con la partida en curso.");
        }
        validarTurnoDe(jugador);
        if (turnoActual.getFase() != FaseTurno.POST_SELECCION) {
            throw new IllegalStateException(
                    "Solo se puede plantar después de haber seleccionado dados en este turno.");
        }
        if (turnoActual.getPuntosAcumulados() == 0) {
            throw new IllegalStateException(
                    "No se puede plantar sin haber acumulado puntos en este turno.");
        }

        Jugador jugadorQueSePlanta = turnoActual.getJugador();
        jugadorQueSePlanta.sumarPuntos(turnoActual.getPuntosAcumulados());
        notificarObservadores(Evento.JUGADOR_PLANTADO);

        if (jugadorQueSePlanta.getPuntajeTotal() >= PUNTOS_PARA_GANAR) {
            this.ganador = jugadorQueSePlanta;
            this.estado = EstadoPartida.FINALIZADA;
            notificarObservadores(Evento.PARTIDA_FINALIZADA);
            return;
        }

        avanzarTurno();
    }

    @Override
    public boolean hayCombinacionesPosibles() throws RemoteException {
        if (turnoActual == null || turnoActual.getFase() != FaseTurno.POST_TIRADA) {
            return false;
        }
        List<Integer> valoresDisponibles = cubilete.getValoresDisponibles();
        if (valoresDisponibles.isEmpty()) {
            return false;
        }
        ResultadoTirada r = evaluador.evaluar(valoresDisponibles, turnoActual.esPrimeraTirada());
        return r.hayPuntos();
    }

    private void avanzarTurno() throws RemoteException {
        indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
        turnoActual = new Turno(jugadores.get(indiceJugadorActual));
        cubilete.liberarTodos();
        ultimoResultado = null;
        notificarObservadores(Evento.CAMBIO_TURNO);
    }

    @Override
    public EstadoPartida getEstado() throws RemoteException {
        return estado;
    }

    @Override
    public List<Jugador> getJugadores() throws RemoteException {
        return new ArrayList<>(jugadores);
    }

    @Override
    public Jugador getJugadorActual() throws RemoteException {
        if (turnoActual == null) {
            return null;
        }
        return turnoActual.getJugador();
    }

    @Override
    public int getPuntosAcumuladosTurno() throws RemoteException {
        if (turnoActual == null) {
            return 0;
        }
        return turnoActual.getPuntosAcumulados();
    }

    @Override
    public int getCantidadTiradasTurno() throws RemoteException {
        if (turnoActual == null) {
            return 0;
        }
        return turnoActual.getCantidadDeTiradas();
    }

    @Override
    public List<Integer> getValoresDeLosDados() throws RemoteException {
        return cubilete.getValoresTodos();
    }

    @Override
    public List<Boolean> getReservasDeLosDados() throws RemoteException {
        List<Boolean> reservas = new ArrayList<>();
        for (Dado dado : cubilete.getDados()) {
            reservas.add(dado.estaReservado());
        }
        return reservas;
    }

    @Override
    public int cantidadDadosDisponibles() throws RemoteException {
        return cubilete.cantidadDisponibles();
    }

    @Override
    public ResultadoTirada getUltimoResultado() throws RemoteException {
        return ultimoResultado;
    }

    @Override
    public Jugador getGanador() throws RemoteException {
        return ganador;
    }

    @Override
    public FaseTurno getFaseTurno() throws RemoteException {
        if (turnoActual == null) {
            return null;
        }
        return turnoActual.getFase();
    }

    @Override
    public void guardar(String nombreArchivo) throws RemoteException, IOException {
        GestorPartida.guardar(this, nombreArchivo);
    }

    @Override
    public List<String> listarPartidasGuardadas() throws RemoteException {
        return GestorPartida.listarPartidasGuardadas();
    }
}
