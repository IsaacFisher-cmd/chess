package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.GameRequest;
import request.JoinRequest;
import result.GameResult;
import result.ListResult;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService service){
        this.gameService = service;
    }

    public Object listGames(Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            String authToken = req.headers("authorization");
            ListResult result = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException E) {
            if (E.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + E.getMessage()));
        }
    }

    public Object createGame(Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            String authToken = req.headers("authorization");
            GameRequest request = gson.fromJson(req.body(), GameRequest.class);
            GameResult result = gameService.createGame(authToken, request);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else if (e.getMessage().contains("request")) {
                res.status(400);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object joinGame(Request req, Response res) throws DataAccessException {
        Gson gson = new Gson();
        try {
            String authToken = req.headers("authorization");
            JoinRequest request = gson.fromJson(req.body(), JoinRequest.class);
            gameService.joinGame(authToken, request);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            if (e.getMessage().contains("bad")) {
                res.status(400);
            } else if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else if (e.getMessage().contains("taken")){
                res.status(403);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
