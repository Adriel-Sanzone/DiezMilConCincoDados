package modelo;

import observer.Evento;
import observer.Observable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Partida extends Observable implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int PUNTOS_PARA_GANAR = 10000;
    public static final int MIN_JUGADORES = 2;

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

    public void agregarJugador(String nombre) {
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
        notificar(Evento.JUGADOR_AGREGADO);
    }

    public void iniciar() {
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
        notificar(Evento.PARTIDA_INICIADA);
    }

    public void tirarDados() {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se pueden tirar los dados con la partida en curso.");
        }
        FaseTurno fase = turnoActual.getFase();
        if (fase == FaseTurno.POST_TIRADA) {
            throw new IllegalStateException(
                    "Debe seleccionar dados o pasar el turno antes de tirar de nuevo.");
        }

        cubilete.tirar();
        this.ultimoResultado = null;
        turnoActual.cambiarFase(FaseTurno.POST_TIRADA);
        notificar(Evento.DADOS_TIRADOS);
    }

    public void seleccionarDados(List<Integer> indicesSeleccionados) {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se pueden seleccionar dados con la partida en curso.");
        }
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
        notificar(Evento.DADOS_SELECCIONADOS);
    }

    public void pasarTurno() {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se puede pasar el turno con la partida en curso.");
        }
        if (turnoActual.getFase() != FaseTurno.POST_TIRADA) {
            throw new IllegalStateException(
                    "Solo se puede pasar el turno después de tirar los dados.");
        }
        if (hayCombinacionesPosibles()) {
            throw new IllegalStateException(
                    "No se puede pasar el turno: hay combinaciones disponibles para seleccionar.");
        }
        turnoActual.perder();
        notificar(Evento.TURNO_PERDIDO);
        avanzarTurno();
    }

    public void plantarse() {
        if (estado != EstadoPartida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se puede plantar con la partida en curso.");
        }
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
        notificar(Evento.JUGADOR_PLANTADO);

        if (jugadorQueSePlanta.getPuntajeTotal() >= PUNTOS_PARA_GANAR) {
            this.ganador = jugadorQueSePlanta;
            this.estado = EstadoPartida.FINALIZADA;
            notificar(Evento.PARTIDA_FINALIZADA);
            return;
        }

        avanzarTurno();
    }

    public boolean hayCombinacionesPosibles() {
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

    private void avanzarTurno() {
        indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
        turnoActual = new Turno(jugadores.get(indiceJugadorActual));
        cubilete.liberarTodos();
        ultimoResultado = null;
        notificar(Evento.CAMBIO_TURNO);
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
    }

    public Jugador getJugadorActual() {
        if (turnoActual == null) {
            return null;
        }
        return turnoActual.getJugador();
    }

    public int getPuntosAcumuladosTurno() {
        if (turnoActual == null) {
            return 0;
        }
        return turnoActual.getPuntosAcumulados();
    }

    public int getCantidadTiradasTurno() {
        if (turnoActual == null) {
            return 0;
        }
        return turnoActual.getCantidadDeTiradas();
    }

    public List<Integer> getValoresDeLosDados() {
        return cubilete.getValoresTodos();
    }

    public List<Boolean> getReservasDeLosDados() {
        List<Boolean> reservas = new ArrayList<>();
        for (Dado dado : cubilete.getDados()) {
            reservas.add(dado.estaReservado());
        }
        return reservas;
    }

    public int cantidadDadosDisponibles() {
        return cubilete.cantidadDisponibles();
    }

    public ResultadoTirada getUltimoResultado() {
        return ultimoResultado;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public FaseTurno getFaseTurno() {
        if (turnoActual == null) {
            return null;
        }
        return turnoActual.getFase();
    }
}
