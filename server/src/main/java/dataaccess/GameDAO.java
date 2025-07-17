package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;
}
