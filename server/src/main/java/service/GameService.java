package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
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

    public void clear() throws DataAccessException{
        gameDAO.clear();
    }
}
