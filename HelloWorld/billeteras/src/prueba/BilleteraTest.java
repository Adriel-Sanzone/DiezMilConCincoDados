package prueba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import principal.Billetera;
import principal.Dinero;
import principal.Moneda;

import static org.junit.jupiter.api.Assertions.*;

class BilleteraTest {

    Billetera billetera;

    @BeforeEach
    public void beforeEach(){
        this.billetera = new Billetera(Moneda.DOLAR, "Pablo", "alias");
    }

    @Test
    public void testCuandoCreoUnaBilleteraSuSaldoDebeSerCero(){
        assertEquals(new Dinero(0.0, Moneda.DOLAR), this.billetera.getSaldo());
    }

    @Test
    public void testDuenoEs(){
        assertTrue(this.billetera.duenoEs("Pablo"));
        assertFalse(this.billetera.duenoEs("José"));
    }

    @Test
    public void testMonedaEs(){
        assertTrue(this.billetera.monedaEs(Moneda.DOLAR));
        assertFalse(this.billetera.monedaEs(Moneda.PESOS_ARGENTINOS));
    }

    @Test
    public void testAcreditar(){
        this.billetera.acreditar(new Dinero(1500.00, Moneda.DOLAR));
        assertEquals(new Dinero(1500.00, Moneda.DOLAR), this.billetera.getSaldo());
    }

    @Test
    public void testDebitar(){
        this.billetera.acreditar(new Dinero(1500.00, Moneda.DOLAR));
        this.billetera.debitar(new Dinero(1000.00, Moneda.DOLAR));
        assertEquals(new Dinero(500.00, Moneda.DOLAR), this.billetera.getSaldo());
    }
}
