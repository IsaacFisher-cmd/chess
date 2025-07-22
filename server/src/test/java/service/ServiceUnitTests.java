package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import request.GameRequest;
import request.JoinRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.GameResult;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTests {
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    void setup() {
        MemoryAuthDAO mad = new MemoryAuthDAO();
        userService = new UserService(new MemoryUserDAO(), mad);
        gameService = new GameService(new MemoryGameDAO(), mad);
    }

    @Test
    void positiveRegister() throws Exception {
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        assertNotNull(res.authToken());
        assertEquals("fish", res.username());
    }

    @Test
    void negativeRegister() throws Exception {
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        assertThrows(Exception.class, () -> userService.register(req));
    }

    @Test
    void positiveClear() throws Exception {
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        gameService.createGame(res.authToken(), new GameRequest("fishgame"));

        userService.clear();
        gameService.clear();
    }

    @Test
    void positiveLogin() throws Exception {
        RegisterRequest req = new RegisterRequest("fish",BCrypt.hashpw("boy", BCrypt.gensalt()), "fish@boy.com");
        userService.register(req);
        LoginResult res = userService.login(new LoginRequest("fish", "boy"));

        assertNotNull(res.authToken());
        assertEquals("fish", res.username());
    }

    @Test
    void negativeLogin() throws Exception {
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        userService.register(req);
        assertThrows(Exception.class, () -> userService.login(new LoginRequest("fish", "girl")));
    }

    @Test
    void positiveLogout() throws Exception {
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        assertDoesNotThrow(() -> userService.logout(res.authToken()));
    }

    @Test
    void negativeLogout() throws Exception {
        assertThrows(Exception.class, () -> userService.logout("fake"));
    }

    @Test
    void positiveCreateGame() throws Exception{
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        GameResult gRes = gameService.createGame(res.authToken(), new GameRequest("fishgame"));
        assertTrue(gRes.gameID() > 0);
    }

    @Test
    void negativeCreateGame() throws Exception{
        assertThrows(Exception.class, () -> gameService.createGame("fake", new GameRequest("fishgame")));
    }

    @Test
    void positiveListGames() throws Exception{
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        GameResult gRes = gameService.createGame(res.authToken(), new GameRequest("fishgame"));
        assertNotNull(gameService.listGames(res.authToken()));
    }

    @Test
    void negativeListGames() throws Exception{
        assertThrows(Exception.class, () -> gameService.listGames("fake"));
    }

    @Test
    void positiveJoinGame() throws Exception{
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        GameResult gRes = gameService.createGame(res.authToken(), new GameRequest("fishgame"));
        assertDoesNotThrow(() -> gameService.joinGame(res.authToken(), new JoinRequest("WHITE", gRes.gameID())));
    }

    @Test
    void negativeJoinGame() throws Exception{
        RegisterRequest req = new RegisterRequest("fish","boy", "fish@boy.com");
        RegisterResult res = userService.register(req);
        GameResult gRes = gameService.createGame(res.authToken(), new GameRequest("fishgame"));
        assertThrows(Exception.class, () -> gameService.joinGame(res.authToken(), new JoinRequest("ORANGE", gRes.gameID())));
    }
}
