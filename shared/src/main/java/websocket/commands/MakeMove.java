package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import websocket.commands.UserGameCommand;

public class MakeMove extends UserGameCommand {

    ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}