package controlador;

import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import modelo.EstadoPartida;
import modelo.FaseTurno;
import modelo.IPartida;
import modelo.Jugador;
import observer.Evento;
import observer.Observador;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Controlador implements IControladorRemoto {

    private IPartida modelo;
    private final List<Observador> vistasRegistradas;
    private String miJugador;

    public Controlador() {
        this.vistasRegistradas = new ArrayList<>();
        this.miJugador = null;
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.modelo = (IPartida) modeloRemoto;
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object cambio) throws RemoteException {
        if (!(cambio instanceof Evento)) {
            return;
        }
        notificarVistas((Evento) cambio);
    }

    private void notificarVistas(Evento evento) {
        List<Observador> copia = new ArrayList<>(vistasRegistradas);
        for (Observador vista : copia) {
            vista.actualizar(evento);
        }
    }

    public void registrarVista(Observador vista) {
        if (vista == null) {
            throw new IllegalArgumentException("La vista no puede ser nula.");
        }
        if (!vistasRegistradas.contains(vista)) {
            vistasRegistradas.add(vista);
        }
    }

    public void desregistrarVista(Observador vista) {
        vistasRegistradas.remove(vista);
    }

    public void unirse(String nombre) {
        agregarJugador(nombre);
        this.miJugador = nombre.trim();
    }

    public void setMiJugador(String nombre) {
        this.miJugador = nombre == null ? null : nombre.trim();
    }

    public String getMiJugador() {
        return miJugador;
    }

    public boolean estoyUnido() {
        return miJugador != null;
    }

    public boolean esMiTurno() {
        if (miJugador == null || !estaEnCurso()) {
            return false;
        }
        return miJugador.equalsIgnoreCase(getNombreJugadorActual());
    }

    public void agregarJugador(String nombre) {
        try {
            modelo.agregarJugador(nombre);
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo agregar el jugador.", e);
        }
    }

    public void iniciarPartida() {
        try {
            modelo.iniciar();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo iniciar la partida.", e);
        }
    }

    private String exigirMiJugador() {
        if (miJugador == null) {
            throw new IllegalStateException("Todavia no estas unido a la partida.");
        }
        return miJugador;
    }

    public void tirarDados() {
        try {
            modelo.tirarDados(exigirMiJugador());
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron tirar los dados.", e);
        }
    }

    public void seleccionarDados(List<Integer> indicesSeleccionados) {
        try {
            modelo.seleccionarDados(exigirMiJugador(), indicesSeleccionados);
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo confirmar la seleccion.", e);
        }
    }

    public void pasarTurno() {
        try {
            modelo.pasarTurno(exigirMiJugador());
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo pasar el turno.", e);
        }
    }

    public void plantarse() {
        try {
            modelo.plantarse(exigirMiJugador());
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo plantar.", e);
        }
    }

    public boolean estaEnConfiguracion() {
        return getEstado() == EstadoPartida.CONFIGURACION;
    }

    public boolean estaEnCurso() {
        return getEstado() == EstadoPartida.EN_CURSO;
    }

    public boolean estaFinalizada() {
        return getEstado() == EstadoPartida.FINALIZADA;
    }

    private EstadoPartida getEstado() {
        try {
            return modelo.getEstado();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar el estado de la partida.", e);
        }
    }

    public boolean esFaseInicial() {
        return getFaseTurno() == FaseTurno.INICIAL;
    }

    public boolean esFasePostTirada() {
        return getFaseTurno() == FaseTurno.POST_TIRADA;
    }

    public boolean esFasePostSeleccion() {
        return getFaseTurno() == FaseTurno.POST_SELECCION;
    }

    private FaseTurno getFaseTurno() {
        try {
            return modelo.getFaseTurno();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar la fase del turno.", e);
        }
    }

    public String getNombreJugadorActual() {
        try {
            return modelo.getJugadorActual().getNombre();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar el jugador actual.", e);
        }
    }

    public int getPuntajeJugadorActual() {
        try {
            return modelo.getJugadorActual().getPuntajeTotal();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar el puntaje del jugador actual.", e);
        }
    }

    public String getNombreGanador() {
        try {
            return modelo.getGanador().getNombre();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar el ganador.", e);
        }
    }

    public int getPuntajeGanador() {
        try {
            return modelo.getGanador().getPuntajeTotal();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar el puntaje del ganador.", e);
        }
    }

    public int getCantidadJugadores() {
        return getJugadores().size();
    }

    public List<String> getNombresJugadores() {
        List<String> nombres = new ArrayList<>();
        for (Jugador jugador : getJugadores()) {
            nombres.add(jugador.getNombre());
        }
        return nombres;
    }

    public List<Integer> getPuntajesJugadores() {
        List<Integer> puntajes = new ArrayList<>();
        for (Jugador jugador : getJugadores()) {
            puntajes.add(jugador.getPuntajeTotal());
        }
        return puntajes;
    }

    private List<Jugador> getJugadores() {
        try {
            return modelo.getJugadores();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar la lista de jugadores.", e);
        }
    }

    public int getPuntosAcumuladosTurno() {
        try {
            return modelo.getPuntosAcumuladosTurno();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron consultar los puntos del turno.", e);
        }
    }

    public int getCantidadTiradasTurno() {
        try {
            return modelo.getCantidadTiradasTurno();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar la cantidad de tiradas.", e);
        }
    }

    public List<Integer> getValoresDeLosDados() {
        try {
            return modelo.getValoresDeLosDados();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron consultar los dados.", e);
        }
    }

    public List<Boolean> getReservasDeLosDados() {
        try {
            return modelo.getReservasDeLosDados();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron consultar las reservas de los dados.", e);
        }
    }

    public int cantidadDadosDisponibles() {
        try {
            return modelo.cantidadDadosDisponibles();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar la cantidad de dados disponibles.", e);
        }
    }

    public boolean hayCombinacionesPosibles() {
        try {
            return modelo.hayCombinacionesPosibles();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron consultar las combinaciones posibles.", e);
        }
    }

    public boolean hayUltimoResultado() {
        try {
            return modelo.getUltimoResultado() != null;
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar la ultima tirada.", e);
        }
    }

    public int getPuntosUltimoResultado() {
        try {
            return modelo.getUltimoResultado().getPuntos();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron consultar los puntos de la ultima tirada.", e);
        }
    }

    public List<Integer> getValoresUltimoResultado() {
        try {
            return modelo.getUltimoResultado().getValoresQuePuntuaron();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron consultar los valores de la ultima tirada.", e);
        }
    }

    public boolean ultimoResultadoUsoTodosLosDados() {
        try {
            return modelo.getUltimoResultado().usoTodosLosDados();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo consultar el detalle de la ultima tirada.", e);
        }
    }

    public int getMinimoJugadoresParaIniciar() {
        return IPartida.MIN_JUGADORES;
    }

    public int getPuntosParaGanar() {
        return IPartida.PUNTOS_PARA_GANAR;
    }

    public void guardarPartida(String nombreArchivo) throws IOException {
        try {
            modelo.guardar(nombreArchivo);
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudo guardar la partida en el servidor.", e);
        }
    }

    public List<String> listarPartidasGuardadas() {
        try {
            return modelo.listarPartidasGuardadas();
        } catch (RemoteException e) {
            throw new ErrorDeConexion("No se pudieron listar las partidas guardadas.", e);
        }
    }
}
