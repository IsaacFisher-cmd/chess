package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDAO;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        var type = command.getCommandType();
        switch(type){
            case CONNECT -> {
                connect(command.getGameID(),command.getAuthToken(), session);
            }
            case LEAVE -> {

            }
            case RESIGN -> {

            }
            case MAKE_MOVE -> {
                
            }
        }
    }

    @OnWebSocketConnect
    private void onConnect(Session session) throws IOException {

    }

    @OnWebSocketClose
    private void onClose(Session session, int status, String message) throws IOException {

    }

    public void connect(int gameID, String authToken, Session session){
        connections.add(gameID, authToken, session);
    }

    public void disconnect(int gameID, String authToken) throws ResponseException {
        connections.remove(gameID, authToken);
    }

    public void whisperGame(int gameID, String authToken, LoadGameMessage message) throws IOException{
        connections.whisper(gameID, authToken, message);
    }

    public void broadcastMessage(int gameID, String excludeAuthToken, NotificationMessage message) throws IOException {
        connections.broadcast(gameID, excludeAuthToken, message);
    }
}