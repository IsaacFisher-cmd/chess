package ui;

import exception.ResponseException;
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
        var result = "";
        while(!result.equals("Quit")){
            System.out.print("\n [LOGGED_IN] >>> ");
            String line = scanner.nextLine();

            try{
                result = eval(line);
                if(result.equals("joined")) {
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
                case "logout" -> logout(params);
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> ovserve(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    
}
