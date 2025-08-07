package model;

import chess.ChessGame;

public class GameData {
    public int gameID;
    public String gameName;
    public String whiteUsername;
    public String blackUsername;
    public ChessGame game;
    public boolean isOver = false;

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game){
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;
    }
}
