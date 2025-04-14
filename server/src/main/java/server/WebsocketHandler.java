package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.commands.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        Server.gameSessions.put(session, 0);
        System.out.println("Connected!");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Server.gameSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s\n", message);

        if (message.contains("\"commandType\":\"CONNECT\"")) {
            Connect command = new Gson().fromJson(message, Connect.class);
            Server.gameSessions.put(session, command.getGameID());
            handleConnect(session, command);
        }
        else if (message.contains("\"commandType\":\"MAKE_MOVE\"")) {
            System.out.println("move");
            MakeMove command = new Gson().fromJson(message, MakeMove.class);
            handleMakeMove(session, command);
        }
        else if (message.contains("\"commandType\":\"LEAVE\"")) {
            System.out.println("move");
            Leave command = new Gson().fromJson(message, Leave.class);
            handleLeave(session, command);
        }
        else if (message.contains("\"commandType\":\"RESIGN\"")) {
            Resign command = new Gson().fromJson(message, Resign.class);
            handleResign(session, command);
        }
    }

    private void handleConnect(Session session, Connect command) throws IOException {
        try {
            // Get user authentication data
            AuthData auth = Server.userService.getAuth(command.getAuthToken());

            // Get the game data based on the game ID and auth token
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            // Send the LOAD_GAME message to the root client with the current game state
            LoadGame load = new LoadGame(game.game());
            sendMessage(session, load);

            // Create a notification message to inform others about the root client joining
            String username = auth.username();
            String messageText;
            ChessGame.TeamColor joiningColor = command.getPlayerColor();

            // Check if the root client is joining as a player or observer
            if (joiningColor != null) {
                // Root client is a player, so set the color and notify others
                messageText = "%s has joined the game as %s".formatted(username, joiningColor);
            } else {
                // Root client is an observer, so notify others without specifying color
                messageText = "%s has joined the game as an observer".formatted(username);
            }

            Notification notification = new Notification(messageText);

            // Send the notification to all other clients in the game (except the root client)
            broadcastMessage(session, notification, false);
        }
        catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: Not a valid game"));
        }
    }

    private void handleMakeMove(Session session, MakeMove command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
            if (userColor == null) {
                sendError(session, new Error("Error: You are observing this game"));
                return;
            }

            if (game.game().getGameOver()) {
                sendError(session, new Error("Error: can not make a move, game is over"));
                return;
            }

            if (game.game().getTeamTurn().equals(userColor)) {
                game.game().makeMove(command.getMove());

                Notification notif;
                ChessGame.TeamColor opponentColor = userColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

                if (game.game().isInCheckmate(opponentColor)) {
                    notif = new Notification("Checkmate! %s wins!".formatted(auth.username()));
                    game.game().setGameOver(true);
                }
                else if (game.game().isInStalemate(opponentColor)) {
                    notif = new Notification("Stalemate caused by %s's move! It's a tie!".formatted(auth.username()));
                    game.game().setGameOver(true);
                }
                else if (game.game().isInCheck(opponentColor)) {
                    notif = new Notification("A move has been made by %s, %s is now in check!".formatted(auth.username(), opponentColor.toString()));
                }
                else {
                    notif = new Notification("A move has been made by %s".formatted(auth.username()));
                }
                broadcastMessage(session, notif);

                Server.gameService.updateGame(auth.authToken(), game);

                LoadGame load = new LoadGame(game.game());
                broadcastMessage(session, load, true);
            }
            else {
                sendError(session, new Error("Error: it is not your turn"));
            }
        }
        catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: invalid game"));
        } catch (InvalidMoveException e) {
            System.out.println("****** error: " + e.getMessage() + "  " + command.getMove().toString());
            sendError(session, new Error("Error: invalid move (you might need to specify a promotion piece)"));
        }
    }

    private void handleLeave(Session session, Leave command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            int gameID = Server.gameSessions.getOrDefault(session, 0);
            GameData game = Server.gameService.getGameData(command.getAuthToken(), gameID);

            // Determine and clear the player color
            if (auth.username().equals(game.whiteUsername())) {
                game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            } else if (auth.username().equals(game.blackUsername())) {
                game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            }

            Server.gameService.updateGame(auth.authToken(), game);

            Notification notif = new Notification("%s has left the game".formatted(auth.username()));
            broadcastMessage(session, notif);

            session.close();
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: invalid game"));
        }
    }

    private void handleResign(Session session, Resign command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);

            String opponentUsername = userColor == ChessGame.TeamColor.WHITE ? game.blackUsername() : game.whiteUsername();

            if (userColor == null) {
                sendError(session, new Error("Error: You are observing this game"));
                return;
            }
            if (game.game().getGameOver()) {
                sendError(session, new Error("Error: The game is already over!"));
                return;
            }

            game.game().setGameOver(true);
            Server.gameService.updateGame(auth.authToken(), game);

            Notification notif = new Notification("%s has forfeited, %s wins!".formatted(auth.username(), opponentUsername));
            broadcastMessage(session, notif, true);
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: invalid game"));
        }
    }

    // Send the notification to all clients on the current game except the currSession
    public void broadcastMessage(Session currSession, ServerMessage message) throws IOException {
        broadcastMessage(currSession, message, false);
    }

    // Send the notification to all clients on the current game
    public void broadcastMessage(Session currSession, ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));
        for (Session session : Server.gameSessions.keySet()) {
            boolean inAGame = Server.gameSessions.get(session) != 0;
            boolean sameGame = Server.gameSessions.get(session).equals(Server.gameSessions.get(currSession));
            boolean isSelf = session == currSession;
            if ((toSelf || !isSelf) && inAGame && sameGame) {
                sendMessage(session, message);
            }
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        System.out.println("Sending message to client: " + new Gson().toJson(message));
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void sendError(Session session, Error error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }

    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        else {return null;}
    }
}
