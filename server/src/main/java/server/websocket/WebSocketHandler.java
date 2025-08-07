package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDao;
    private final AuthDAO authDao;

    public WebSocketHandler(GameDAO gameDao, AuthDAO authDao){
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException, ResponseException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        var type = command.getCommandType();
        switch(type){
            case CONNECT -> {
                connect(command.getGameID(), command.getAuthToken(), session);
            }
            case LEAVE -> {
                leave(command.getGameID(), command.getAuthToken());
            }
            case RESIGN -> {
                resign(command.getGameID(), command.getAuthToken());
            }
            case MAKE_MOVE -> {

            }
        }
    }

    public void connect(int gameID, String authToken, Session session) throws IOException, DataAccessException, ResponseException {
        connections.add(gameID, authToken, session);
        ChessGame game = gameDao.getGame(gameID).game;
        LoadGameMessage msg = new LoadGameMessage(game);
        connections.whisper(gameID, authToken, msg);
        connections.broadcast(gameID, authToken, new NotificationMessage("we here"));
    }

    public void leave(int gameID, String authToken) throws ResponseException, DataAccessException, IOException {
        connections.remove(gameID, authToken);
        String user = authDao.getUsername(authToken);
        if(gameDao.getGame(gameID).whiteUsername.equals(user)){
            gameDao.getGame(gameID).whiteUsername = null;
        } else {
            gameDao.getGame(gameID).blackUsername = null;
        }
        connections.broadcast(gameID, authToken, new NotificationMessage("they left"));
    }

    public void resign(int gameID, String authToken) throws ResponseException, DataAccessException, IOException {
        GameData game = gameDao.getGame(gameID);
        game.isOver = true;
        connections.broadcast(gameID, null, new NotificationMessage("we done"));
    }

    public void makeMove()
}