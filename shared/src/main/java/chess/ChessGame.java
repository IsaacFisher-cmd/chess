package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor gameTurn;
    private ChessBoard gameBoard;
    public boolean isOver = false;

    public ChessGame() {
        this.gameTurn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return gameTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        gameTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return gameTurn == chessGame.gameTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameTurn, gameBoard);
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
        Collection<ChessMove> vMoves = new ArrayList<>();
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if(piece == null){
            return vMoves;
        }
        Collection<ChessMove> moves = piece.pieceMoves(gameBoard, startPosition);
        for(ChessMove move : moves){
            ChessPiece tempPiece = gameBoard.getPiece(move.getEndPosition());
            gameBoard.addPiece(move.getEndPosition(), piece);
            gameBoard.addPiece(startPosition, null);
            if(!isInCheck(piece.getTeamColor())){
                vMoves.add(move);
            }
            gameBoard.addPiece(startPosition, piece);
            gameBoard.addPiece(move.getEndPosition(), tempPiece);
        }
        return vMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean valid = false;
        for(ChessMove vMove : validMoves(move.getStartPosition())){
            if(vMove.equals(move)){
                valid = true;
            }
        }
        if(!valid){
            throw new InvalidMoveException();
        }
        if(!gameBoard.getPiece(move.getStartPosition()).getTeamColor().equals(gameTurn)){
            throw new InvalidMoveException();
        }
        if(valid){
            ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
            if(move.getPromotionPiece() == null){
                gameBoard.addPiece(move.getEndPosition(), piece);
                gameBoard.addPiece(move.getStartPosition(), null);
            } else {
                gameBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
                gameBoard.addPiece(move.getStartPosition(), null);
            }

            if(gameTurn.equals(TeamColor.WHITE)){
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = new ChessPosition(1, 1);
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(i, j));
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    kingPos = new ChessPosition(i, j);
                }
            }
        }
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(i, j));
                if(piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                for (ChessMove move : piece.pieceMoves(gameBoard, new ChessPosition(i, j))){
                    if(move.getEndPosition().equals(kingPos)){
                        return true;
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
    public boolean isInMate(TeamColor teamColor){
        int moves = 0;
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(i, j));
                if(piece != null && piece.getTeamColor() == teamColor){
                    if(!validMoves(new ChessPosition(i, j)).isEmpty()){
                        moves++;
                    }
                }
            }
        }
        return moves == 0;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        return isInMate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        return isInMate(teamColor);
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
