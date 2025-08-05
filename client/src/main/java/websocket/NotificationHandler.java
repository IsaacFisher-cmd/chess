package websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void load(LoadGameMessage message);

    void error(ErrorMessage message);

    void notify(NotificationMessage message);
}