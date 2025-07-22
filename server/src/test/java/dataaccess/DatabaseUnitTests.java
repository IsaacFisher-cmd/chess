package dataaccess;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseUnitTests {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    @BeforeEach
    void setup() {
        {
            try {
                userDAO = new SQLUserDAO();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
        {
            try {
                authDAO = new SQLAuthDAO();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
        {
            try {
                gameDAO = new SQLGameDAO();
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void positiveClearAuth() throws Exception {
    }

    @Test
    void positiveClearUser() throws Exception {
    }

    @Test
    void positiveClearGame() throws Exception {
    }
}
