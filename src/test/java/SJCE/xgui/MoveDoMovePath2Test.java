package SJCE.xgui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import SJCE.xgui.JPanel.BoardUI;

public class MoveDoMovePath2Test {
    private int[] board;

    @Before
    public void setUp() {
        board = new int[64];
        for (int i = 0; i < 64; i++) { board[i] = PiecesUI.NO_PIECE; }
    }

    @Test
    public void testEnPassantMovePath() {
        // Path: 1, 2, 4, 5, 6, 8 (Valid En Passant Capture)
        board[35] = PiecesUI.WHITE_PAWN; // Source d5 (square 35)
        board[34] = PiecesUI.BLACK_PAWN; // Adjacent black pawn c5 (square 34)
        
        // white moves d5 to c6 (square 42)
        Move move = new Move(35, 42, PiecesUI.WHITE_PAWN);
        int type = move.doMove(board);

        Assert.assertEquals("Expected ENPASSANT_MOVE", Move.ENPASSANT_MOVE, type);
    }
}
