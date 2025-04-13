package websocket;

public class ServerConfig {
    private static String port;

    public static void setPort(String port) {
        ServerConfig.port = port;
        System.out.println(port);
    }

    public static String getPort() {
        return port;
    }
}