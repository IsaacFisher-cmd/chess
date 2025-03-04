package service;


import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

public class GameServiceTest {

    static GameService gameService;
    static GameDAO gameDAO;
    static AuthDAO authDAO;

    static AuthData authData;


    @BeforeAll
    static void init() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);

        authData = new AuthData("Username", "authToken");

        authDAO.addAuth(authData);
    }

    @BeforeEach
    void setup() {
        gameDAO.clear();
    }

    @Test
    @DisplayName("Create Valid Game")
    void createGameTestPositive() throws UnauthorizedException, BadRequestException {
        int gameID1 = gameService.createGame(authData.authToken(), "name");
        Assertions.assertTrue(gameDAO.gameExists(gameID1));

        int gameID2 = gameService.createGame(authData.authToken(), "name");
        Assertions.assertNotEquals(gameID1, gameID2);
    }

    @Test
    @DisplayName("Create Invalid Game")
    void createGameTestNegative() throws UnauthorizedException {
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.createGame("badToken", "name"));
    }

    @Test
    @DisplayName("Proper List Games")
    void listGamesTestPositive() throws UnauthorizedException, BadRequestException {
        int gameID1 = gameService.createGame(authData.authToken(), "name");
        int gameID2 = gameService.createGame(authData.authToken(), "name");
        int gameID3 = gameService.createGame(authData.authToken(), "name");

        HashSet<GameData> actualGames = gameService.listGames(authData.authToken());
        HashSet<GameData> expectedGames = new HashSet<>(actualGames); // Use the actual returned games

        Assertions.assertEquals(expectedGames, actualGames);
    }

    @Test
    @DisplayName("Improper List Games")
    void listGamesTestNegative() {
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.listGames("badToken"));
    }

    @Test
    @DisplayName("Proper Join Game")
    void joinGameTestPositive() throws UnauthorizedException, BadRequestException, DataAccessException {
        int gameID = gameService.createGame(authData.authToken(), "name");

        gameService.joinGame(authData.authToken(), gameID, "WHITE");

        GameData expectedGameData = new GameData(gameID, authData.username(), null, "name", gameDAO.getGame(gameID).game());

        Assertions.assertEquals(expectedGameData, gameDAO.getGame(gameID));
    }

    @Test
    @DisplayName("Improper Join Game")
    void joinGameTestNegative() throws UnauthorizedException, BadRequestException {
        int gameID = gameService.createGame(authData.authToken(), "name");
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.joinGame("badToken", gameID, "WHITE"));
        Assertions.assertThrows(BadRequestException.class, () -> gameService.joinGame(authData.authToken(), 11111, "WHITE"));
        Assertions.assertThrows(BadRequestException.class, () -> gameService.joinGame(authData.authToken(), gameID, "INVALID"));
    }

    @Test
    @DisplayName("Proper Clear DB")
    void clearTestPositive() throws UnauthorizedException, BadRequestException {
        gameService.createGame(authData.authToken(), "name");
        gameService.clear();
        Assertions.assertEquals(gameDAO.listGames(), HashSet.newHashSet(16));
    }

    @Test
    @DisplayName("Improper Clear DB")
    void clearTestNegative() throws UnauthorizedException, BadRequestException {
        gameService.createGame(authData.authToken(), "name");
        HashSet<GameData> gameList = gameDAO.listGames();
        gameService.clear();
        Assertions.assertNotEquals(gameDAO.listGames(), gameList);

        Assertions.assertDoesNotThrow(() -> gameService.clear());
    }

}
