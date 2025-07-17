package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameID = 1;

    @Override
    public List<GameData> listGames() throws DataAccessException{
        return new ArrayList<>(games.values());
    }

    @Override
    public void clear() throws DataAccessException{
        games.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException{
        int gameId = gameID;
        gameID++;

        GameData game = new GameData(gameId, null, null, gameName, new ChessGame());

        games.put(gameId, game);
        return gameId;
    }
}
