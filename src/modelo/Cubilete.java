package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cubilete implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CANTIDAD_DADOS = 5;

    private final List<Dado> dados;

    public Cubilete() {
        this.dados = new ArrayList<>(CANTIDAD_DADOS);
        for (int i = 0; i < CANTIDAD_DADOS; i++) {
            dados.add(new Dado());
        }
    }

    public void tirar() {
        for (Dado dado : dados) {
            dado.tirar();
        }
    }

    public List<Integer> getValoresDisponibles() {
        List<Integer> valores = new ArrayList<>();
        for (Dado dado : dados) {
            if (!dado.estaReservado()) {
                valores.add(dado.getValor());
            }
        }
        return valores;
    }

    public List<Integer> getValoresTodos() {
        List<Integer> valores = new ArrayList<>(CANTIDAD_DADOS);
        for (Dado dado : dados) {
            valores.add(dado.getValor());
        }
        return valores;
    }

    public int cantidadDisponibles() {
        int cantidad = 0;
        for (Dado dado : dados) {
            if (!dado.estaReservado()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public boolean todosReservados() {
        for (Dado dado : dados) {
            if (!dado.estaReservado()) {
                return false;
            }
        }
        return true;
    }

    public void reservarPorValores(List<Integer> valoresAReservar) {
        List<Integer> pendientes = new ArrayList<>(valoresAReservar);
        for (Dado dado : dados) {
            if (dado.estaReservado()) {
                continue;
            }
            Integer valor = Integer.valueOf(dado.getValor());
            if (pendientes.remove(valor)) {
                dado.reservar();
            }
        }
    }

    public void liberarTodos() {
        for (Dado dado : dados) {
            dado.liberar();
        }
    }

    public void reservarPorIndices(List<Integer> indices) {
        if (indices == null) {
            throw new IllegalArgumentException("La lista de índices no puede ser nula.");
        }
        Set<Integer> unicos = new HashSet<>();
        for (Integer indice : indices) {
            if (indice == null) {
                throw new IllegalArgumentException("Un índice no puede ser nulo.");
            }
            if (!unicos.add(indice)) {
                throw new IllegalArgumentException("Índices duplicados en la selección: " + indice);
            }
            validarIndice(indice);
            if (dados.get(indice).estaReservado()) {
                throw new IllegalArgumentException(
                        "El dado en el índice " + indice + " ya estaba reservado.");
            }
        }
        for (Integer indice : indices) {
            dados.get(indice).reservar();
        }
    }

    public int getValorEnIndice(int indice) {
        validarIndice(indice);
        return dados.get(indice).getValor();
    }

    public boolean estaReservadoEnIndice(int indice) {
        validarIndice(indice);
        return dados.get(indice).estaReservado();
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= CANTIDAD_DADOS) {
            throw new IllegalArgumentException(
                    "Índice de dado fuera de rango (0-" + (CANTIDAD_DADOS - 1) + "): " + indice);
        }
    }

    public List<Dado> getDados() {
        return new ArrayList<>(dados);
    }
}
