public class ConfiguracionRed {

    public static final String IP_SERVIDOR = "127.0.0.1";
    public static final int PUERTO_SERVIDOR = 8888;
    public static final int PUERTO_BASE_CLIENTE = 8889;

    private ConfiguracionRed() {
    }

    public static String getDireccionServidor() {
        return IP_SERVIDOR + ":" + PUERTO_SERVIDOR;
    }
}
