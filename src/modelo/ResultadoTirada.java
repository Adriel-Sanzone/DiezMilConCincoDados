package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResultadoTirada implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int puntos;
    private final List<Integer> valoresQuePuntuaron;
    private final boolean usoTodosLosDados;

    public ResultadoTirada(int puntos, List<Integer> valoresQuePuntuaron, boolean usoTodosLosDados) {
        if (puntos < 0) {
            throw new IllegalArgumentException("Los puntos no pueden ser negativos.");
        }
        if (valoresQuePuntuaron == null) {
            throw new IllegalArgumentException("La lista de valores no puede ser nula.");
        }
        this.puntos = puntos;
        this.valoresQuePuntuaron = Collections.unmodifiableList(new ArrayList<>(valoresQuePuntuaron));
        this.usoTodosLosDados = usoTodosLosDados;
    }

    public int getPuntos() {
        return puntos;
    }

    public List<Integer> getValoresQuePuntuaron() {
        return valoresQuePuntuaron;
    }

    public boolean usoTodosLosDados() {
        return usoTodosLosDados;
    }

    public boolean hayPuntos() {
        return puntos > 0;
    }
}
