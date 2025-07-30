package server;

import exception.ResponseException;

import com.google.gson.Gson;
import request.*;
import result.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null){
                http.setRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException{
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class, null);
    }

    public void clear() throws ResponseException{
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public LoginResult login(LoginRequest request) throws ResponseException{
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class, null);
    }

    public void logout(String authToken) throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public ListResult list(String authToken) throws ResponseException{
        var path = "/game";
        return this.makeRequest("GET", path, null, ListResult.class, authToken);
    }

    public GameResult create(String authToken, GameRequest request) throws ResponseException{
        var path = "/game";
        return this.makeRequest("POST", path, request, GameResult.class, authToken);
    }

    public void join(String authToken, JoinRequest request) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, request, null, authToken);
    }
}
