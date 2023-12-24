import java.util.List;
import java.util.Optional;

public class Piece {
    private final List<Square> squares;
    private final int row;
    private final int col;
    private final int id;

    public Piece(int id, List<Square> squares) {
        this.id = id;
        if (squares.isEmpty()) {
            throw new IllegalArgumentException("Piece must consist of at least one square.");
        }
        this.row = squares.getFirst().relativeRow();
        this.col = squares.getFirst().relativeColumn();
        this.squares = normalise(squares);
    }

    private Piece(int id, List<Square> squares, int row, int col) {
        this.id = id;
        this.squares = squares;
        this.row = row;
        this.col = col;
    }

    public Piece withLocation(int row, int col) {
        return new Piece(id, squares, row, col);
    }

    public Piece rotateCounterClockwise() {
        var newSquares = squares.stream()
                .map(square -> new Square(square.pieceId(), square.relativeColumn() * -1, square.relativeRow(), square.type()))
                .toList();
        return new Piece(id, newSquares, row, col);
    }

    public List<Piece> getRotatedVariants() {
        // lazy implementation
        return List.of(this,
                this.rotateCounterClockwise(),
                this.rotateCounterClockwise().rotateCounterClockwise(),
                this.rotateCounterClockwise().rotateCounterClockwise().rotateCounterClockwise());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Piece piece = (Piece) o;

        if (row != piece.row) {
            return false;
        }
        if (col != piece.col) {
            return false;
        }
        if (id != piece.id) {
            return false;
        }
        return squares.equals(piece.squares);
    }

    @Override
    public int hashCode() {
        int result = squares.hashCode();
        result = 31 * result + row;
        result = 31 * result + col;
        result = 31 * result + id;
        return result;
    }

    public List<Square> getSquares() {
        return squares;
    }

    public Optional<Square> getSquarePiece(int row, int col) {
        return squares.stream()
                .filter(square -> this.row + square.relativeRow() == row && this.col + square.relativeColumn() == col)
                .findFirst();
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public int id() {
        return id;
    }

    private static List<Square> normalise(List<Square> squares) {
        var first = squares.getFirst();
        return squares.stream()
                .map(square -> new Square(square.pieceId(),
                        square.relativeRow() - first.relativeRow(),
                        square.relativeColumn() - first.relativeColumn(),
                        square.type()))
                .toList();
    }

}
