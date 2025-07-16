package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import spark.Response;
import spark.Request;

import java.util.Map;

public class UserHandler {
    private final UserService userService;

    public UserHandler(){
        this.userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    }

    public Object register(Request req, Response res) throws Exception{
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
}
