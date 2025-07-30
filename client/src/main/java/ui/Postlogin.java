package ui;

import exception.ResponseException;
import request.*;
import result.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

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
                        return;
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
                        join(params);
                        break;
                    }
                    case "observe" -> {
                        observe(params);
                        break;
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
        try{
            server.logout(authToken);
        } catch (ResponseException e) {
            System.out.println("logout failed: " + e.getMessage());
        }
    }

    public void create(String... params) throws ResponseException{
        if (1 == params.length) {
            try {
                GameRequest request = new GameRequest(params[0]);
                GameResult result = server.create(authToken, request);
                if(result.gameID() != -1){
                    System.out.println("create worked, id: " + Integer.toString(result.gameID()));
                } else {
                    System.out.println("create failed");
                }
            } catch (ResponseException e) {
                System.out.println("create failed: bad create");
            }
        } else {
            System.out.println("create <NAME>");
        }
    }

    public void list() throws ResponseException {
        ListResult result = server.list(authToken);
        var games = result.games();

        if(games == null || games.isEmpty()) {
            System.out.println("no games");
            return;
        }

        System.out.println("games");
        for(int i = 0; i < games.size(); i++){
            var game = games.get(i);
            var white = game.whiteUsername != null ? game.whiteUsername : "empty";
            var black = game.blackUsername != null ? game.blackUsername : "empty";
            System.out.println(Integer.toString(i + 1) + ": " + game.gameName + " | White: " + white + " | Black: " + black);
        }
    }

    public void join(String... params) throws ResponseException{
        if (2 == params.length) {
            try {
                int id = Integer.parseInt(params[0]) - 1;
                ListResult result = server.list(authToken);
                var games = result.games();
                int realId = games.get(id).gameID;
                JoinRequest request = new JoinRequest(params[1].toUpperCase(), realId);
                server.join(authToken, request);
                if(params[1].equals("white")){
                    new Gameplay(server, true).run();
                } else {
                    new Gameplay(server, false).run();
                }
            } catch (ResponseException e) {
                System.out.println("join failed: bad join");
            }
        } else {
            System.out.println("join <ID> [WHITE|BLACK]");
        }
    }

    public void observe(String... params) throws ResponseException{
        if(1 == params.length) {
            try {
                int id = Integer.parseInt(params[0]);
                ListResult result = server.list(authToken);
                var games = result.games();
                if (id > 0 && id <= games.size()){
                    new Gameplay(server, false).run();
                } else {
                    System.out.println("observe failed: invalid id");
                }
            } catch (ResponseException e) {
                System.out.println("observe failed: bad observe");
            }
        } else {
            System.out.println("observe <ID>");
        }
    }

    public String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                help - with possible commands
                """;
    }
}
