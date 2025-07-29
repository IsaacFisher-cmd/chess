package ui;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class Postlogin {
    private ServerFacade server;
    private String serverURL;
    private String authToken;

    public Postlogin(String serverUrl, ServerFacade s, String auth) {
        this.serverURL = serverUrl;
        this.server = s;
        this.authToken = auth;
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print("\n [LOGGED_IN] >>> ");
            String line = scanner.nextLine();
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try{
                switch (cmd){
                    case "logout" -> {
                        logout();
                        break;
                    }
                    case "create" -> {
                        create(params);
                        break;
                    }
                    case "list" -> {
                        list();
                        break;
                    }
                    case "join" -> {
                        join();
                        break;
                    }
                    case "observe" -> {
                        observe(params);
                        break;
                    }
                    case "quit" -> {
                        return;
                    }
                    default -> {
                        help();
                    }
                }
            } catch (ResponseException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String logout() throws ResponseException {
        server.logout(authToken);
    }

    public String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }
}
