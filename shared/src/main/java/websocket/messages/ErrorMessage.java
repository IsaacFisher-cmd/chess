package websocket.messages;

public class ErrorMessage extends ServerMessage{

    private final String errorMessage;

    public ErrorMessage(ServerMessage.ServerMessageType type, String error){
        super(type);
        this.errorMessage = error;
    }

}
