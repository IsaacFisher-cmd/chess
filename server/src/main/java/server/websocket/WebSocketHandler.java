package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
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
                makeMove(command.getGameID(), command.getAuthToken(), command);
            }
        }
    }

    public void connect(int gameID, String authToken, Session session) throws IOException, DataAccessException, ResponseException {
        if(gameDao.getGame(gameID) == null){
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("no game")));
            return;
        }
        if(authDao.getAuth(authToken) == null){
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("bad auth")));
            return;
        }
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

    public void makeMove(int gameID, String authToken, UserGameCommand command) throws ResponseException, DataAccessException, IOException {
        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game;
        String user = authDao.getUsername(authToken);

        if(gameData.isOver){
            connections.whisper(gameID, authToken, new ErrorMessage("game is over"));
            return;
        }

        boolean white = user.equals(gameData.whiteUsername);
        boolean black = user.equals(gameData.blackUsername);
        ChessGame.TeamColor turn = game.getTeamTurn();

        if((turn == ChessGame.TeamColor.WHITE && !white) || (turn == ChessGame.TeamColor.BLACK && !black)){
            connections.whisper(gameID, authToken, new ErrorMessage("not ya turn"));
            return;
        }

        MakeMoveCommand moveCommand = new Gson().fromJson(new Gson().toJson(command), MakeMoveCommand.class);
        ChessMove move = moveCommand.getMove();

        try{
            game.makeMove(move);
        } catch (Exception e) {
            connections.whisper(gameID, authToken, new ErrorMessage("bad move"));
            return;
        }
        connections.broadcast(gameID, null, new LoadGameMessage(game));
    }
}