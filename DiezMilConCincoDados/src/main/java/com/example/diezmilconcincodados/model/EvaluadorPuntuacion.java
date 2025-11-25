package com.example.diezmilconcincodados.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EvaluadorPuntuacion implements Serializable {
    private static final long serialVersionUID = 1L;

    public static boolean hayCombinacionValida(int[] roll) {
        if (roll == null || roll.length != 5) {
            return false;
        }
        if (esEscalera(roll)) return true;
        Map<Integer, Integer> counts = contarPorValor(roll);
        if (counts.getOrDefault(1, 0) >= 1) return true;
        if (counts.getOrDefault(5, 0) >= 1) return true;
        for (int v = 1; v <= 6; v++) {
            if (counts.getOrDefault(v, 0) >= 3) return true;
        }
        return false;
    }

    public static int calcularPuntosPorSeleccion(int[] roll, List<Integer> indicesSeleccionados) {
        if (roll == null || roll.length != 5) {
            return -1;
        }
        if (indicesSeleccionados == null || indicesSeleccionados.isEmpty()) {
            return -1;
        }

        List<Integer> indices = indicesSeleccionados.stream().sorted().distinct().collect(Collectors.toList());

        for (int idx : indices) {
            if (idx < 0 || idx >= 5) return -1;
        }

        int[] selectedValues = indices.stream().mapToInt(i -> roll[i]).toArray();

        if (indices.size() == 5 && esCincoUnos(selectedValues)) {
            return 10000;
        }

        if (indices.size() == 5 && esEscalera(selectedValues)) {
            return 500;
        }

        Map<Integer, Integer> countsSel = contarPorValor(selectedValues);

        // Validación sin excepciones
        for (Map.Entry<Integer, Integer> e : countsSel.entrySet()) {
            int valor = e.getKey();
            int cnt = e.getValue();

            if (valor == 1 || valor == 5) {
                continue;
            } else {
                if (cnt < 3) {
                    return -1;
                }
                if (cnt > 3) {
                    return -1;
                }
            }
        }

        int puntos = 0;

        for (Map.Entry<Integer, Integer> e : countsSel.entrySet()) {
            int valor = e.getKey();
            int cnt = e.getValue();

            if (valor == 1) {
                if (cnt >= 3) {
                    puntos += 1000;
                    puntos += 100 * (cnt - 3);
                } else {
                    puntos += 100 * cnt;
                }
            } else if (valor == 5) {
                if (cnt >= 3) {
                    puntos += 500;
                    puntos += 50 * (cnt - 3);
                } else {
                    puntos += 50 * cnt;
                }
            } else {
                puntos += puntosTresIguales(valor);
            }
        }

        return puntos;
    }


    private static boolean esCincoUnos(int[] roll) {
        if (roll == null || roll.length != 5) return false;
        for (int v : roll) if (v != 1) return false;
        return true;
    }

    private static Map<Integer, Integer> contarPorValor(int[] roll) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int v : roll) counts.put(v, counts.getOrDefault(v, 0) + 1);
        return counts;
    }

    private static boolean esEscalera(int[] roll) {
        Set<Integer> s = Arrays.stream(roll).boxed().collect(Collectors.toSet());
        Set<Integer> e1 = new HashSet<>(Arrays.asList(1,2,3,4,5));
        Set<Integer> e2 = new HashSet<>(Arrays.asList(2,3,4,5,6));
        return s.equals(e1) || s.equals(e2);
    }

    private static int puntosTresIguales(int valor) {
        if (valor == 1) return 1000;
        return valor * 100;
    }
}
