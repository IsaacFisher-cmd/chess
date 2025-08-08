package ui;

import chess.*;
import exception.ResponseException;
import server.ServerFacade;
import websocket.GameHelper;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Gameplay implements GameHelper {
    private ServerFacade server;
    private boolean tF;
    private ChessGame game;
    private WebSocketFacade facade;
    private String authToken;
    private Integer gameID;
    private String serverUrl;

    public Gameplay(ServerFacade s, boolean tf, String authToken, Integer gameID, String url) {
        this.server = s;
        this.tF = tf;
        this.authToken = authToken;
        this.gameID = gameID;
        this.serverUrl = url;
    }

    public void run() throws ResponseException {
        facade = new WebSocketFacade(serverUrl, this);
        facade.connect(authToken, gameID);
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("\n [GAME] >>> ");
            String line = scanner.nextLine();
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd){
                case "redraw" -> {
                    printBoard();
                }
                case "leave" -> {
                    leaveGame();
                    return;
                }
                case "move" -> {
                    break;
                }
                case "resign" -> {
                    return;
                }
                case "highlight" -> {
                    break;
                }
                default -> {
                    System.out.println(help());
                }
            }
        }
    }

    @Override
    public void updateGame(ChessGame game){
        this.game = game;
        printBoard();
    }

    @Override
    public void printMessage(String message){
        System.out.println(message);
    }

    public String help() {
        return """
                redraw - the board
                leave - the game
                move <FROM> <TO> - a piece
                resign - the game
                highlight - legal moves
                help - with possible commands
                """;
    }

    public void leaveGame() throws ResponseException {
        facade.leave(authToken, gameID);
    }

    public void move(String... params){
        if (params.length != 2 || params[0].length() != 2 || params[1].length() != 2){
            System.out.println("move <FROM> <TO> - a piece");
            return;
        }
        
        int fCol = params[0].charAt(0) - 'a' + 1;
        int fRow = Integer.parseInt(String.valueOf(params[0].charAt(1)));
        int tCol = params[1].charAt(0) - 'a' + 1;
        int tRow = Integer.parseInt(String.valueOf(params[1].charAt(1)));

        facade.move(authToken, gameID, new ChessMove());
    }

    public void printBoard(){
        //clear screen
        System.out.print(ERASE_SCREEN);
        //letter row
        letters();
        //current board
        ChessBoard board = game.getBoard();
        if(tF){
            for (int i = 0; i < 8; i++){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(8-i) + " ");
                for (int j = 0; j < 8; j++){
                    String color;
                    if ((i + j) % 2 == 0){
                        color = SET_BG_COLOR_MAGENTA;
                    } else {
                        color = SET_BG_COLOR_DARK_GREEN;
                    }
                    ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                    String p = uiPiece(piece);
                    System.out.print(color + p);
                }
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(8-i) + " ");
                System.out.print(RESET_BG_COLOR + "\n");
            }
        } else {
            for (int i = 0; i < 8; i++){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(1+i) + " ");
                for (int j = 0; j < 8; j++){
                    String color;
                    if ((i + j) % 2 == 0){
                        color = SET_BG_COLOR_MAGENTA;
                    } else {
                        color = SET_BG_COLOR_DARK_GREEN;
                    }
                    ChessPiece piece = board.getPiece(new ChessPosition(8-i, j + 1));
                    String p = uiPiece(piece);
                    System.out.print(color + p);
                }
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(1+i) + " ");
                System.out.print(RESET_BG_COLOR + "\n");
            }
        }
        letters();
    }

    public void letters(){
        String[] row = {" a ", "  b ", " c ", "  d ", " e ", "  f ", " g ", "  h  "};
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        for (int i = 0; i < 8; i++){
            System.out.print(row[i]);
        }
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY);
        System.out.print(RESET_BG_COLOR + "\n");
    }

    public String uiPiece(ChessPiece piece){
        if (piece == null){
            return EMPTY;
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()){
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()){
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        }
    }
}
