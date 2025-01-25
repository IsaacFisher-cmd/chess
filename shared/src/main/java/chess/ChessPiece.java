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

    static boolean OnBoard(ChessPosition position) {
        return (position.getRow() >= 1 && position.getColumn() >= 1 && position.getRow() <= 8 && position.getColumn() <= 8);
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        switch (type) {
            case KING:
                int [][] kingMoveType = {{-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}};
                for (int[] direction : kingMoveType) {
                    ChessPosition testPosition = new ChessPosition(row + direction[0], col + direction[1]);
                    if (OnBoard(testPosition)) {
                        if (board.getPiece(testPosition) == null) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                        } else if (board.getPiece(testPosition).getTeamColor() != teamColor) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                        }
                    }
                }
                break;
            case QUEEN:
                int [][] queenMoveType = {{-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}};
                for (int[] direction : queenMoveType) {
                    boolean blocked = false;
                    int i = 1;
                    while (!blocked) {
                        ChessPosition testPosition = new ChessPosition(row + direction[0] * i, col + direction[1] * i);
                        if (!OnBoard(testPosition)) {
                            blocked = true;
                        } else if (board.getPiece(testPosition) == null) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                        } else if (board.getPiece(testPosition).getTeamColor() != teamColor) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                            blocked = true;
                        } else if (board.getPiece(testPosition).getTeamColor() == teamColor) {
                            blocked = true;
                        } else {
                            blocked = true;
                        }
                        i++;
                    }
                }
                break;
            case BISHOP:
                int [][] bishopMoveType = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
                for (int[] direction : bishopMoveType) {
                    boolean blocked = false;
                    int i = 1;
                    while (!blocked) {
                        ChessPosition testPosition = new ChessPosition(row + direction[0] * i, col + direction[1] * i);
                        if (!OnBoard(testPosition)) {
                            blocked = true;
                        } else if (board.getPiece(testPosition) == null) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                        } else if (board.getPiece(testPosition).getTeamColor() != teamColor) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                            blocked = true;
                        } else if (board.getPiece(testPosition).getTeamColor() == teamColor) {
                            blocked = true;
                        } else {
                            blocked = true;
                        }
                        i++;
                    }
                }
                break;
            case KNIGHT:
                int [][] knightMoveType = {{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};
                for (int[] direction : knightMoveType) {
                    ChessPosition testPosition = new ChessPosition(row + direction[0], col + direction[1]);
                    if (OnBoard(testPosition)) {
                        if (board.getPiece(testPosition) == null) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                        } else if (board.getPiece(testPosition).getTeamColor() != teamColor) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                        }
                    }
                }
                break;
            case ROOK:
                int [][] rookMoveType = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
                for (int[] direction : rookMoveType) {
                    boolean blocked = false;
                    int i = 1;
                    while (!blocked) {
                        ChessPosition testPosition = new ChessPosition(row + direction[0] * i, col + direction[1] * i);
                        if (!OnBoard(testPosition)) {
                            blocked = true;
                        } else if (board.getPiece(testPosition) == null) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                        } else if (board.getPiece(testPosition).getTeamColor() != teamColor) {
                            moves.add(new ChessMove(myPosition, testPosition, null));
                            blocked = true;
                        } else if (board.getPiece(testPosition).getTeamColor() == teamColor) {
                            blocked = true;
                        } else {
                            blocked = true;
                        }
                        i++;
                    }
                }
                break;
            case PAWN:
                ChessPiece.PieceType[] promotions = new ChessPiece.PieceType[]{null};

                int direction = -1;
                if (teamColor == ChessGame.TeamColor.WHITE){
                    direction = 1;
                }

                if((direction == 1 && row == 7) || (direction == -1 && row == 2)){
                    promotions = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};
                }

                for (ChessPiece.PieceType promotion : promotions) {
                    ChessPosition forwardMove = new ChessPosition(row + direction, col);
                    if (OnBoard(forwardMove) && board.getPiece(forwardMove) == null) {
                        moves.add(new ChessMove(myPosition, forwardMove, promotion));
                    }

                    ChessPosition leftCap = new ChessPosition(row + direction, col - 1);
                    if (OnBoard(leftCap) && board.getPiece(leftCap) != null && board.getPiece(leftCap).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(myPosition, leftCap, promotion));
                    }

                    ChessPosition rightCap = new ChessPosition(row + direction, col + 1);
                    if (OnBoard(rightCap) && board.getPiece(rightCap) != null && board.getPiece(rightCap).getTeamColor() != teamColor) {
                        moves.add(new ChessMove(myPosition, rightCap, promotion));
                    }

                    ChessPosition doubleMove = new ChessPosition(row + direction*2, col);
                    if (OnBoard(doubleMove) && ((direction == 1 && row == 2) || (direction == -1 && row == 7)) && board.getPiece(forwardMove) == null && board.getPiece(doubleMove) == null) {
                        moves.add(new ChessMove(myPosition, doubleMove, promotion));
                    }
                }
                break;
        }
        return moves;
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