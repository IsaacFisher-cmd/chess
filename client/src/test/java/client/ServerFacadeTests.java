package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.GameRequest;
import request.JoinRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.GameResult;
import result.ListResult;
import result.LoginResult;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:0");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() {
        try{
            facade.clear();
        } catch (ResponseException e) {
            System.out.println("how");
        }
    }

    @Test
    void register() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerF() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(Exception.class, () -> facade.register(new RegisterRequest("player1", "password", "p1@email.com")));
    }

    @Test
    void login() throws Exception{
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        LoginResult result = facade.login(new LoginRequest("player1", "password"));
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void loginF() throws Exception{
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("player1", "password")));
    }

    @Test
    void logout() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutF() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.logout(authData.authToken());
        assertThrows(Exception.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    void list() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.create(authData.authToken(), new GameRequest("fish"));
        ListResult result = facade.list(authData.authToken());
        assertTrue(result.games().size() > 0);
    }

    @Test
    void listF() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.logout(authData.authToken());
        assertThrows(Exception.class, () -> facade.list(authData.authToken()));
    }

    @Test
    void create() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        GameResult result = facade.create(authData.authToken(), new GameRequest("fish"));
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createF() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.logout(authData.authToken());
        assertThrows(Exception.class, () -> facade.create(authData.authToken(), new GameRequest("fish")));
    }

    @Test
    void join() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        GameResult result = facade.create(authData.authToken(), new GameRequest("fish"));
        assertDoesNotThrow(() -> facade.join(authData.authToken(), new JoinRequest("WHITE", result.gameID())));
    }

    @Test
    void joinF() throws Exception{
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        GameResult result = facade.create(authData.authToken(), new GameRequest("fish"));
        assertThrows(Exception.class, () -> facade.join(authData.authToken(), new JoinRequest("WHIT", result.gameID())));
    }
}
