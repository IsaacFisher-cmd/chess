package server;

import com.google.gson.Gson;
import dataaccess.*;
import server.handler.UserHandler;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    UserService userService = new UserService(userDAO, authDAO);

    UserHandler userHandler = new UserHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::register);
        Spark.delete("/db", this::clear);
        //This line initializes the server and can be removed once you have a functioning endpoint

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res){
        Gson gson = new Gson();
        try {
            userService.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
