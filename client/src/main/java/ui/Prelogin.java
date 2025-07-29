package ui;

import exception.ResponseException;
import request.RegisterRequest;
import result.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Prelogin{
    private ServerFacade server;
    private String serverURL;

    public Prelogin(String serverUrl) {
        this.serverURL = serverUrl;
    }

    public void run(){
        System.out.println("Welcome to 240 chess. Type Help to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("Quit")){
            System.out.print("\n [LOGGED_OUT] >>> ");
            String line = scanner.nextLine();

            try{
                result = eval(line);
                if(result.equals("loggedin")) {
                    System.out.println("nice");
                    Postlogin postlogin = new Postlogin(serverURL, server, result);
                    postlogin.run();
                    break;
                } else {
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (3 == params.length) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            return result.authToken();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length() == 3) {

        }
    }


}