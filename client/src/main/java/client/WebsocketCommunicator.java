package client;

import com.google.gson.Gson;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {

    Session session;

    public WebsocketCommunicator(String serverDomain) throws Exception {
        try {
            URI uri = new URI("ws://" + serverDomain + "/connect");
            //            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception();
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    public void handleMessage(String message) {
        System.out.println(message);
    }


    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }
}