import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class SolverTest {

    @Test
    public void solveStarter1() {
        Board board = Board.from(List.of("@abB@", "Aa@b.", ".aAb.", "@cdDd", "cCc@d"));

        Solver solver = new Solver();
        var res = solver.solve(board);

        assertEquals(4, res.getMovesMade());
    }

    @Test
    public void solveMaster39() {
        Board board = Board.from(List.of(".Aaab", "@a@bB", "cc.db", "C@Dd@", "c@.dD"));

        Solver solver = new Solver();
        var res = solver.solve(board);
        assertEquals(7, res.getMovesMade());
    }

    @Test
    public void solveMaster42() {
        Board board = Board.from(List.of("a.@.b", "AabbB", "acDd@", "@C@dD", "cc@d."));

        Solver solver = new Solver();
        var res = solver.solve(board);
        assertEquals(11, res.getMovesMade());
    }

    @Test
    public void solveWizard60() {
        Board board = Board.from(List.of("@aAa.", ".b@a@", "bBCc.", "@bdcC", "ddDc@"));

        Solver solver = new Solver();
        var res = solver.solve(board);

        assertEquals(33, res.getMovesMade());
    }
}
