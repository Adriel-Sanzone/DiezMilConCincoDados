package com.example.diezmilconcincodados.model;

import java.io.Serializable;
import java.util.Random;

public class CuboDados implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int[] valores = new int[5];
    private transient Random rng;

    public CuboDados() {
        this.rng = new Random();
        for (int i = 0; i < 5; i++) valores[i] = 1;
    }

    public int[] lanzar(int cantidadDados) {
        if (cantidadDados < 1 || cantidadDados > 5) throw new IllegalArgumentException("cantidadDados debe estar entre 1 y 5");
        if (rng == null) rng = new Random();
        int[] resultado = new int[5];
        for (int i = 0; i < cantidadDados; i++) {
            int v = rng.nextInt(6) + 1;
            resultado[i] = v;
            valores[i] = v;
        }
        return resultado.clone();
    }
}
