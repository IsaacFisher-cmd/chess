package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand {

    ChessGame.TeamColor playerColor; // Can be null if just observing

    public Connect(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(CommandType.CONNECT, authToken, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}