package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.*;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    GameHelper gameHelper;


    public WebSocketFacade(String url, GameHelper helper) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHelper = helper;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson gson = new Gson();
                    JsonObject obj = gson.fromJson(message, JsonObject.class);

                    ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.valueOf(obj.get("serverMessageType").getAsString());

                    switch (type) {
                        case LOAD_GAME -> {
                            LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                            gameHelper.updateGame(load.game);
                        }
                        case ERROR -> {
                            ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                            gameHelper.printMessage(error.errorMessage);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage note = gson.fromJson(message, NotificationMessage.class);
                            gameHelper.printMessage(note.message);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch(IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void move(String authToken, int gameID, ChessMove m) throws ResponseException {
        try{
            MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, m);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch(IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch(IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch(IOException e){
            throw new ResponseException(500, e.getMessage());
        }
    }
}