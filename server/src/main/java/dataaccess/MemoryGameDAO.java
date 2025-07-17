package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public List<GameData> listGames() throws DataAccessException{
        return new ArrayList<>(games.values());
    }

    @Override
    public void clear() throws DataAccessException{
        games.clear();
    }
}
