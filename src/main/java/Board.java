import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Board {

    private static final char CAT_SYMBOL = '@';
    private static final char AIR_SYMBOL = '.';

    private final Board previousState;
    private final int rows;
    private final int cols;
    private final List<Piece> pieces;
    private final List<Cat> cats;

    private Board(Board previousState, int rows, int cols, List<Piece> pieces, List<Cat> cats) {
        this.previousState = previousState;
        this.rows = rows;
        this.cols = cols;
        this.pieces = pieces;
        this.cats = cats;
    }

    public static Board from(List<String> stringRepresentation) {
        var cats = new ArrayList<Cat>();
        var pieces = new HashMap<Character, PieceBuilder>();

        for (int row = 0; row < stringRepresentation.size(); row++) {
            var rowEntry = stringRepresentation.get(row);
            for (int col = 0; col < rowEntry.length(); col++) {
                var Char = rowEntry.charAt(col);
                if (Char == CAT_SYMBOL) {
                    cats.add(new Cat(row, col));
                } else if (Character.isLetter(Char)) {
                    var type = Character.isUpperCase(Char) ? Square.Type.BOX : Square.Type.NORMAL;
                    var key = Character.toLowerCase(Char);
                    pieces.computeIfAbsent(key, x -> new PieceBuilder()).add(row, col, type);
                } else if (Char == AIR_SYMBOL) {
                    // do nothing
                } else {
                    throw new IllegalArgumentException(String.format("Character not recognized: '%c'.", Char));
                }
            }
        }
        List<Piece> createdPieces = pieces.values().stream().map(PieceBuilder::build).toList();

        return new Board(null, stringRepresentation.size(), stringRepresentation.getFirst().length(), createdPieces, cats);
    }

    public Map<Piece, List<Piece>> getMovesPerPiece() {
        var res = new HashMap<Piece, List<Piece>>();
        for (var piece : pieces) {
            for (var rotatedVariant : piece.getRotatedVariants()) {
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        var potentialMove = rotatedVariant.withLocation(row, col);
                        if (potentialMove.equals(piece)) { // don't consider not moving the piece as a valid move
                            continue;
                        }
                        if (canBePlaced(potentialMove)) {
                            res.computeIfAbsent(piece, k -> new ArrayList<>()).add(potentialMove);
                        }
                    }
                }
            }
        }
        return res;
    }

    public Board makeMove(Piece piece) {
        if (!canBePlaced(piece)) {
            throw new IllegalArgumentException(String.format("Placing piece '%s' is considered an illegal move!", piece));
        }

        var newPieces = new ArrayList<>(pieces);
        newPieces.removeIf(p -> p.id() == piece.id());
        newPieces.add(piece);

        return new Board(this, rows, cols, newPieces, cats);
    }

    public Optional<Piece> getPieceAt(int row, int col) {
        return pieces.stream().filter(p -> p.getSquarePiece(row, col).isPresent()).findFirst();
    }

    public int getMovesMade() {
        if (previousState == null) {
            return 0;
        }
        return 1 + previousState.getMovesMade();
    }

    public int encode() {
        return this.toString().hashCode();
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public List<Piece> pieces() {
        return new ArrayList<>(pieces);
    }

    public List<Cat> cats() {
        return new ArrayList<>(cats);
    }

    public boolean isCat(int row, int col) {
        return cats.stream().anyMatch(cat -> cat.row() == row && cat.column() == col);
    }

    public boolean isFinished() {
        return cats.stream().map(cat -> getSquareAt(cat.row(), cat.column())).allMatch(Optional::isPresent);
    }

    boolean canBePlaced(Piece piece) {
        for (Square square : piece.getSquares()) {
            var absoluteRow = piece.row() + square.relativeRow();
            var absoluteColumn = piece.col() + square.relativeColumn();

            if (isOutOfBounds(absoluteRow, absoluteColumn)) {
                return false;
            }
            var existingSquare = getSquareAt(absoluteRow, absoluteColumn);
            if (existingSquare.isPresent() && existingSquare.get().pieceId() != square.pieceId()) {
                return false;
            }
            if (isCat(absoluteRow, absoluteColumn) && square.type() != Square.Type.BOX) {
                return false;
            }
        }
        return true;
    }

    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= rows || col < 0 || col >= cols;
    }

    private Optional<Square> getSquareAt(int row, int col) {
        for (Piece p : pieces) {
            var maybeSquare = p.getSquarePiece(row, col);
            if (maybeSquare.isPresent()) {
                return maybeSquare;
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        String[][] board = new String[rows][cols];

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = String.valueOf(AIR_SYMBOL);
            }
        }
        for (Cat cat : cats) {
            board[cat.row()][cat.column()] = String.valueOf(CAT_SYMBOL);
        }
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                var maybePiece = getPieceAt(r, c);
                if (maybePiece.isPresent()) {
                    var piece = maybePiece.get();
                    String pieceString = convertIntToAscii(piece.id());
                    if (piece.getSquarePiece(r, c).orElseThrow().type() == Square.Type.NORMAL) {
                        pieceString = pieceString.toLowerCase();
                    }
                    board[r][c] = pieceString;
                }
            }
        }

        return String.join("\n", Arrays.stream(board).map(col -> String.join("", col)).toList());
    }

    private String convertIntToAscii(int i) {
        if (!(i > -1 && i < 26)) {
            throw new IllegalArgumentException(String.format("Encoding '%d' cannot be converted to ascii character", i));
        }
        return String.valueOf((char) (i + 65));
    }

}
