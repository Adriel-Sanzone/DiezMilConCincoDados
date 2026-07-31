package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluadorPuntaje implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int PUNTOS_CINCO_UNOS_PRIMER_LANZAMIENTO = 10000;
    public static final int PUNTOS_ESCALERA = 500;
    public static final int PUNTOS_TRES_UNOS = 1000;
    public static final int PUNTOS_UNO_SUELTO = 100;
    public static final int PUNTOS_CINCO_SUELTO = 50;

    public ResultadoTirada evaluar(List<Integer> valoresTirados, boolean esPrimeraTiradaDelTurno) {
        validarValores(valoresTirados);

        int cantidadDados = valoresTirados.size();

        if (cantidadDados == Cubilete.CANTIDAD_DADOS) {
            if (esPrimeraTiradaDelTurno && contarApariciones(valoresTirados, 1) == 5) {
                return new ResultadoTirada(
                        PUNTOS_CINCO_UNOS_PRIMER_LANZAMIENTO,
                        valoresTirados,
                        true);
            }
            if (esEscalera(valoresTirados)) {
                return new ResultadoTirada(PUNTOS_ESCALERA, valoresTirados, true);
            }
        }

        return evaluarCombinacionesGenerales(valoresTirados);
    }

    private ResultadoTirada evaluarCombinacionesGenerales(List<Integer> valoresTirados) {
        Map<Integer, Integer> conteoPorCara = contarPorValor(valoresTirados);
        int puntos = 0;
        List<Integer> valoresQuePuntuaron = new ArrayList<>();

        for (int cara = 1; cara <= 6; cara++) {
            int apariciones = conteoPorCara.getOrDefault(cara, 0);

            if (apariciones >= 3 && cara != 6) {
                int puntosTrio = (cara == 1) ? PUNTOS_TRES_UNOS : cara * 100;
                puntos += puntosTrio;
                for (int i = 0; i < 3; i++) {
                    valoresQuePuntuaron.add(cara);
                }
                apariciones -= 3;
            }

            if (cara == 1) {
                puntos += apariciones * PUNTOS_UNO_SUELTO;
                for (int i = 0; i < apariciones; i++) {
                    valoresQuePuntuaron.add(1);
                }
            } else if (cara == 5) {
                puntos += apariciones * PUNTOS_CINCO_SUELTO;
                for (int i = 0; i < apariciones; i++) {
                    valoresQuePuntuaron.add(5);
                }
            }
        }

        boolean usoTodosLosDados = valoresQuePuntuaron.size() == valoresTirados.size();
        return new ResultadoTirada(puntos, valoresQuePuntuaron, usoTodosLosDados);
    }

    private boolean esEscalera(List<Integer> valores) {
        List<Integer> ordenados = new ArrayList<>(valores);
        Collections.sort(ordenados);
        return ordenados.equals(List.of(1, 2, 3, 4, 5))
                || ordenados.equals(List.of(2, 3, 4, 5, 6));
    }

    private int contarApariciones(List<Integer> valores, int valorBuscado) {
        int cantidad = 0;
        for (Integer valor : valores) {
            if (valor != null && valor == valorBuscado) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private Map<Integer, Integer> contarPorValor(List<Integer> valores) {
        Map<Integer, Integer> mapa = new HashMap<>();
        for (Integer valor : valores) {
            mapa.merge(valor, 1, Integer::sum);
        }
        return mapa;
    }

    private void validarValores(List<Integer> valoresTirados) {
        if (valoresTirados == null) {
            throw new IllegalArgumentException("La lista de valores no puede ser nula.");
        }
        if (valoresTirados.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un valor para evaluar.");
        }
        if (valoresTirados.size() > Cubilete.CANTIDAD_DADOS) {
            throw new IllegalArgumentException(
                    "No puede evaluarse una tirada con más de " + Cubilete.CANTIDAD_DADOS + " dados.");
        }
        for (Integer valor : valoresTirados) {
            if (valor == null || valor < 1 || valor > 6) {
                throw new IllegalArgumentException("Valor de dado fuera de rango (1-6): " + valor);
            }
        }
    }
}
