package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{

    private final ChessGame game;

    public LoadGameMessage(ServerMessage.ServerMessageType type, ChessGame game){
        super(type);
        this.game = game;
    }

}
