package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceTeam;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceTeam = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceTeam == piece.pieceTeam && pieceType == piece.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceTeam, pieceType);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceTeam;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public boolean onBoard(ChessPosition checkPos){
        int checkRow = checkPos.getRow();
        int checkCol = checkPos.getColumn();
        return ((1 <= checkRow) && (checkRow <= 8) && (1 <= checkCol) && (checkCol <= 8));
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeam = board.getPiece(myPosition).pieceTeam;
        int[][] directions;
        switch(piece.pieceType){
            case KING:
                directions = new int[][] {{1, -1},{1, 0},{1, 1},{0, -1},{0, 1},{-1, -1},{-1, 0},{-1, 1}};
                for (int[] dir : directions){
                    ChessPosition newPos = new ChessPosition(myPosition.getRow() + dir[0],myPosition.getColumn() + dir[1]);
                    if(onBoard(newPos)){
                        ChessPiece target = board.getPiece(newPos);
                        if(target == null || target.getTeamColor() != myTeam){
                            moves.add(new ChessMove(myPosition, newPos, null));
                        }
                    }
                }
                break;
            case QUEEN:
                directions = new int[][] {{1, -1},{1, 0},{1, 1},{0, -1},{0, 1},{-1, -1},{-1, 0},{-1, 1}};
                for (int[] dir : directions){
                    int i = 1;
                    int b = 1;
                    while(b == 1){
                        ChessPosition newPos = new ChessPosition(myPosition.getRow() + dir[0] * i,myPosition.getColumn() + dir[1] * i);
                        if(onBoard(newPos)){
                            ChessPiece target = board.getPiece(newPos);
                            if(target == null){
                                moves.add(new ChessMove(myPosition, newPos, null));
                            } else if (target.getTeamColor() != myTeam){
                                moves.add(new ChessMove(myPosition, newPos, null));
                                b = 0;
                            } else{
                                b = 0;
                            }
                        } else {
                            b = 0;
                        }
                        i++;
                    }
                }
                break;
        }
        return moves;
    }
}
