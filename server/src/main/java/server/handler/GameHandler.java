package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import request.LoginRequest;
import request.RegisterRequest;
import result.ListResult;
import result.LoginResult;
import result.RegisterResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(){
        this.gameService = new GameService(new MemoryGameDAO(), new MemoryAuthDAO());
    }

    public Object listGames(Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            String authToken = req.headers("authorization");
            ListResult result = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
