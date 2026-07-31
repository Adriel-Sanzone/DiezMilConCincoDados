package modelo;

import java.io.Serializable;
import java.util.Random;

public class Dado implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int VALOR_MINIMO = 1;
    private static final int VALOR_MAXIMO = 6;

    private int valor;
    private boolean reservado;

    private transient Random generador;

    public Dado() {
        this.valor = VALOR_MINIMO;
        this.reservado = false;
        this.generador = new Random();
    }

    public void tirar() {
        if (reservado) {
            return;
        }
        if (generador == null) {
            generador = new Random();
        }
        this.valor = generador.nextInt(VALOR_MAXIMO) + VALOR_MINIMO;
    }

    public void reservar() {
        this.reservado = true;
    }

    public void liberar() {
        this.reservado = false;
    }

    public int getValor() {
        return valor;
    }

    public boolean estaReservado() {
        return reservado;
    }
}
