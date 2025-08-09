package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
        JsonObject obj = new Gson().fromJson(message, JsonObject.class);
        UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(obj.get("commandType").getAsString());

        switch(type){
            case CONNECT -> {
                UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                connect(command.getGameID(), command.getAuthToken(), session);
            }
            case LEAVE -> {
                UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                leave(command.getGameID(), command.getAuthToken());
            }
            case RESIGN -> {
                UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
                resign(command.getGameID(), command.getAuthToken());
            }
            case MAKE_MOVE -> {
                MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(command.getGameID(), command.getAuthToken(), command, session);
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
        String user = authDao.getUsername(authToken);
        String color = "observer";
        if(gameDao.getGame(gameID).whiteUsername != null && gameDao.getGame(gameID).whiteUsername.equals(user)){
            color = "white";
        } else if(gameDao.getGame(gameID).blackUsername != null && gameDao.getGame(gameID).blackUsername.equals(user)){
            color = "black";
        }
        connections.broadcast(gameID, authToken, new NotificationMessage(" " + user +" is here on team " + color));
    }

    public void leave(int gameID, String authToken) throws ResponseException, DataAccessException, IOException {
        String user = authDao.getUsername(authToken);
        connections.broadcast(gameID, authToken, new NotificationMessage(" " + user + " left"));
        connections.remove(gameID, authToken);
        if(gameDao.getGame(gameID).whiteUsername.equals(user)){
            gameDao.addPlayer(gameID, "WHITE", null);
        } else if(gameDao.getGame(gameID).blackUsername.equals(user)){
            gameDao.addPlayer(gameID, "BLACK", null);
        }
    }

    public void resign(int gameID, String authToken) throws ResponseException, DataAccessException, IOException {
        GameData game = gameDao.getGame(gameID);
        if(game.game.isOver){
            connections.whisper(gameID, authToken, new ErrorMessage(" game is over"));
            return;
        }
        String user = authDao.getUsername(authToken);
        boolean white = user != null && user.equals(game.whiteUsername);
        boolean black = user != null && user.equals(game.blackUsername);
        if(!white && !black){
            connections.whisper(gameID, authToken, new ErrorMessage(" observers can't resign"));
            return;
        }
        game.game.isOver = true;
        gameDao.updateGame(gameID, game.game);
        connections.broadcast(gameID, null, new NotificationMessage(" " + user + " has resigned, game is over"));
    }

    public void makeMove(int gameID, String authToken, MakeMoveCommand command, Session session)
            throws ResponseException, DataAccessException, IOException {
        if(authDao.getAuth(authToken) == null){
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("bad auth")));
            return;
        }
        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game;
        String user = authDao.getUsername(authToken);
        if(game.isOver){
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

        ChessMove move = command.getMove();

        try{
            game.makeMove(move);
            gameDao.updateGame(gameID, game);
        } catch (Exception e) {
            connections.whisper(gameID, authToken, new ErrorMessage("bad move"));
            return;
        }

        connections.broadcast(gameID, null, new LoadGameMessage(game));

        String moveS = parseMove(move);

        ChessGame.TeamColor oppColor = (turn == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        String oppName = (oppColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername : gameData.blackUsername;

        if (game.isInCheckmate(turn == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE)){
            game.isOver = true;
            gameDao.updateGame(gameID, game);
            connections.broadcast(gameID, null, new NotificationMessage(oppName + " is in checkmate"));
        } else if (game.isInStalemate(turn == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE)){
            game.isOver = true;
            gameDao.updateGame(gameID, game);
            connections.broadcast(gameID, null, new NotificationMessage(oppName + " is in stalemate"));
        } else if (game.isInCheck(turn == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE)){
            connections.broadcast(gameID, null, new NotificationMessage(oppName + " is in check"));
        }
        connections.broadcast(gameID, authToken, new NotificationMessage(user + " made move " + moveS));
    }

    public String parseMove(ChessMove move){
        ChessPosition f = move.getStartPosition();
        ChessPosition t = move.getEndPosition();
        String fr = Integer.toString(f.getRow());
        String fc = String.valueOf((char) ('a' + f.getColumn() - 1));
        String tr = Integer.toString(t.getRow());
        String tc = String.valueOf((char) ('a' + t.getColumn() - 1));
        String p = "";
        if (move.getPromotionPiece() != null){
            p = move.getPromotionPiece().toString();
        }
        return (fc + fr + " " + tc + tr + " " + p);
    }
}