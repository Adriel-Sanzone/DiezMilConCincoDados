package prueba;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import principal.Aplicacion;
import principal.Dinero;
import principal.Moneda;

class AplicacionTest {

    private Aplicacion app;

    @BeforeEach
    public void beforeEach(){
        app = new Aplicacion();
    }

    @Test
    @DisplayName("Si agrego una billetera la misma debería formar parte de la app")
    public void deberiaAgregarUnaBilletera(){
        // Arrange -> acomodar: prepara el entorno y los datos de prueba para el escenario
        // Act -> realizar: ejecutar el escenario
        app.agregarBilletera("Pablo", "alias", Moneda.DOLAR);
        // Assert -> verificar: verificar que el comportamiento sea el esperado
        // IMPORTANTE: el test no sabe nada de la implementación
        Assertions.assertTrue(app.existeBilletera("alias"));
    }

    @Test
    @DisplayName("Dada una cuenta sin saldo, al agregar USD 1000 su saldo debería ser USD 1000")
    public void debeAgregarDinero(){
        app.agregarBilletera("Pablo", "alias", Moneda.DOLAR);
        app.agregarDinero("alias", new Dinero(1000.0, Moneda.DOLAR));
        Assertions.assertEquals(app.getSaldo("alias"), new Dinero(1000.0, Moneda.DOLAR));
    }

    @Test
    public void test(){
        app.agregarBilletera("Pablo", "alias_origen", Moneda.DOLAR);
        app.agregarDinero("alias_origen", new Dinero(1000.00, Moneda.DOLAR));
        app.agregarBilletera("José", "alias_destino", Moneda.DOLAR);
        app.transferir("alias_origen", new Dinero(1000.00, Moneda.DOLAR), "alias_destino");
        Assertions.assertEquals(app.getSaldo("alias_origen"), new Dinero(0.0, Moneda.DOLAR));
        Assertions.assertEquals(app.getSaldo("alias_destino"), new Dinero(1000.0, Moneda.DOLAR));
    }

    @Test
    public void transferenciaSinFondos(){
    }
}
