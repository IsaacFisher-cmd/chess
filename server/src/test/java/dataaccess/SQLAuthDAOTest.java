package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    AuthDAO dao;

    AuthData defaultAuth;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        dao = new SQLAuthDAO();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE auth")) {
                statement.executeUpdate();
            }
        }

        defaultAuth = new AuthData("username", "token");
    }

    @AfterEach
    void tearDown() throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE auth")) {
                statement.executeUpdate();
            }
        }
    }

    @Test
    void addAuthPositive() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);

        String resultUsername;
        String resultToken;

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE username=?")) {
                statement.setString(1, defaultAuth.username());
                try (var results = statement.executeQuery()) {
                    results.next();
                    resultUsername = results.getString("username");
                    resultToken = results.getString("authToken");
                }
            }
        }

        assertEquals(defaultAuth, new AuthData(resultUsername, resultToken));
    }

    @Test
    void addAuthNegative() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);
        dao.addAuth(defaultAuth);

        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE username=?")) {
                statement.setString(1, defaultAuth.username());
                try (var results = statement.executeQuery()) {
                    results.next();
                    assertFalse(results.next()); //There should only be one element, despite having added two
                }
            }
        }
    }

    private void assertAuthNotExists(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE username=?")) {
                statement.setString(1, username);
                try (var results = statement.executeQuery()) {
                    assertFalse(results.next(), "Auth record should not exist in the database.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking auth record existence");
        }
    }

    @Test
    void deleteAuthPositive() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);
        dao.deleteAuth(defaultAuth.authToken());
        assertAuthNotExists(defaultAuth.username());
    }

    @Test
    void deleteAuthNegative() throws DataAccessException, SQLException {
        // If the given token does not exist, nothing should be thrown because "delete" is successful
        assertDoesNotThrow(() -> dao.deleteAuth("badToken"));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        dao.addAuth(defaultAuth);
        AuthData result = dao.getAuth(defaultAuth.authToken());
        assertEquals(defaultAuth, result);
    }

    @Test
    void getAuthNegative() {
        dao.addAuth(defaultAuth);
        assertThrows(DataAccessException.class, () -> dao.getAuth("badToken"));
    }

    @Test
    void clear() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);
        dao.clear();
        assertAuthNotExists(defaultAuth.username());
    }
}
