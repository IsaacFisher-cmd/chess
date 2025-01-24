package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }
}

class King extends ChessPiece {
    public King(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        super(teamColor, type);
    }
}

class Queen extends ChessPiece {
    public Queen(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        super(teamColor, type);
    }
}

class Bishop extends ChessPiece {
    public Bishop(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        super(teamColor, type);
    }
}

class Rook extends ChessPiece {
    public Rook(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        super(teamColor, type);
    }
}

class Knight extends ChessPiece {
    public Knight(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        super(teamColor, type);
    }
}

class Pawn extends ChessPiece {
    public Pawn(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        super(teamColor, type);
    }
}