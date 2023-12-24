import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PieceBuilder {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private final int pieceId;
    private final List<Square> squares;

    public PieceBuilder() {
        this.pieceId = counter.getAndIncrement();
        squares = new ArrayList<>();
    }

    public PieceBuilder add(int row, int col, Square.Type type) {
        squares.add(new Square(pieceId, row, col, type));
        return this;
    }

    public Piece build() {
        return new Piece(pieceId, squares);
    }
}
