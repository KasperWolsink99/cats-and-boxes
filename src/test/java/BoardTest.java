import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class BoardTest {

    @Test
    public void testFromString() {
        var exampleBoard = List.of("@abB@", "Aa@b.", ".aAb.", "@cdDd", "cCc@d");

        Board board = Board.from(exampleBoard);

        assertEquals(5, board.rows());
        assertEquals(5, board.cols());
        var cats = board.cats();

        assertTrue(cats.contains(new Cat(0, 0)));
        assertTrue(cats.contains(new Cat(0, 4)));
        assertTrue(cats.contains(new Cat(1, 2)));
        assertTrue(cats.contains(new Cat(3, 0)));
        assertTrue(cats.contains(new Cat(4, 3)));

        assertEquals(4, board.pieces().size());
    }

    @Test
    public void testSimpleMoves1() {
        var board = Board.from(List.of("a.", "aA"));

        var potentialMoves = board.getMovesPerPiece();

        assertEquals(1, potentialMoves.keySet().size());
        assertEquals(3, potentialMoves.values().stream().flatMap(List::stream).toList().size());
    }

    @Test
    public void testSimpleMoves2() {
        var board = Board.from(List.of("a.", "a."));

        var potentialMoves = board.getMovesPerPiece();

        assertEquals(1, potentialMoves.keySet().size());
        assertEquals(7, potentialMoves.values().stream().flatMap(List::stream).toList().size());
    }

    @Test
    public void testComplexMoves1() {
        var board = Board.from(List.of("ab", "aB"));

        var potentialMoves = board.getMovesPerPiece();
        assertEquals(2, potentialMoves.keySet().size());
        for (var value : potentialMoves.values()) {
            assertEquals(1, value.size());
        }
    }

    @Test
    public void pieceCanBePlaced() {
        var board = Board.from(List.of("..", ".."));
        var piece = new PieceBuilder().add(0, 0, Square.Type.NORMAL).add(0, 1, Square.Type.NORMAL).build();

        assertTrue(board.canBePlaced(piece));
    }

    @Test
    public void pieceCannotBePlacedOutOfBounds() {
        var board = Board.from(List.of("..", ".."));
        var piece = new PieceBuilder().add(0, 0, Square.Type.NORMAL).add(0, -1, Square.Type.NORMAL).build();

        assertFalse(board.canBePlaced(piece));
    }

    @Test
    public void pieceCanBePlacedOnItself() {
        var board = Board.from(List.of("aa", ".."));
        var pieces = board.pieces();
        assertEquals(1, pieces.size());
        var piece = pieces.getFirst();
        var movedPiece = piece.rotateCounterClockwise().withLocation(1, 0); // rotate piece and place it in bottom left corner
        assertTrue(board.canBePlaced(movedPiece));
    }

    @Test
    public void pieceCanNotBePlacedOnOther() {
        var board = Board.from(List.of("aa", ".."));
        var otherPiece = new PieceBuilder().add(0, 0, Square.Type.NORMAL).add(0, 1, Square.Type.NORMAL).build();

        assertFalse(board.canBePlaced(otherPiece));
    }

    @Test
    public void pieceCanBePlacedOnCatIfBox() {
        var board = Board.from(List.of("@"));
        var boxPiece = new PieceBuilder().add(0, 0, Square.Type.BOX).build();
        assertTrue(board.canBePlaced(boxPiece));
        var newBoard = board.makeMove(boxPiece);
        assertTrue(newBoard.isFinished());
    }

    @Test
    public void pieceCannotBePlacedOnCatIfNotBox() {
        var board = Board.from(List.of("@"));
        var boxPiece = new PieceBuilder().add(0, 0, Square.Type.NORMAL).build();
        assertFalse(board.canBePlaced(boxPiece));
    }

    @Test
    public void makeMove() {
        var board = Board.from(List.of("aa", ".."));
        var moves = board.getMovesPerPiece().values().stream().flatMap(List::stream).toList();
        var firstMove = moves.getFirst();

        var newBoard = board.makeMove(firstMove);

        var maybePiece1 = newBoard.getPieceAt(1, 0);
        assertTrue(maybePiece1.isPresent());
        var maybePiece2 = newBoard.getPieceAt(1, 1);
        assertTrue(maybePiece2.isPresent());
        assertSame(maybePiece1.get(), maybePiece2.get());
    }

}
