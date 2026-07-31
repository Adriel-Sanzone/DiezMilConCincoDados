package controlador;

import modelo.EstadoPartida;
import modelo.FaseTurno;
import modelo.Jugador;
import modelo.Partida;
import observer.Evento;
import observer.Observador;
import persistencia.GestorPartida;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controlador {

    private Partida partida;
    private final List<Observador> vistasRegistradas;

    public Controlador(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser nula.");
        }
        this.partida = partida;
        this.vistasRegistradas = new ArrayList<>();
    }

    public void cambiarPartida(Partida nuevaPartida) {
        if (nuevaPartida == null) {
            throw new IllegalArgumentException("La partida nueva no puede ser nula.");
        }
        for (Observador vista : vistasRegistradas) {
            partida.quitarObservador(vista);
        }
        this.partida = nuevaPartida;
        for (Observador vista : vistasRegistradas) {
            nuevaPartida.agregarObservador(vista);
        }
        for (Observador vista : vistasRegistradas) {
            vista.actualizar(Evento.PARTIDA_CARGADA);
        }
    }

    public void registrarVista(Observador vista) {
        if (vista == null) {
            throw new IllegalArgumentException("La vista no puede ser nula.");
        }
        if (!vistasRegistradas.contains(vista)) {
            vistasRegistradas.add(vista);
        }
        partida.agregarObservador(vista);
    }

    public void desregistrarVista(Observador vista) {
        vistasRegistradas.remove(vista);
        partida.quitarObservador(vista);
    }

    public void agregarJugador(String nombre) {
        partida.agregarJugador(nombre);
    }

    public void iniciarPartida() {
        partida.iniciar();
    }

    public void tirarDados() {
        partida.tirarDados();
    }

    public void seleccionarDados(List<Integer> indicesSeleccionados) {
        partida.seleccionarDados(indicesSeleccionados);
    }

    public void pasarTurno() {
        partida.pasarTurno();
    }

    public void plantarse() {
        partida.plantarse();
    }

    public boolean estaEnConfiguracion() {
        return partida.getEstado() == EstadoPartida.CONFIGURACION;
    }

    public boolean estaEnCurso() {
        return partida.getEstado() == EstadoPartida.EN_CURSO;
    }

    public boolean estaFinalizada() {
        return partida.getEstado() == EstadoPartida.FINALIZADA;
    }

    public boolean esFaseInicial() {
        return partida.getFaseTurno() == FaseTurno.INICIAL;
    }

    public boolean esFasePostTirada() {
        return partida.getFaseTurno() == FaseTurno.POST_TIRADA;
    }

    public boolean esFasePostSeleccion() {
        return partida.getFaseTurno() == FaseTurno.POST_SELECCION;
    }

    public String getNombreJugadorActual() {
        return partida.getJugadorActual().getNombre();
    }

    public int getPuntajeJugadorActual() {
        return partida.getJugadorActual().getPuntajeTotal();
    }

    public String getNombreGanador() {
        return partida.getGanador().getNombre();
    }

    public int getPuntajeGanador() {
        return partida.getGanador().getPuntajeTotal();
    }

    public int getCantidadJugadores() {
        return partida.getJugadores().size();
    }

    public List<String> getNombresJugadores() {
        List<String> nombres = new ArrayList<>();
        for (Jugador jugador : partida.getJugadores()) {
            nombres.add(jugador.getNombre());
        }
        return nombres;
    }

    public List<Integer> getPuntajesJugadores() {
        List<Integer> puntajes = new ArrayList<>();
        for (Jugador jugador : partida.getJugadores()) {
            puntajes.add(jugador.getPuntajeTotal());
        }
        return puntajes;
    }

    public int getPuntosAcumuladosTurno() {
        return partida.getPuntosAcumuladosTurno();
    }

    public int getCantidadTiradasTurno() {
        return partida.getCantidadTiradasTurno();
    }

    public List<Integer> getValoresDeLosDados() {
        return partida.getValoresDeLosDados();
    }

    public List<Boolean> getReservasDeLosDados() {
        return partida.getReservasDeLosDados();
    }

    public int cantidadDadosDisponibles() {
        return partida.cantidadDadosDisponibles();
    }

    public boolean hayCombinacionesPosibles() {
        return partida.hayCombinacionesPosibles();
    }

    public boolean hayUltimoResultado() {
        return partida.getUltimoResultado() != null;
    }

    public int getPuntosUltimoResultado() {
        return partida.getUltimoResultado().getPuntos();
    }

    public List<Integer> getValoresUltimoResultado() {
        return partida.getUltimoResultado().getValoresQuePuntuaron();
    }

    public boolean ultimoResultadoUsoTodosLosDados() {
        return partida.getUltimoResultado().usoTodosLosDados();
    }

    public int getMinimoJugadoresParaIniciar() {
        return Partida.MIN_JUGADORES;
    }

    public int getPuntosParaGanar() {
        return Partida.PUNTOS_PARA_GANAR;
    }

    public void guardarPartida(String nombreArchivo) throws IOException {
        GestorPartida.guardar(partida, nombreArchivo);
    }

    public void cargarPartida(String nombreArchivo) throws IOException, ClassNotFoundException {
        Partida cargada = GestorPartida.cargar(nombreArchivo);
        cambiarPartida(cargada);
    }

    public List<String> listarPartidasGuardadas() {
        return GestorPartida.listarPartidasGuardadas();
    }
}
