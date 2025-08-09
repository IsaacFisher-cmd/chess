package ui;

import chess.*;
import exception.ResponseException;
import server.ServerFacade;
import websocket.GameHelper;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Scanner scanner = new Scanner(System.in);
        while(true){
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print("\n [GAME] >>> ");
            String line = scanner.nextLine();
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd){
                case "redraw" -> {
                    printBoard(false, null);
                    break;
                }
                case "leave" -> {
                    leaveGame();
                    return;
                }
                case "move" -> {
                    moveGame(params);
                    break;
                }
                case "resign" -> {
                    System.out.print("\n [YOU SURE?] <Y/N> >>> ");
                    String confirm = scanner.nextLine();
                    if(confirm.toLowerCase().equals("y")){
                        resignGame();
                        return;
                    }
                }
                case "highlight" -> {
                    highlight(params);
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
        printBoard(false, null);
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
                highlight <PIECE> - legal moves
                help - with possible commands
                """;
    }

    public void leaveGame() throws ResponseException {
        facade.leave(authToken, gameID);
    }

    public void resignGame() throws ResponseException{
        facade.resign(authToken, gameID);
    }

    public void moveGame(String... params){
        if (params.length != 2 && params.length != 3){
            System.out.println("move <FROM> <TO> (PROMOTE) - a piece");
        }
        try {
            ChessPosition f = parsePos(params[0]);
            ChessPosition t = parsePos(params[1]);
            ChessPiece.PieceType p = null;
            if (params.length == 3){
                switch(params[2].toLowerCase()){
                    case "queen" -> {
                        p = ChessPiece.PieceType.QUEEN;
                    }
                    case "rook" -> {
                        p = ChessPiece.PieceType.ROOK;
                    }
                    case "bishop" -> {
                        p = ChessPiece.PieceType.BISHOP;
                    }
                    case "knight" -> {
                        p = ChessPiece.PieceType.KNIGHT;
                    }
                    default -> {
                        System.out.println("not a valid promotion");
                        return;
                    }
                };
            }
            ChessMove move = new ChessMove(f, t, p);
            facade.move(authToken, gameID, move);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    public ChessPosition parsePos(String param){
        int col = param.charAt(0) - 'a' + 1;
        int row = Integer.parseInt(String.valueOf(param.charAt(1)));
        return new ChessPosition(row, col);
    }

    public void highlight(String... params){
        if (params.length != 1){
            System.out.println("highlight <PIECE> - legal moves");
            return;
        }
        ChessPosition pos = parsePos(params[0]);
        ChessPiece piece = game.getBoard().getPiece(pos);
        Collection<ChessMove> moves = piece.pieceMoves(game.getBoard(), pos);
        Collection<ChessPosition> pauses = new ArrayList<>();
        for(ChessMove move : moves){
            pauses.add(move.getEndPosition());
        }
        printBoard(true, pauses);
    }

    public void printBoard(boolean h, Collection<ChessPosition> pauses){
        //clear screen
        System.out.println(ERASE_SCREEN);
        //letter row
        letters();
        //current board
        ChessBoard board = game.getBoard();
        if(tF){
            for (int i = 8; i > 0; i--){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(i) + " ");
                for (int j = 1; j < 9; j++){
                    String color;
                    ChessPosition pos = new ChessPosition(i, j);
                    if ((i + j) % 2 == 0){
                        color = SET_BG_COLOR_MAGENTA;
                    } else {
                        color = SET_BG_COLOR_DARK_GREEN;
                    }
                    if (h){
                        if(pauses.contains(pos)){
                            color = SET_BG_COLOR_RED;
                        }
                    }
                    ChessPiece piece = board.getPiece(pos);
                    String p = uiPiece(piece);
                    if(i == 7 || i == 8){
                        System.out.print(color + SET_TEXT_COLOR_BLACK + p);
                    } else {
                        System.out.print(color + p);
                    }
                }
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(i) + " ");
                System.out.print(RESET_BG_COLOR + "\n");
            }
        } else {
            for (int i = 1; i < 9; i++){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(i) + " ");
                for (int j = 1; j < 9; j++){
                    String color;
                    ChessPosition pos = new ChessPosition(i, j);
                    if ((i + j) % 2 == 0){
                        color = SET_BG_COLOR_MAGENTA;
                    } else {
                        color = SET_BG_COLOR_DARK_GREEN;
                    }
                    if (h){
                        if(pauses.contains(pos)){
                            color = SET_BG_COLOR_RED;
                        }
                    }
                    ChessPiece piece = board.getPiece(pos);
                    String p = uiPiece(piece);
                    if(i == 7 || i == 8){
                        System.out.print(color + SET_TEXT_COLOR_BLACK + p);
                    } else {
                        System.out.print(color + p);
                    }
                }
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + Integer.toString(i) + " ");
                System.out.print(RESET_BG_COLOR + "\n");
            }
        }
        letters();
    }

    public void letters(){
        String[] row = {" a ", "  b ", " c ", "  d ", " e ", "  f ", " g ", "  h "};
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
