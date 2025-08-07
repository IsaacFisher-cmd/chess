package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap <String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String authToken, Session session) {
        connections.putIfAbsent(gameID, new ConcurrentHashMap<>());
        var connection = new Connection(authToken, session);
        connections.get(gameID).put(authToken, connection);
    }

    public void remove(int gameID, String authToken) {
        if (connections.containsKey(gameID)){
            connections.get(gameID).remove(authToken);
            if(connections.get(gameID).isEmpty()){
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludeAuthToken, ServerMessage message) throws IOException {
        if(!connections.containsKey(gameID)) {
            return;
        }

        var connects = connections.get(gameID);
        var removeList = new ArrayList<String>();

        for (var c : connects.entrySet()) {
            String authToken = c.getKey();
            Connection connection = c.getValue();

            if (connection.session.isOpen()) {
                if (!authToken.equals(excludeAuthToken)) {
                    connection.send(message.toString());
                }
            } else {
                removeList.add(authToken);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connects.remove(c);
        }
    }

    public void whisper(int gameID, String authToken, ServerMessage message) throws IOException{
        if(connections.containsKey(gameID)){
            Connection connection = connections.get(gameID).get(authToken);
            if(connection != null && connection.session.isOpen()){
                connection.send(message.toString());
            }
        }
    }
}
