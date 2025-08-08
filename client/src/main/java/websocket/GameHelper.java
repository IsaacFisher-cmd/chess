package websocket;

import chess.ChessGame;

public interface GameHelper {
    void updateGame(ChessGame game);

    void printMessage(String message);
}
