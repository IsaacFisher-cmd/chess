package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.*;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class PostloginREPL {

    ServerFacade server;
    List<GameData> games;

    public PostloginREPL(ServerFacade server) {
        this.server = server;
        games = new ArrayList<>();
    }

    public void run() {
        boolean loggedIn = true;
        boolean inGame = false;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        while (loggedIn && !inGame) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "quit":
                    return;
                case "help":
                    printHelpMenu();
                    break;
                case "logout":
                    server.logout();
                    loggedIn = false;
                    break;
                case "list":
                    refreshGames();
                    printGames();
                    break;
                case "create":
                    if (input.length != 2) {
                        out.println("Please provide a name");
                        printCreate();
                        break;
                    }
                    int gameID = server.createGame(input[1]);
                    out.printf("Created game: %s%n", input[1]);
                    break;
                case "join":
                    handleJoin(input, inGame);
                    break;
                case "observe":
                    if (input.length != 2 || !input[1].matches("\\d")) {
                        out.println("Please provide a game ID");
                        printObserve();
                        break;
                    }
                    int gameObservedId = Integer.parseInt(input[1]);
                    if (games.isEmpty() || games.size() <= gameObservedId) {
                        refreshGames();
                        if (games.isEmpty()) {
                            out.println("Error: please first create a game");
                            break;
                        }
                        if (games.size() <= gameObservedId) {
                            out.println("Error: that Game ID does not exist");
                            printGames();
                            break;
                        }
                    }
                    GameData observeGame = games.get(gameObservedId);
                    if (observeGame == null) {
                        out.println("Error: Game with ID " + gameObservedId + " does not exist");
                        printGames();
                        break;
                    }
                    if (server.observeGame(observeGame.gameID())) {
                        out.println("You have joined the game as an observer");
                        inGame = true;
                        server.connectWS();
                        server.connect(observeGame.gameID(), null);
                        GameplayREPL gameplayREPL = new GameplayREPL(server, observeGame, null);
                        gameplayREPL.run();
                        break;
                    } else {
                        out.println("Error joining game as observer");
                        printObserve();
                        break;
                    }
                default:
                    out.println("Command not recognized, please try again");
                    printHelpMenu();
                    break;
            }
        }

        if (!loggedIn) {
            PreloginREPL preloginREPL = new PreloginREPL(server);
            preloginREPL.run();
        }
    }

    private void handleJoin(String[] input, boolean inGame) {
        if (input.length != 3 || !input[1].matches("\\d") || !input[2].toUpperCase().matches("WHITE|BLACK")) {
            out.println("Please provide a game ID and color choice");
            printJoin();
            return;
        }
        refreshGames();
        int gameNum = Integer.parseInt(input[1]);
        if (games.isEmpty() || games.size() <= gameNum) {
            refreshGames();
            if (games.isEmpty()) {
                out.println("Error: please first create a game");
                return;
            }
            if (games.size() <= gameNum) {
                out.println("Error: that Game ID does not exist");
                printGames();
                return;
            }
        }
        GameData joinGame = games.get(gameNum);
        ChessGame.TeamColor color = input[2].equalsIgnoreCase("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if (joinGame == null) {
            out.println("Game with ID " + gameNum + " not found");
            return;
        }
        if (server.joinGame(joinGame.gameID(), input[2].toUpperCase())) {
            out.println("You have joined the game");
            inGame = true;
            server.connectWS();
            server.connect(joinGame.gameID(), color);
            GameplayREPL gameplayREPL = new GameplayREPL(server, joinGame, color);
            gameplayREPL.run();
        } else {
            out.println("Game does not exist or color taken");
            printJoin();
        }
    }

    private String[] getUserInput() {
        out.print("\n[LOGGED IN] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void refreshGames() {
        games = new ArrayList<>();
        HashSet<GameData> gameList = server.listGames();
        games.addAll(gameList);
    }

    private void printGames() {
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            out.printf("%d -- Game Name: %s  |  White User: %s  |  Black User: %s %n", i, game.gameName(), whiteUser, blackUser);
        }
    }

    private void printHelpMenu() {
        printCreate();
        out.println("list - list all games");
        printJoin();
        printObserve();
        out.println("logout - log out of current user");
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printCreate() {
        out.println("create <NAME> - create a new game");
    }

    private void printJoin() {
        out.println("join <ID> [WHITE|BLACK] - join a game as color");
    }

    private void printObserve() {
        out.println("observe <ID> - observe a game");
    }
}