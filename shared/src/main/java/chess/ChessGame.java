package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public TeamColor teamTurn;
    public ChessBoard gameBoard;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //grab the piece
        ChessPiece thisPiece = gameBoard.getPiece(startPosition);
        if (thisPiece == null) {
            return null;
        }
        //find out which moves don't place its team in check
        Collection<ChessMove> maybeMoves = gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : maybeMoves){
            //make a new board with the move
            ChessPiece temp = gameBoard.getPiece(move.getEndPosition());
            gameBoard.addPiece(startPosition, null);
            gameBoard.addPiece(move.getEndPosition(), thisPiece);
            if(!isInCheck(thisPiece.getTeamColor())){
                validMoves.add(move);
            }
            gameBoard.addPiece(move.getEndPosition(), temp);
            gameBoard.addPiece(startPosition, thisPiece);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (moves == null){
            throw new InvalidMoveException();
        }

        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        if(moves.contains(move) && (teamTurn == piece.getTeamColor())){
            if (move.getPromotionPiece() != null) {
                piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            gameBoard.addPiece(move.getStartPosition(), null);
            gameBoard.addPiece(move.getEndPosition(), piece);
            if(teamTurn == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find the king
        ChessPosition kingPos = null;
        for(int i = 1; i < 9 && kingPos == null; i++){
            for(int j = 1; j < 9 && kingPos == null; j++){
                if(gameBoard.getPiece(new ChessPosition(i, j)) != null) {
                    if (gameBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor && gameBoard.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING) {
                        kingPos = new ChessPosition(i, j);
                    }
                }
            }
        }
        // see if anyone can take him
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                if(gameBoard.getPiece(new ChessPosition(i, j)) != null) {
                    Collection<ChessMove> moves = gameBoard.getPiece(new ChessPosition(i, j)).pieceMoves(gameBoard, new ChessPosition(i, j));
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(position);
                    if(moves != null && !moves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //basically we are going to see if we have any moves, if we do false and otherwise true
        if(isInCheck(teamColor)){
            return false;
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = gameBoard.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(position);
                    if(moves != null && !moves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
