package principal;

import java.util.HashMap;

public class Aplicacion {
    private HashMap<String, Billetera> billeteras;

    public Aplicacion() {
        this.billeteras = new HashMap<>();
    }

    public void agregarBilletera(String dueno, String alias, Moneda moneda) {
        this.billeteras.put(alias, new Billetera(moneda, dueno, alias));
    }

    public boolean existeBilletera(String alias) {
        return  this.billeteras.containsKey(alias);
    }

    public void agregarDinero(String alias, Dinero dinero) {
        this.billeteras.get(alias).acreditar(dinero);
    }

    public Dinero getSaldo(String alias) {
        return this.billeteras.get(alias).getSaldo();
    }

    public void transferir(String aliasOrigen, Dinero dinero, String aliasDestino) {
        this.agregarDinero(aliasDestino, dinero);
        this.quitarDinero(aliasOrigen, dinero);
    }

    private void quitarDinero(String aliasOrigen, Dinero dinero) {
        this.billeteras.get(aliasOrigen).debitar(dinero);
    }
}

