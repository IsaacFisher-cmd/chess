package websocket.commands;

public class Resign extends UserGameCommand {

    int gameID;

    public Resign(String authToken, int gameID) {
        super(UserGameCommand.CommandType.LEAVE, authToken, gameID);
    }

    public Integer getGameID() {
        return gameID;
    }
}
