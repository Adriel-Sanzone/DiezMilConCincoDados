package controlador;

public class ErrorDeConexion extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErrorDeConexion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
