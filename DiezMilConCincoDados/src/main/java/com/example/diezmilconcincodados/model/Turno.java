package com.example.diezmilconcincodados.model;

import java.io.Serializable;
import java.util.List;

public class Turno implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Jugador jugador;
    private int puntosAcumuladosTurno;
    private int dadosRestantes;
    private int[] ultimoLanzamiento;
    private int ultimoLanzamientoCount;
    private boolean ultimoLanzamientoSumaba;
    private boolean yaLanze;

    public Turno(Jugador jugador) {
        this.jugador = jugador;
        this.puntosAcumuladosTurno = 0;
        this.dadosRestantes = 5;
        this.ultimoLanzamiento = new int[5];
        this.ultimoLanzamientoCount = 0;
        this.ultimoLanzamientoSumaba = false;
        this.yaLanze = false;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public int getPuntosAcumuladosTurno() {
        return puntosAcumuladosTurno;
    }

    public int getDadosRestantes() {
        return dadosRestantes;
    }

    public int[] getUltimoLanzamiento() {
        return ultimoLanzamiento.clone();
    }

    public int getUltimoLanzamientoCount() {
        return ultimoLanzamientoCount;
    }

    public boolean yaLanze() {
        return yaLanze;
    }

    public boolean lanzar(CuboDados cubo) {
        int[] res = cubo.lanzar(dadosRestantes);
        for (int i = 0; i < 5; i++) ultimoLanzamiento[i] = 0;
        int efectivos = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i] != 0) {
                ultimoLanzamiento[efectivos++] = res[i];
            } else {
                break;
            }
        }
        ultimoLanzamientoCount = efectivos;
        boolean hay = EvaluadorPuntuacion.hayCombinacionValida(ultimoLanzamiento);
        ultimoLanzamientoSumaba = hay;
        yaLanze = true;
        return hay;
    }

    public int aplicarSeleccion(List<Integer> indicesSeleccionados) {
        if (indicesSeleccionados == null || indicesSeleccionados.isEmpty())
            return -1; // selección vacía -> inválida

        List<Integer> indices = indicesSeleccionados.stream().distinct().sorted().toList();

        int cantidadValidos = ultimoLanzamientoCount;
        for (Integer idx : indices) {
            if (idx < 0 || idx >= cantidadValidos) {
                return -1; // índice fuera de rango
            }
        }

        // Calcular puntos sin modificar el estado aún
        int[] snapshot = new int[ultimoLanzamiento.length];
        System.arraycopy(ultimoLanzamiento, 0, snapshot, 0, ultimoLanzamiento.length);
        int puntos = EvaluadorPuntuacion.calcularPuntosPorSeleccion(snapshot, indices);

        if (puntos < 0) {
            // selección inválida según el evaluador -> no tocar estado
            return -1;
        }

        // --- Si llegamos acá la selección es válida: aplicar efectos sobre el turno ---
        puntosAcumuladosTurno += puntos;
        int dadosUsados = indices.size();
        dadosRestantes -= dadosUsados;
        if (dadosRestantes < 0) dadosRestantes = 0;

        for (Integer idx : indices) {
            if (idx >= 0 && idx < ultimoLanzamiento.length) {
                ultimoLanzamiento[idx] = 0;
            }
        }

        int[] compact = new int[5];
        int p = 0;
        for (int i = 0; i < ultimoLanzamiento.length; i++) {
            if (ultimoLanzamiento[i] != 0) {
                compact[p++] = ultimoLanzamiento[i];
            }
        }
        ultimoLanzamiento = compact;
        ultimoLanzamientoCount = p;

        if (dadosRestantes == 0 && ultimoLanzamientoSumaba) {
            dadosRestantes = 5;
        }
        yaLanze = false;
        return puntos;
    }


    public int plantarse() {
        int guardado = puntosAcumuladosTurno;
        puntosAcumuladosTurno = 0;
        dadosRestantes = 5;
        ultimoLanzamiento = new int[5];
        ultimoLanzamientoCount = 0;
        ultimoLanzamientoSumaba = false;
        yaLanze = false;
        return guardado;
    }
}