package vista;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DadoView extends GridPane {

    private static final double TAMANIO_LADO = 90;
    private static final double RADIO_PUNTO = 7;
    private static final double RADIO_ESQUINA = 12;

    private final Circle[] puntos;
    private int valor;
    private boolean reservado;
    private boolean seleccionado;

    public DadoView() {
        this.valor = 1;
        this.reservado = false;
        this.seleccionado = false;
        this.puntos = new Circle[9];

        setPrefSize(TAMANIO_LADO, TAMANIO_LADO);
        setMinSize(TAMANIO_LADO, TAMANIO_LADO);
        setMaxSize(TAMANIO_LADO, TAMANIO_LADO);
        setHgap(6);
        setVgap(6);
        setPadding(new Insets(12));

        for (int i = 0; i < 9; i++) {
            int fila = i / 3;
            int columna = i % 3;
            Circle circulo = new Circle(RADIO_PUNTO, Color.BLACK);
            circulo.setVisible(false);
            puntos[i] = circulo;
            add(circulo, columna, fila);
            GridPane.setHalignment(circulo, HPos.CENTER);
            GridPane.setValignment(circulo, VPos.CENTER);
        }

        actualizarApariencia();
    }

    public void setValor(int valor) {
        if (valor < 1 || valor > 6) {
            throw new IllegalArgumentException("Valor de dado fuera de rango (1-6): " + valor);
        }
        this.valor = valor;
        actualizarApariencia();
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
        actualizarApariencia();
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        actualizarApariencia();
    }

    public boolean estaReservado() {
        return reservado;
    }

    public boolean estaSeleccionado() {
        return seleccionado;
    }

    private void actualizarApariencia() {
        boolean[] visibles = patronDePuntos(valor);
        for (int i = 0; i < 9; i++) {
            puntos[i].setVisible(visibles[i]);
        }

        Color colorFondo;
        Color colorBorde;
        double anchoBorde;
        if (reservado) {
            colorFondo = Color.web("#c8e6c9");
            colorBorde = Color.web("#2e7d32");
            anchoBorde = 3;
        } else if (seleccionado) {
            colorFondo = Color.web("#bbdefb");
            colorBorde = Color.web("#1565c0");
            anchoBorde = 3;
        } else {
            colorFondo = Color.WHITE;
            colorBorde = Color.BLACK;
            anchoBorde = 2;
        }

        setBackground(new Background(
                new BackgroundFill(colorFondo, new CornerRadii(RADIO_ESQUINA), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(
                colorBorde,
                BorderStrokeStyle.SOLID,
                new CornerRadii(RADIO_ESQUINA),
                new BorderWidths(anchoBorde))));
    }

    private boolean[] patronDePuntos(int valor) {
        boolean[] v = new boolean[9];
        switch (valor) {
            case 1 -> v[4] = true;
            case 2 -> { v[2] = true; v[6] = true; }
            case 3 -> { v[2] = true; v[4] = true; v[6] = true; }
            case 4 -> { v[0] = true; v[2] = true; v[6] = true; v[8] = true; }
            case 5 -> { v[0] = true; v[2] = true; v[4] = true; v[6] = true; v[8] = true; }
            case 6 -> { v[0] = true; v[2] = true; v[3] = true; v[5] = true; v[6] = true; v[8] = true; }
        }
        return v;
    }
}
