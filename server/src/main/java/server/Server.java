package server;

import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {


    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    UserService userService;
    GameService gameService;

    UserHandler userHandler;
    GameHandler gameHandler;

    public Server() {

        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        // Each of our endpoints
        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);
        Spark.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);
        Spark.exception(Exception.class, this::genericExceptionHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    // Methods needed for endpoints
    public void clearDB() {
        userService.clear();
        gameService.clear();
    }

    private Object clear(Request req, Response resp) {
        clearDB();
        resp.status(200);
        return "{}";
    }

    private void badRequestExceptionHandler(BadRequestException ex, Request req, Response resp) {
        resp.status(400);
        resp.body("{ \"message\": \"Error: bad request\" }");
    }

    private void unauthorizedExceptionHandler(UnauthorizedException ex, Request req, Response resp) {
        resp.status(401);
        resp.body("{ \"message\": \"Error: unauthorized\" }");
    }

    private void genericExceptionHandler(Exception ex, Request req, Response resp) {
        resp.status(500);
        resp.body("{ \"message\": \"Error: %s\" }".formatted(ex.getMessage()));
    }

}
