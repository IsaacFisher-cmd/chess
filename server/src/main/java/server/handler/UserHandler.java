package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;
import spark.Response;
import spark.Request;

import java.util.Map;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService service){
        this.userService = service;
    }

    public Object register(Request req, Response res) throws DataAccessException{
        Gson gson = new Gson();
        try {
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("bad")) {
                res.status(400);
            } else if (e.getMessage().contains("taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object login(Request req, Response res) throws DataAccessException{
        Gson gson = new Gson();
        try {
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("bad")) {
                res.status(400);
            } else if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object logout(Request req, Response res) throws DataAccessException{
        Gson gson = new Gson();
        try {
            String authToken = req.headers("authorization");
            System.out.println("logout with " + authToken);
            userService.logout(authToken);
            res.status(200);
            return "{}";
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
