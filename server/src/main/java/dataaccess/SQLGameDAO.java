package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO() throws DataAccessException{
        configureGameDatabase();
    }

    private final String[] gameStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `id` int NOT NULL AUTO_INCREMENT,
            `name` VARCHAR(255) NOT NULL,
            `white` VARCHAR(255) DEFAULT NULL,
            `black` VARCHAR(255) DEFAULT NULL,
            `game` TEXT,
            PRIMARY KEY (`id`)
            )
            """
    };

    private void configureGameDatabase() throws DataAccessException {
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
        String sql = "TRUNCATE TABLE games";
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

        String sql = "SELECT id, name, white, black, game FROM games";

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                try (var returnStatement = preparedStatement.executeQuery()) {
                    while (returnStatement.next()){
                        GameData gameData = new GameData(
                                returnStatement.getInt("id"),
                                returnStatement.getString("name"),
                                returnStatement.getString("white"),
                                returnStatement.getString("black"),
                                new Gson().fromJson(returnStatement.getString("game"), ChessGame.class)
                        );
                        games.add(gameData);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return games;
    }

    public int createGame(String gameName) throws DataAccessException{
        String sql = "INSERT INTO games (name, game) VALUES (?, ?)";

        ChessGame game = new ChessGame();
        String jGame = new Gson().toJson(game);

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, jGame);
                preparedStatement.executeUpdate();
                try(var gameId = preparedStatement.getGeneratedKeys()) {
                    if(gameId.next()) {
                        return gameId.getInt(1);
                    } else {
                        throw new DataAccessException("failed");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData getGame(int gameId) throws DataAccessException{
        String sql = "SELECT id, name, white, black, game FROM games WHERE id = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setInt(1, gameId);
                try (var returnStatement = preparedStatement.executeQuery()){
                    if(returnStatement.next()){
                        return new GameData(
                                returnStatement.getInt("id"),
                                returnStatement.getString("name"),
                                returnStatement.getString("white"),
                                returnStatement.getString("black"),
                                new Gson().fromJson(returnStatement.getString("game"), ChessGame.class)
                        );
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String getPlayer(int gameId, String playerColor) throws DataAccessException{
        if(playerColor.equals("WHITE")){
            String sql = "SELECT white FROM games WHERE id = ?";
            try (var conn = DatabaseManager.getConnection();
                 var preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setInt(1, gameId);
                    try (var returnStatement = preparedStatement.executeQuery()){
                        if(returnStatement.next()){
                            return returnStatement.getString("white");
                        }
                        return null;
                    }

            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } else {
            String sql = "SELECT black FROM games WHERE id = ?";
            try (var conn = DatabaseManager.getConnection();
                 var preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setInt(1, gameId);
                    try (var returnStatement = preparedStatement.executeQuery()){
                        if(returnStatement.next()){
                            return returnStatement.getString("black");
                        }
                        return null;
                    }

            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    public void addPlayer(int gameId, String playerColor, String username) throws DataAccessException{
        if(playerColor.equals("WHITE")){
            String sql = "UPDATE games SET white = ? WHERE id = ?";
            try (var conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameId);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } else {
            String sql = "UPDATE games SET black = ? WHERE id = ?";
            try (var conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameId);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }
}
