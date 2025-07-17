package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.GameRequest;
import request.JoinRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.GameResult;
import result.ListResult;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDao, AuthDAO authDao){
        this.gameDAO = gameDao;
        this.authDAO = authDao;
    }

    public ListResult listGames(String authToken) throws DataAccessException{
        if(authDAO.getAuth(authToken) == null){
            throw new DataAccessException("unauthorized");
        }

        return new ListResult(gameDAO.listGames());
    }

    public GameResult createGame(String authToken, GameRequest request) throws DataAccessException{
        if(request.gameName() == null){
            throw new DataAccessException("bad request");
        }

        if(authDAO.getAuth(authToken) == null){
            throw new DataAccessException("unauthorized");
        }

        return new GameResult(gameDAO.createGame(request.gameName()));
    }

    public void joinGame(String authToken, JoinRequest request) throws DataAccessException{
        if(gameDAO.getGame(request.gameID()) == null){
            throw new DataAccessException("bad request");
        }


        if(!"WHITE".equals(request.playerColor()) && !"BLACK".equals(request.playerColor())){
            throw new DataAccessException("bad request");
        }

        if(gameDAO.getPlayer(request.gameID(), request.playerColor()) != null){
            throw new DataAccessException("already taken");
        }

        if(authDAO.getAuth(authToken) == null){
            throw new DataAccessException("unauthorized");
        }

        String username = authDAO.getUsername(authToken);
        gameDAO.addPlayer(request.gameID(), request.playerColor(), username);
    }

    public void clear() throws DataAccessException{
        gameDAO.clear();
    }
}
