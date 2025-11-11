package prueba;

import org.junit.jupiter.api.Test;
import principal.Dinero;
import principal.Moneda;

import static org.junit.jupiter.api.Assertions.*;

class DineroTest {

    @Test
    public void crearInstancia(){
        Dinero dinero  = new Dinero(1000.0, Moneda.DOLAR);
        assertEquals(1000.0, dinero.getMonto());
        assertEquals(Moneda.DOLAR, dinero.getMoneda());
    }

    @Test
    public void comparacionConIgualMoneda(){
        assertEquals(
                new Dinero(1000.0, Moneda.DOLAR),
                new Dinero(1000.0, Moneda.DOLAR)
        );
        assertNotEquals(
                new Dinero(1000.0, Moneda.DOLAR),
                new Dinero(1500.0, Moneda.DOLAR)
        );
    }

    @Test
    public void comparacionConDiferenteMoneda(){
        assertNotEquals(
                new Dinero(1000.0, Moneda.DOLAR),
                new Dinero(1000.0, Moneda.PESOS_ARGENTINOS)
        );
        assertNotEquals(
                new Dinero(1000.0, Moneda.DOLAR),
                new Dinero(1500.0, Moneda.PESOS_ARGENTINOS)
        );
    }


}