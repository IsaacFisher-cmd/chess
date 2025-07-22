package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.GameRequest;
import request.JoinRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.GameResult;
import result.LoginResult;
import result.RegisterResult;
import service.GameService;
import service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseUnitTests {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    void positiveClearAuth() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        authDAO.clear();
        assertNull(authDAO.getAuth("fish"));
    }

    @Test
    void positiveCreateAuth() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        assertEquals(new AuthData("fish", "boy"), authDAO.getAuth("fish"));
    }

    @Test
    void negativeCreateAuth() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        assertThrows(Exception.class, () -> authDAO.createAuth(new AuthData("fish", "girl")));
    }

    @Test
    void positiveGetAuth() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        assertEquals(new AuthData("fish", "boy"), authDAO.getAuth("fish"));
    }

    @Test
    void negativeGetAuth() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        assertNull(authDAO.getAuth("shark"));
    }

    @Test
    void positiveRemoveAuth() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        authDAO.removeAuth("fish");
        assertNull(authDAO.getAuth("fish"));
    }

    @Test
    void negativeRemoveAuth() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        authDAO.removeAuth("shark");
        assertNotNull(authDAO.getAuth("fish"));
    }

    @Test
    void positiveGetUsername() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        assertEquals("boy", authDAO.getUsername("fish"));
    }

    @Test
    void negativeGetUsername() throws Exception {
        authDAO.createAuth(new AuthData("fish", "boy"));
        assertNull(authDAO.getUsername("shark"));
    }

    @Test
    void positiveClearUser() throws Exception {
        userDAO.createUser(new UserData("fish", "boy", "fisher"));
        userDAO.clear();
        assertNull(userDAO.getUser("fish"));
    }

    @Test
    void positiveCreateUser() throws Exception {
        userDAO.createUser(new UserData("fish", "boy", "fisher"));
        assertNotNull(userDAO.getUser("fish"));
    }

    @Test
    void negativeCreateUser() throws Exception {
        userDAO.createUser(new UserData("fish", "boy", "fisher"));
        assertThrows(Exception.class, () -> userDAO.createUser(new UserData("fish", "girl", "fischer")));
    }

    @Test
    void positiveGetUser() throws Exception {
        userDAO.createUser(new UserData("fish", "girl", "fisher"));
        assertNotNull(userDAO.getUser("fish"));
    }

    @Test
    void negativeGetUser() throws Exception {
        userDAO.createUser(new UserData("fish", "boy", "fisher"));
        assertNull(userDAO.getUser("shark"));
    }

    @Test
    void positiveClearGame() throws Exception {
        int id = gameDAO.createGame("fish");
        gameDAO.clear();
        assertNull(gameDAO.getGame(id));
    }

    @Test
    void positiveCreateGame() throws Exception {
        int id = gameDAO.createGame("fish");
        assertNotNull(gameDAO.getGame(id));
    }

    @Test
    void negativeCreateGame() throws Exception {
        int id = gameDAO.createGame("fish");
        assertNotEquals(id, gameDAO.createGame("fish"));
    }

    @Test
    void positiveGetGame() throws Exception {
        int id = gameDAO.createGame("shark");
        assertNotNull(gameDAO.getGame(id));
    }

    @Test
    void negativeGetGame() throws Exception {
        assertNull(gameDAO.getGame(42));
    }

    @Test
    void positiveListGame() throws Exception {
        gameDAO.createGame("fish");
        assertNotNull(gameDAO.listGames());
    }

    @Test
    void negativeListGame() throws Exception {
        assertTrue(gameDAO.listGames().size() == 0);
    }

    @Test
    void positiveAddPlayer() throws Exception {
        int id = gameDAO.createGame("fish");
        gameDAO.addPlayer(id, "WHITE", "boy");
        assertNotNull(gameDAO.getPlayer(id, "WHITE"));
    }

    @Test
    void negativeAddPlayer() throws Exception {
        int id = gameDAO.createGame("fish");
        gameDAO.getPlayer(id, "white");
        assertNull(gameDAO.getPlayer(id, "WHITE"));
    }

    @Test
    void positiveGetPlayer() throws Exception {
        int id = gameDAO.createGame("fish");
        gameDAO.addPlayer(id, "WHITE", "boy");
        assertEquals("boy", gameDAO.getPlayer(id, "WHITE"));
    }

    @Test
    void negativeGetPlayer() throws Exception {
        int id = gameDAO.createGame("fish");
        gameDAO.addPlayer(id, "WHITE", "boy");
        assertNull(gameDAO.getPlayer(id, "white"));
    }
}
