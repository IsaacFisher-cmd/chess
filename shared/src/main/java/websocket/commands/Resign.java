package websocket.commands;

public class Resign extends UserGameCommand {

    public Resign(String authToken, int gameID) {
        super(UserGameCommand.CommandType.RESIGN, authToken, gameID);
    }

}
