package client;

import com.google.gson.Gson;
import model.GameData;
import model.GamesList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class HttpCommunicator {

    String baseURL;
    ServerFacade facade;

    public HttpCommunicator(ServerFacade facade, String serverDomain) {
        baseURL = serverDomain.startsWith("http") ? serverDomain : "http://" + serverDomain;
        this.facade = facade;
    }

    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/user", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken((String) resp.get("authToken"));
        return true;
    }

    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/session", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken((String) resp.get("authToken"));
        return true;
    }

    public boolean logout() {
        Map resp = request("DELETE", "/session");
        if (resp.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken(null);
        return true;
    }

    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/game", jsonBody);
        if (resp.containsKey("Error")) {
            return -1;
        }
        double gameID = (double) resp.get("gameID");
        return (int) gameID;
    }

    public HashSet<GameData> listGames() {
        String resp = requestString("GET", "/game");
        if (resp.contains("Error")) {
            return HashSet.newHashSet(8);
        }
        GamesList games = new Gson().fromJson(resp, GamesList.class);

        return games.games();
    }

    public boolean joinGame(int gameId, String playerColor) {
        Map body;
        if (playerColor != null) {
            body = Map.of("gameID", gameId, "playerColor", playerColor);
        } else {
            body = Map.of("gameID", gameId);
        }
        var jsonBody = new Gson().toJson(body);
        Map resp = request("PUT", "/game", jsonBody);
        return !resp.containsKey("Error");
    }

    public boolean observeGame(int gameID) {
        var jsonBody = new Gson().toJson(Map.of("gameID", gameID));
        Map resp = request("PUT", "/game/observe", jsonBody);
        return !resp.containsKey("Error");
    }

    private Map request (String method, String endpoint) {
        return request(method, endpoint, null);
    }

    private HttpURLConnection createConnection(String method, String endpoint, String body) throws IOException, URISyntaxException {
        URI uri = new URI(baseURL + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);

        // Add Authorization header if available
        if (facade.getAuthToken() != null) {
            http.addRequestProperty("authorization", facade.getAuthToken());
        }

        // If there's a body, set content type and write it to the connection
        if (body != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }

        http.connect();
        return http;
    }

    private Map request(String method, String endpoint, String body) {
        Map respMap;
        try {
            HttpURLConnection http = createConnection(method, endpoint, body);

            try {
                if (http.getResponseCode() == 401) {
                    return Map.of("Error", 401);
                }
            } catch (IOException e) {
                return Map.of("Error", 401);
            }


            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                respMap = new Gson().fromJson(inputStreamReader, Map.class);
            }

        } catch (URISyntaxException | IOException e) {
            return Map.of("Error", e.getMessage());
        }

        return respMap;
    }

    private String requestString(String method, String endpoint) {
        return requestString(method, endpoint, null);
    }

    private String requestString(String method, String endpoint, String body) {
        String resp;
        try {
            HttpURLConnection http = createConnection(method, endpoint, body);

            try {
                if (http.getResponseCode() == 401) {
                    return "Error: 401";
                }
            } catch (IOException e) {
                return "Error: 401";
            }


            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                resp = readerToString(inputStreamReader);
            }

        } catch (URISyntaxException | IOException e) {
            return String.format("Error: %s", e.getMessage());
        }

        return resp;
    }

    private String readerToString(InputStreamReader reader) {
        StringBuilder sb = new StringBuilder();
        try {
            for (int ch; (ch = reader.read()) != -1; ) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }

    }


}