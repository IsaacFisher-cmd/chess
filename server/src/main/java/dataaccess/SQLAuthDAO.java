package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException{
        configureAuthDatabase();
    }

    private final String[] authStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
            `token` VARCHAR(255) NOT NULL,
            `username` VARCHAR(255) NOT NULL,
            PRIMARY KEY (`token`)
            )
            """
    };

    private void configureAuthDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : authStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO auths (token, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException{
        String sql = "TRUNCATE TABLE auths";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        String sql = "SELECT token, username FROM auths WHERE token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, authToken);
                try (var returnStatement = preparedStatement.executeQuery()){
                    if(returnStatement.next()){
                        return new AuthData(returnStatement.getString("token"), returnStatement.getString("username"));
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void removeAuth(String authToken) throws DataAccessException{
        String sql = "DELETE FROM auths WHERE token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String getUsername(String authToken) throws DataAccessException{
        String sql = "SELECT username FROM auths WHERE token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, authToken);
                try (var returnStatement = preparedStatement.executeQuery()){
                    if(returnStatement.next()){
                        return returnStatement.getString("username");
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
