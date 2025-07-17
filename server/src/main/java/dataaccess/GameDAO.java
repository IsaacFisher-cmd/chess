package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    String getPlayer(int gameID, String playerColor) throws DataAccessException;

    void addPlayer(int gameID, String playerColor, String username) throws DataAccessException;
}
