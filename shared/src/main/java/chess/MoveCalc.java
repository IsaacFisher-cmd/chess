package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface MoveCalc {

    static boolean positionOnBoard(ChessPosition position){
        return (position.getRow() >= 1 && position.getColumn() >= 1 && position.getRow() <= 8 && position.getColumn() <= 8);
    }

    static

    static Collection<ChessMove> moveList(ChessBoard board, ChessPosition position, int[][] moveType, ChessGame.TeamColor teamColor, boolean isPawn){
        Collection<ChessMove> moves = new ArrayList<>();

    }
}