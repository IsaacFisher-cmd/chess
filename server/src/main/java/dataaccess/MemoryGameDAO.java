package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<String, GameData> games = new HashMap<>();

    @Override
    public void clear() throws DataAccessException{
        games.clear();
    }
}
