package SJCE.xgui;

import org.junit.Assert;
import org.junit.Test;
import SJCE.xgui.EventObject.MoveEvent;
import SJCE.xgui.JPanel.BoardUI;

public class BottomUpTests {

    @Test
    public void testNotationWithMoveBottomUp() {
        // Step 1 class is Move
        // Step 3 class is Notation (operating ABOVE Move, depending on it)
        
        // Let's create a move using the base class: "e2" to "e4"
        Move initialMove = new Move(12, 28, PiecesUI.WHITE_PAWN); // e2=12, e4=28
        
        // Initialize dependencies that statically rely on XChessFrame.aktion
        if (SJCE.XChessFrame.aktion.enginePromotionFig == null) {
            SJCE.XChessFrame.aktion.enginePromotionFig = "";
        }
        String notationStr = Notation.toString(initialMove);
        Assert.assertNotNull(notationStr);
        Assert.assertTrue(notationStr.startsWith("e2e4"));
        
        // Parse it back from notation using Step 3 class
        Move parsedMove = Notation.toMove("e2e4");
        Assert.assertEquals(12, parsedMove.getSource());
        Assert.assertEquals(28, parsedMove.getDestination());
        
        // Notation edge cases interacting with board logic
        Assert.assertEquals('e', Notation.FILE_CHAR[Notation.toSquare("e4") % BoardUI.FILE_RANK]);
        Assert.assertEquals('4', Notation.RANK_CHAR[Notation.toSquare("e4") / BoardUI.FILE_RANK]);
        
        // Another edge case: "g1f3"
        Move knightMove = Notation.toMove("g1f3");
        Assert.assertEquals(6, knightMove.getSource());
        Assert.assertEquals(21, knightMove.getDestination());
        
        // Initialize dependencies that statically rely on XChessFrame.aktion
        if (SJCE.XChessFrame.aktion.enginePromotionFig == null) {
            SJCE.XChessFrame.aktion.enginePromotionFig = "";
        }
        
        String knightStr = Notation.toString(knightMove);
        Assert.assertTrue(knightStr.startsWith("g1f3"));
    }

    @Test
    public void testMoveEventWithMoveBottomUp() {
        // Step 1 class is Move
        // Step 3 class is MoveEvent (operating ABOVE Move, passing it around)

        // Bottom-up testing of creating the dependent object
        Move moveData = new Move(0, 16, PiecesUI.WHITE_ROOK); // a1 to a3
        
        // Component above is MoveEvent. Uses base moveData
        Object eventSource = new Object();
        MoveEvent event = new MoveEvent(eventSource, moveData);

        // Verification of dependency injected into Step 3 class
        Assert.assertEquals(eventSource, event.getSource());
        Assert.assertEquals(moveData, event.getMove());
        
        // Verify move is correctly returned and accessible
        Move retrievedMove = event.getMove();
        Assert.assertEquals(0, retrievedMove.getSource());
        Assert.assertEquals(16, retrievedMove.getDestination());
        Assert.assertEquals(PiecesUI.WHITE_ROOK, retrievedMove.getPiece());
        
        // Test modifying the state via setter provided by Step 3 component
        Move newMoveData = new Move(63, 61, PiecesUI.BLACK_KING); // Castle short for black
        event.setMove(newMoveData);
        
        Move updatedMove = event.getMove();
        Assert.assertNotEquals(moveData, updatedMove);
        Assert.assertEquals(63, updatedMove.getSource());
        Assert.assertEquals(61, updatedMove.getDestination());
        Assert.assertEquals(PiecesUI.BLACK_KING, updatedMove.getPiece());
    }

    @Test
    public void testNotationAndEventIntegration() {
        // Testing across both Step 3 classes with Step 1 class in the center
        // 1. User types in notation (e.g. from UI)
        Move extractedCommandMove = Notation.toMove("h7h8"); // En Passant or promotion line

        // 2. Put into an event (mocking Engine or UserAgent layer message parsing)
        MoveEvent unifiedEvent = new MoveEvent("Engine", extractedCommandMove);

        // 3. System reads the event out
        Move dispatchMove = unifiedEvent.getMove();

        // 4. Translate back to notation after executing or processing
        if (SJCE.XChessFrame.aktion.enginePromotionFig == null) {
            SJCE.XChessFrame.aktion.enginePromotionFig = "";
        }
        String resultStr = Notation.toString(dispatchMove);

        // Assert integration workflow functions properly bottom-up
        Assert.assertEquals(extractedCommandMove.getSource(), dispatchMove.getSource());
        Assert.assertTrue(resultStr.startsWith("h7h8"));
    }
}