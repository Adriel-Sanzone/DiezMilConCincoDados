package observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Observable implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient List<Observador> observadores;

    protected Observable() {
        this.observadores = new ArrayList<>();
    }

    public void agregarObservador(Observador observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador no puede ser nulo.");
        }
        if (observadores == null) {
            observadores = new ArrayList<>();
        }
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void quitarObservador(Observador observador) {
        if (observadores == null) {
            return;
        }
        observadores.remove(observador);
    }

    protected void notificar(Evento evento) {
        if (observadores == null) {
            return;
        }
        List<Observador> copia = new ArrayList<>(observadores);
        for (Observador observador : copia) {
            observador.actualizar(evento);
        }
    }
}
