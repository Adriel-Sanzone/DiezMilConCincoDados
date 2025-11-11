package principal;

import java.util.Objects;

public class Dinero {
    private final double monto;
    private final Moneda moneda;

    public Dinero(double monto, Moneda moneda) {
        this.monto = monto;
        this.moneda = moneda;
    }

    public double getMonto() {
        return this.monto;
    }

    public Moneda getMoneda() {
        return this.moneda;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dinero dinero = (Dinero) o;
        return Double.compare(getMonto(), dinero.getMonto()) == 0 && getMoneda() == dinero.getMoneda();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMonto(), getMoneda());
    }

    public Dinero sumar(Dinero dinero) {
        return new Dinero(
                this.getMonto() + dinero.getMonto(),
                this.getMoneda()
        );
    }
}
