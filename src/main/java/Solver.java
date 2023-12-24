import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Solver {

    public Board solve(Board board) {
        var visited = new HashSet<>();
        var queue = new LinkedList<Board>();

        queue.add(board);
        Board currentBoard;

        while (!queue.isEmpty()) {
            currentBoard = queue.remove();
            if (visited.contains(currentBoard.encode())) {
                continue;
            }
            visited.add(currentBoard.encode());
            if (currentBoard.isFinished()) {
                return currentBoard;
            }

            var moves = currentBoard.getMovesPerPiece().values().stream().flatMap(List::stream).toList();
            for (Piece move : moves) {
                var newBoard = currentBoard.makeMove(move);
                queue.add(newBoard);
            }
        }
        return null;
    }

}
