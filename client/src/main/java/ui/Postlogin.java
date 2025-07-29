package ui;

import exception.ResponseException;
import request.*;
import result.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class Postlogin {
    private ServerFacade server;
    private String authToken;

    public Postlogin(ServerFacade s, String auth) {
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
                        System.out.println(help());
                    }
                }
            } catch (ResponseException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void logout() throws ResponseException {
        server.logout(authToken);
    }

    public void create(String... params) throws ResponseException{
        if (1 == params.length) {
            GameRequest request = new GameRequest(params[0]);
            GameResult result = server.create(authToken, request);
            if(result.gameID() != -1){
                System.out.println("success");
            } else {
                System.out.println("failed");
            }
        } else {
            System.out.println("wrong");
        }
    }

    public void list() throws ResponseException {
        ListResult result = server.list(authToken);
        System.out.println(result);
    }

    public void join(String... params) throws ResponseException{
        if (2 == params.length) {
            JoinRequest request = new JoinRequest(params[1], Integer.parseInt(params[0]));
            server.join(authToken, request);
            new Gameplay(server).run();
        } else {
            System.out.println("wrong");
        }
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
