package ui;

import exception.ResponseException;
import request.*;
import result.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Prelogin{
    private ServerFacade server;
    private String serverURL;

    public Prelogin(String serverUrl) {
        this.serverURL = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to 240 chess. Type Help to get started.");

        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.print("\n [LOGGED_OUT] >>> ");
            String line = scanner.nextLine();
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try{
                switch (cmd){
                    case "register" -> {
                        register(params);
                        break;
                    }
                    case "login" -> {
                        login(params);
                        break;
                    }
                    case "quit" -> {
                        System.out.println("bye");
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

    public void register(String... params) throws ResponseException {
        if (3 == params.length) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            if(result.authToken() != null){
                new Postlogin(serverURL, server, result.authToken()).run();
            } else {
                System.out.println("failed");
            }
        } else {
            System.out.println("wrong");
        }
    }

    public void login(String... params) throws ResponseException {
        if (2 == params.length) {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(request);
            if(result.authToken() != null){
                new Postlogin(serverURL, server, result.authToken()).run();
            } else {
                System.out.println("failed");
            }
        } else {
            System.out.println("wrong");
        }
    }

    public void help() {
        System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """);
    }
}