package SJCE.xgui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import SJCE.xgui.JPanel.BoardUI;

public class MoveDoMovePath1Test {
    private int[] board;

    @Before
    public void setUp() {
        board = new int[64];
        for (int i = 0; i < 64; i++) { board[i] = PiecesUI.NO_PIECE; }
    }

    @Test
    public void testCastleMovePath() {
        // Path: 1, 2, 3, 8 (Valid Castle Move)
        board[4] = PiecesUI.WHITE_KING;  // Source e1 (square 4)
        board[7] = PiecesUI.WHITE_ROOK;  // Rook at h1 (square 7)
        
        Move move = new Move(4, 6, PiecesUI.WHITE_KING); // e1 to g1
        int type = move.doMove(board);

        Assert.assertEquals("Expected CASTLE_MOVE", Move.CASTLE_MOVE, type);
    }
}
