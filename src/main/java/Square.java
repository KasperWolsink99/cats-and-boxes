public record Square(int pieceId, int relativeRow, int relativeColumn, Square.Type type) {
    public enum Type {
        NORMAL,
        BOX
    }
}
