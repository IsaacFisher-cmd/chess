package model;

import chess.ChessGame;

public class GameData {
    private int gameID;
    public String gameName;
    public String whiteUsername;
    public String blackUsername;
    private ChessGame game;

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game){
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;
    }
}
