package SJCE.xgui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import SJCE.xgui.JPanel.BoardUI;

public class MoveDoMovePath3Test {
    private int[] board;

    @Before
    public void setUp() {
        board = new int[64];
        for (int i = 0; i < 64; i++) { board[i] = PiecesUI.NO_PIECE; }
    }

    @Test
    public void testNormalMovePath() {
        // Path: 1, 2, 4, 5, 7, 8 (Normal Move)
        board[8] = PiecesUI.WHITE_PAWN; // Source square 8
        board[16] = PiecesUI.NO_PIECE;  // Destination square 16
        
        Move move = new Move(8, 16, PiecesUI.WHITE_PAWN);
        int type = move.doMove(board);

        Assert.assertEquals("Expected NORMAL_MOVE", Move.NORMAL_MOVE, type);
    }
}
