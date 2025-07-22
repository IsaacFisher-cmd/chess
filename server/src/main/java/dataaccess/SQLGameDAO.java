package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO {

    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }

    private final String[] gameStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            'id' int NOT NULL AUTO_INCREMENT,
            'name' VARCHAR(255) NOT NULL,
            'white' VARCHAR(255) DEFAULT NULL,
            'black' VARCHAR(255) DEFAULT NULL,
            'game' TEXT
            PRIMARY KEY ('id')
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : gameStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException{
        String sql = "TRUNCATE TABLE game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public List<GameData> listGames() throws DataAccessException{
        List<GameData> games = new ArrayList<>();

        String sql = "SELECT id, name, white, black, game FROM game"
    }

    public int createGame(String gameName) throws DataAccessException{
        String sql = "INSERT INTO game (name, game) VALUES (?, ?)";

        ChessGame game = new ChessGame();
        String jGame = new Gson().toJson(game);

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, jGame);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData getGame(int gameId) throws DataAccessException{
        return games.get(gameId);
    }

    public String getPlayer(int gameId, String playerColor) throws DataAccessException{
        if(playerColor.equals("WHITE")){
            return games.get(gameId).whiteUsername;
        } else {
            return games.get(gameId).blackUsername;
        }
    }

    public void addPlayer(int gameId, String playerColor, String username) throws DataAccessException{
        if(playerColor.equals("WHITE")){
            games.get(gameId).whiteUsername = username;
        } else {
            games.get(gameId).blackUsername = username;
        }
    }
}
