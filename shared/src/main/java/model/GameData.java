package model;

import chess.ChessGame;
import java.util.Objects;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameData other)) return false;
        return this.gameID == other.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID);
    }
}