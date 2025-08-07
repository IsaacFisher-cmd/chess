package server;

import com.google.gson.Gson;
import dataaccess.*;
import server.handler.GameHandler;
import server.handler.UserHandler;
import server.websocket.WebSocketHandler;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {

    UserDAO userDAO;

    {
        try {
            userDAO = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    AuthDAO authDAO;

    {
        try {
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    GameDAO gameDAO;

    {
        try {
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    UserService userService = new UserService(userDAO, authDAO);
    GameService gameService = new GameService(gameDAO, authDAO);

    UserHandler userHandler = new UserHandler(userService);
    GameHandler gameHandler = new GameHandler(gameService);

    WebSocketHandler webSocketHandler = new WebSocketHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler(gameDAO));

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
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
            gameService.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
