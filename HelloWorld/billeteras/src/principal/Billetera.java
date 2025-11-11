package principal;

public class Billetera {
    private String dueno;
    private Dinero saldo;

    public Billetera(Moneda moneda, String dueno, String alias) {
        this.dueno = dueno;
        this.saldo = new Dinero(0.0, moneda);
    }

    public boolean duenoEs(String dueno) {
        return this.dueno.equals(dueno);
    }

    public boolean monedaEs(Moneda moneda) {
        return this.saldo.getMoneda().equals(moneda);
    }

    public void acreditar(Dinero dinero) {
        this.saldo = this.saldo.sumar(dinero);
    }

    public Dinero getSaldo() {
        return this.saldo;
    }

    public void debitar(Dinero dinero) {
        this.saldo = new Dinero(
                this.getSaldo().getMonto() - dinero.getMonto(),
                this.getSaldo().getMoneda()
        );
    }
}

