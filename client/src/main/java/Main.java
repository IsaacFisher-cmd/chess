import chess.*;
import client.ServerFacade;
import ui.PreloginREPL;
import websocket.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client:");
        ServerFacade server = new ServerFacade("localhost:" + ServerConfig.getPort());
        System.out.println(ServerConfig.getPort());
        PreloginREPL prelogin = new PreloginREPL(server);
        prelogin.run();
        System.out.println("Exited");
    }
}