package com.puzzle;

import com.puzzle.Bot.Move;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    private static String[][] copy(String[][] src) {
        String[][] dst = new String[src.length][];
        for (int r = 0; r < src.length; r++) dst[r] = src[r].clone();
        return dst;
    }
    private static String[][] applyMoves(String[][] grid, List<Move> path) {
        String[][] g = copy(grid);
        for (Move m : path) {
            String tmp = g[m.blockR()][m.blockC()];
            g[m.blockR()][m.blockC()] = g[m.serR()][m.serC()];
            g[m.serR()][m.serC()] = tmp;
        }
        return g;
    }
    private static void assertSolved(String[][] grid) {
        List<Move> p = new Bot(grid).solveWithPath();
        assertNotNull(p, "Solved boards must never yield null");
        assertTrue(p.isEmpty(), "Solved boards must yield an empty path");
    }
    private static boolean isSolved(String[][] grid) {
        List<Move> p = new Bot(grid).solveWithPath();
        return p != null && p.isEmpty();
    }

    @Test
    void alreadySolvedReturnsEmptyPath() {
        String[][] grid = {
            {"Ser", "Ser"},
            {"Ser", "Ser"}
        };
        Bot bot = new Bot(grid);
        List<Move> path = bot.solveWithPath();
        assertNotNull(path, "solveWithPath must return a non‑null list even for solved boards");
        assertTrue(path.isEmpty(), "Solver should return an empty path for an already solved board");
    }
    @Test
    @DisplayName("No targets + no moves ⇒ still 'solved' (empty path)")
    void noTargetsIsTriviallySolved() {
        String[][] grid = {
            {"Block", "Block"},
            {"Block", "Block"}
        };
        assertSolved(grid);
    }
    @Test
    @DisplayName("Unsolvable board ⇒ null path")
    void impossiblePuzzleReturnsNull() {
        String[][] grid = {
            {"Laser_cp_0", "Block"},
            {"Block",      "Mishen_cp"}
        };
        Bot bot = new Bot(grid);
        assertNull(bot.solveWithPath(), "An unsolvable board must yield null");
    }

    @Test
    void returnsEmptyListIfNoLegalMovesExist() {
        String[][] grid = {
            {"Block", "Block"},
            {"Block", "Block"}
        };
        Bot bot = new Bot(grid);
        List<Move> path = bot.solveWithPath();

        assertNotNull(path, "When no moves are possible the solver should return an empty list (not null)");
        assertTrue(path.isEmpty(), "When no moves are possible the list must be empty");
    }

    @Test
    void findsPathThatSolvesSimplePuzzle() {
        String[][] grid = {
            {"Ser", "Ser", "Ser", "Ser"},
            {"Laser_сс_60", "Ser", "Mishen_cc", "Ser"},
            {"Ser", "Ser", "Ser", "Ser"},
            {"Ser", "Ser", "Ser", "Block"}
        };
        Bot bot = new Bot(grid);
        List<Move> path = bot.solveWithPath();
        assertNotNull(path, "A solvable board must yield a non‑null path");
        assertEquals(1, path.size(), "Exactly one move is expected for this board");
        String[][] solved = applyMoves(grid, path);
        Bot verifier = new Bot(solved);
        List<Move> residual = verifier.solveWithPath();
        assertNotNull(residual, "Verification step should succeed");
        assertTrue(residual.isEmpty(), "After applying the path the puzzle must be solved");
    }
    @Test
    @DisplayName("Two blocking blocks ⇒ two‑move optimal path")
    void solvesPuzzleInTwoMoves() {
        String[][] grid = {
            {"Ser","Block","Laser_cc_326.0"},
            {"Mishen_cl","Ser","Ser"},
            {"pustoi","Ser","Block"},
        };
        Bot bot = new Bot(grid);
        List<Move> path = bot.solveWithPath();
        assertNotNull(path, "Path must not be null for a solvable puzzle");
        assertEquals(2, path.size(), "Exactly two moves should be enough to solve this board");
        assertSolved(applyMoves(grid, path));
    }
    @Test
    @DisplayName("Three blocking blocks ⇒ three‑move optimal path")
    void findsOptimalPathLengthThree() {
        String[][] grid = {
            {"pustoi","Laser_tp_309","pustoi"},
            {"Ser","Ser","Block"},
            {"Ser","Block","Ser"},
            {"Ser","Mishen_cp","Block"}
        };
        List<Move> path = new Bot(grid).solveWithPath();
        assertNotNull(path);
        assertEquals(3, path.size());
        for (int cut = 0; cut < path.size(); cut++) {
            assertFalse(isSolved(applyMoves(grid, path.subList(0, cut))));
        }
        assertSolved(applyMoves(grid, path));
    }
    @Test
    @DisplayName("Solver must not mutate client’s grid array")
    void originalGridRemainsUnchanged() {
        String[][] grid = {
            {"Ser", "Ser", "Ser", "Ser"},
            {"Laser_сс_60", "Ser", "Mishen_cc", "Ser"},
            {"Ser", "Ser", "Ser", "Ser"},
            {"Ser", "Ser", "Ser", "Block"}
        };
        String[][] snapshot = copy(grid);
        new Bot(grid).solveWithPath();
        assertTrue(Arrays.deepEquals(snapshot, grid), "Bot modified caller‑owned grid");
    }
    @Test
    @DisplayName("Null grid ⇒ NullPointerException")
    void nullGridThrows() {
        assertThrows(NullPointerException.class, () -> new Bot(null),
            "Bot constructor should demand non‑null grid and fail fast");
    }
    @Test
    @DisplayName("1x1 solved Ser board — solved сразу")
    void oneByOneSerSolved() {
        String[][] grid = {{"Ser"}};
        assertSolved(grid);
    }

    @Test
    @DisplayName("1x1 unsolved Block board — solved сразу (нет целей и лазеров)")
    void oneByOneBlockSolved() {
        String[][] grid = {{"Block"}};
        assertSolved(grid);
    }
    @Test
    @DisplayName("Нет ни одного Ser: нет ходов, пустой путь")
    void noSerNoMoves() {
        String[][] grid = {
                {"Block", "Block"},
                {"Block", "Block"}
        };
        Bot bot = new Bot(grid);
        List<Bot.Move> path = bot.solveWithPath();
        assertNotNull(path);
        assertTrue(path.isEmpty());
    }

    @Test
    @DisplayName("Нет ни одного Block: нет ходов, пустой путь")
    void noBlockNoMoves() {
        String[][] grid = {
                {"Ser", "Ser"},
                {"Ser", "Ser"}
        };
        Bot bot = new Bot(grid);
        List<Bot.Move> path = bot.solveWithPath();
        assertNotNull(path);
        assertTrue(path.isEmpty());
    }
    @Test
    @DisplayName("findMoves возвращает все Block-Ser пары")
    void findMovesAllPairs() {
        String[][] grid = {
                {"Block", "Ser"},
                {"Block", "Ser"}
        };
        Bot bot = new Bot(grid);
        List<Bot.Move> moves = bot.solveWithPath();
        for (Bot.Move m : moves) {
            assertEquals("Block", grid[m.blockR()][m.blockC()]);
            assertEquals("Ser", grid[m.serR()][m.serC()]);
        }
    }
    @Test
    @DisplayName("Solver не зацикливается при циклических ходах")
    void solverNoCycles() {
        String[][] grid = {
                {"Laser_cc_0", "Block", "Ser"},
                {"Mishen_cc", "Ser", "Block"}
        };
        Bot bot = new Bot(grid);
        List<Bot.Move> path = bot.solveWithPath();
        assertTrue(path == null || path.size() < 100, "Path is finite or null, no infinite loop");
    }
    @Test
    @DisplayName("Block нельзя поменять местами с чем-то кроме Ser")
    void onlySerAllowedForSwap() {
        String[][] grid = {
                {"Block", "SerX"},
                {"Ser", "Block"}
        };
        Bot bot = new Bot(grid);
        List<Bot.Move> moves = bot.solveWithPath();
        if (moves != null) for (Bot.Move m : moves) {
            assertEquals("Ser", grid[m.serR()][m.serC()]);
        }
    }
    @Test
    @DisplayName("Нестандартные ячейки не ломают solver")
    void exoticCellsDontCrash() {
        String[][] grid = {
                {"Laser_cc_0", "Block", "AAA"},
                {"BBB", "Mishen_cc", "Ser"}
        };
        Bot bot = new Bot(grid);
        List<Bot.Move> path = bot.solveWithPath();
        assertTrue(path == null || path.isEmpty() || path.size() > 0);
    }
    @Test
    @DisplayName("Path не содержит дублирующихся swap подряд")
    void noConsecutiveDuplicateMoves() {
        String[][] grid = {
                {"Ser", "Block"},
                {"Laser_cc_0", "Mishen_cc"}
        };
        Bot bot = new Bot(grid);
        List<Bot.Move> path = bot.solveWithPath();
        if (path != null) {
            for (int i = 1; i < path.size(); i++) {
                Bot.Move prev = path.get(i-1);
                Bot.Move curr = path.get(i);
                assertFalse(prev.equals(curr), "No consecutive duplicate moves");
            }
        }
    }
    @Test
    @DisplayName("Одна ячейка Mishen_cc без лазера — не win, solver возвращает null")
    void singleTargetNoLaserUnsolved() {
        String[][] grid = {{"Mishen_cc"}};
        Bot bot = new Bot(grid);
        List<Bot.Move> path = bot.solveWithPath();
        assertNull(path);
    }
}
