import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PieceTest {

    private PieceBuilder pieceBuilder;

    @BeforeEach
    public void setUp() {
        pieceBuilder = new PieceBuilder();
        pieceBuilder.add(-3, 3, Square.Type.NORMAL);
        pieceBuilder.add(-4, 3, Square.Type.NORMAL);
        pieceBuilder.add(-3, 4, Square.Type.BOX);
    }

    @Test
    public void pieceInitialisationNormalises() {
        Piece piece = pieceBuilder.build();
        var squares = piece.getSquares();

        assertEquals(3, squares.size());
        var squareOne = squares.getFirst();
        assertEquals(0, squareOne.relativeRow());
        assertEquals(0, squareOne.relativeColumn());

        var squareTwo = squares.get(1);
        assertEquals(-1, squareTwo.relativeRow());
        assertEquals(0, squareTwo.relativeColumn());

        var squareThree = squares.get(2);
        assertEquals(0, squareThree.relativeRow());
        assertEquals(1, squareThree.relativeColumn());
    }

    @Test
    public void getSquareWorks() {
        Piece piece = pieceBuilder.build();
        var maybeSquare = piece.getSquarePiece(-3, 3);
        assertTrue(maybeSquare.isPresent());
        assertSame(Square.Type.NORMAL, maybeSquare.get().type());

        var maybeSquare2 = piece.getSquarePiece(-4, 3);
        assertTrue(maybeSquare2.isPresent());
        assertSame(Square.Type.NORMAL, maybeSquare2.get().type());

        var maybeSquare3 = piece.getSquarePiece(-3, 4);
        assertTrue(maybeSquare3.isPresent());
        assertSame(Square.Type.BOX, maybeSquare3.get().type());
    }

    @Test
    public void moveSquareWorks() {
        Piece piece = pieceBuilder.build();
        Piece movedPiece = piece.withLocation(5, 5);

        var originalSquares = piece.getSquares();
        var movedSquares = movedPiece.getSquares();
        assertEquals(originalSquares, movedSquares); // TODO: change naming so it is clear these are relative square coordinates.

        var maybePiece = movedPiece.getSquarePiece(5, 6);
        assertTrue(maybePiece.isPresent());
        assertSame(Square.Type.BOX, maybePiece.get().type());
    }

    @Test
    public void rotationRelativeCords() {
        Piece piece = pieceBuilder.build();
        Piece rotatedPiece = piece.rotateCounterClockwise();

        var squares = rotatedPiece.getSquares();
        assertEquals(3, squares.size());

        var squareOne = squares.getFirst();
        assertEquals(0, squareOne.relativeRow());
        assertEquals(0, squareOne.relativeColumn());

        var squareTwo = squares.get(1);
        assertEquals(0, squareTwo.relativeRow());
        assertEquals(-1, squareTwo.relativeColumn());

        var squareThree = squares.get(2);
        assertEquals(-1, squareThree.relativeRow());
        assertEquals(0, squareThree.relativeColumn());
    }

    @Test
    public void rotationAbsoluteCords() {
        Piece piece = pieceBuilder.build();
        Piece rotatedPiece = piece.rotateCounterClockwise();

        var maybeSquare1 = rotatedPiece.getSquarePiece(-3,3);
        assertTrue(maybeSquare1.isPresent());
        assertSame(Square.Type.NORMAL, maybeSquare1.get().type());

        var maybeSquare2 = rotatedPiece.getSquarePiece(-3,2);
        assertTrue(maybeSquare2.isPresent());
        assertSame(Square.Type.NORMAL, maybeSquare2.get().type());

        var maybeSquare3 = rotatedPiece.getSquarePiece(-4,3);
        assertTrue(maybeSquare3.isPresent());
        assertSame(Square.Type.BOX, maybeSquare3.get().type());
    }
}
