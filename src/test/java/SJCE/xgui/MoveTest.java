package SJCE.xgui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import SJCE.xgui.JPanel.BoardUI;

public class MoveTest {

    private int[] board;

    @Before
    public void setUp() {
        // A simple 64 integer array to represent the chess board
        board = new int[64];
        for (int i = 0; i < 64; i++) {
            board[i] = PiecesUI.NO_PIECE;
        }
    }

    @Test
    public void testConstructorsAndGettersSetters() {
        Move defaultMove = new Move();
        Assert.assertEquals(BoardUI.NO_SQUARE, defaultMove.getSource());
        Assert.assertEquals(BoardUI.NO_SQUARE, defaultMove.getDestination());
        Assert.assertEquals(PiecesUI.NO_PIECE, defaultMove.getPiece());

        Move noPieceMove = new Move(12, 28);
        Assert.assertEquals(12, noPieceMove.getSource());
        Assert.assertEquals(28, noPieceMove.getDestination());
        Assert.assertEquals(PiecesUI.NO_PIECE, noPieceMove.getPiece());

        Move completeMove = new Move(8, 16, PiecesUI.WHITE_PAWN);
        Assert.assertEquals(8, completeMove.getSource());
        Assert.assertEquals(16, completeMove.getDestination());
        Assert.assertEquals(PiecesUI.WHITE_PAWN, completeMove.getPiece());

        completeMove.setPiece(PiecesUI.BLACK_QUEEN);
        completeMove.setCaptured(PiecesUI.WHITE_KNIGHT);
        completeMove.setPromoted(PiecesUI.BLACK_QUEEN);

        Assert.assertEquals(PiecesUI.BLACK_QUEEN, completeMove.getPiece());
        Assert.assertEquals(PiecesUI.WHITE_KNIGHT, completeMove.getCaptured());
        Assert.assertEquals(PiecesUI.BLACK_QUEEN, completeMove.getPromoted());
    }

    @Test
    public void testNormalMoveAndUndo() {
        board[8] = PiecesUI.WHITE_PAWN;
        board[16] = PiecesUI.NO_PIECE;

        Move move = new Move(8, 16, PiecesUI.WHITE_PAWN);
        int type = move.doMove(board);

        Assert.assertEquals(Move.NORMAL_MOVE, type);
        Assert.assertEquals(PiecesUI.NO_PIECE, board[8]);
        Assert.assertEquals(PiecesUI.WHITE_PAWN, board[16]);
        Assert.assertEquals(PiecesUI.NO_PIECE, move.getCaptured());

        int[] affected = move.getAffectedSquares(type);
        Assert.assertArrayEquals(new int[]{8, 16}, affected);

        move.undoMove(board);
        Assert.assertEquals(PiecesUI.WHITE_PAWN, board[8]);
        Assert.assertEquals(PiecesUI.NO_PIECE, board[16]);
    }

    @Test
    public void testNormalMoveCaptureAndUndo() {
        board[10] = PiecesUI.WHITE_KNIGHT;
        board[25] = PiecesUI.BLACK_BISHOP;

        Move move = new Move(10, 25, PiecesUI.WHITE_KNIGHT);
        int type = move.doMove(board);

        Assert.assertEquals(Move.NORMAL_MOVE, type);
        Assert.assertEquals(PiecesUI.BLACK_BISHOP, move.getCaptured());
        Assert.assertEquals(PiecesUI.WHITE_KNIGHT, board[25]);
        Assert.assertEquals(PiecesUI.NO_PIECE, board[10]);

        move.undoMove(board, type);
        Assert.assertEquals(PiecesUI.WHITE_KNIGHT, board[10]);
        Assert.assertEquals(PiecesUI.BLACK_BISHOP, board[25]);
    }

    @Test
    public void testCastleMoveWhiteKingSide() {
        board[4] = PiecesUI.WHITE_KING;  // e1
        board[7] = PiecesUI.WHITE_ROOK;  // h1
        
        Move move = new Move(4, 6, PiecesUI.WHITE_KING); // e1 to g1
        int type = move.doMove(board);

        Assert.assertEquals(Move.CASTLE_MOVE, type);
        // King is moved to g1
        Assert.assertEquals(PiecesUI.NO_PIECE, board[4]);
        Assert.assertEquals(PiecesUI.WHITE_KING, board[6]);
        // Rook is moved from h1 to f1
        Assert.assertEquals(PiecesUI.NO_PIECE, board[7]);
        Assert.assertEquals(PiecesUI.WHITE_ROOK, board[5]);

        int[] affected = move.getAffectedSquares(type);
        Assert.assertArrayEquals(new int[]{4, 6, 7, 5}, affected);

        int undoType = move.undoMove(board);
        Assert.assertEquals(Move.CASTLE_MOVE, undoType);
        Assert.assertEquals(PiecesUI.WHITE_KING, board[4]);
        Assert.assertEquals(PiecesUI.WHITE_ROOK, board[7]);
        Assert.assertEquals(PiecesUI.NO_PIECE, board[5]);
        Assert.assertEquals(PiecesUI.NO_PIECE, board[6]);
    }

    @Test
    public void testCastleMoveBlackQueenSide() {
        board[60] = PiecesUI.BLACK_KING;  // e8
        board[56] = PiecesUI.BLACK_ROOK;  // a8
        
        Move move = new Move(60, 58, PiecesUI.BLACK_KING); // e8 to c8
        int type = move.doMove(board);

        Assert.assertEquals(Move.CASTLE_MOVE, type);
        Assert.assertEquals(PiecesUI.BLACK_KING, board[58]);
        Assert.assertEquals(PiecesUI.BLACK_ROOK, board[59]);

        move.undoMove(board);
        Assert.assertEquals(PiecesUI.BLACK_KING, board[60]);
        Assert.assertEquals(PiecesUI.BLACK_ROOK, board[56]);
    }

    @Test
    public void testEnPassantWhite() {
        board[35] = PiecesUI.WHITE_PAWN; // d5
        board[34] = PiecesUI.BLACK_PAWN; // c5
        
        // white moves d5 to c6 (assuming black pawn just double-jumped to c5)
        Move move = new Move(35, 42, PiecesUI.WHITE_PAWN);
        // Setup board so the destination is empty (en passant square empty)
        int type = move.doMove(board);
        
        Assert.assertEquals(Move.ENPASSANT_MOVE, type);
        Assert.assertEquals(PiecesUI.BLACK_PAWN + Move.ENPASSANT_CAPTURE, move.getCaptured());
        Assert.assertEquals(PiecesUI.WHITE_PAWN, board[42]); // White pawn moved
        Assert.assertEquals(PiecesUI.NO_PIECE, board[35]);   // Old white square
        Assert.assertEquals(PiecesUI.NO_PIECE, board[34]);   // Black pawn removed

        int[] affected = move.getAffectedSquares(type);
        Assert.assertEquals(3, affected.length);

        move.undoMove(board);
        Assert.assertEquals(PiecesUI.WHITE_PAWN, board[35]);
        Assert.assertEquals(PiecesUI.NO_PIECE, board[42]);
        Assert.assertEquals(PiecesUI.BLACK_PAWN, board[34]); // Black pawn restored
    }
}