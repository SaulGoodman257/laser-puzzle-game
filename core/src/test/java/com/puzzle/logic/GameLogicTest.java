package com.puzzle.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {
    private static String[][] copy(String[][] src){
        String[][] dst=new String[src.length][];
        for(int i=0;i<src.length;i++)dst[i]=src[i].clone();
        return dst;
    }
    @Test
    @DisplayName("Grid with one target ⇒ not win at start")
    void initialStateWithTargets() {
        String[][] grid={{"Laser","Mishen_cp"}};
        GameLogic gl=new GameLogic(grid);

        assertFalse(gl.isWin(), "Presence of untouched targets means not win yet");
        assertTrue(gl.isBotButtonEnabled(), "Bot button should be active while puzzle unsolved");
        assertTrue(gl.getHitTargets().isEmpty(), "No hits recorded initially");
    }
    @Test
    @DisplayName("Grid without targets ⇒ immediate win & disabled bot button")
    void noTargetsImmediateWin() {
        String[][] grid={{"Ser","Ser"}};
        GameLogic gl=new GameLogic(grid);

        assertTrue(gl.isWin(), "If there are no targets the game should start in win state");
        assertFalse(gl.isBotButtonEnabled(), "Bot button disabled after win");
    }
    @Test
    @DisplayName("Hitting each target exactly once ⇒ win")
    void addHitTargetAchievesWin() {
        String[][] grid={{"Laser","Mishen_a"},{"Ser","Mishen_b"}};
        GameLogic gl=new GameLogic(grid);
        assertFalse(gl.isWin());
        gl.addHitTarget("0_1_anySuffix");
        assertFalse(gl.isWin(), "Still one target remaining");
        assertEquals(Set.of("0_1_anySuffix"), gl.getHitTargets());
        assertTrue(gl.isBotButtonEnabled());
        gl.addHitTarget("1_1_x");
        assertTrue(gl.isWin());
        assertFalse(gl.isBotButtonEnabled());
        assertEquals(2, gl.getHitTargets().size());
    }
    @Test
    @DisplayName("Duplicate hit must not be counted twice")
    void duplicateHitsIgnored() {
        String[][] grid={{"Mishen_x"}};
        GameLogic gl=new GameLogic(grid);
        gl.addHitTarget("0_0_key1");
        assertTrue(gl.isWin());
        gl.addHitTarget("0_0_anotherKey");
        assertTrue(gl.isWin());
        assertEquals(2, gl.getHitTargets().size(), "Hit set keeps both keys, but win counted only once");
    }
    @Test
    @DisplayName("Bot flags getters/setters work independently")
    void botFlagsRoundTrip() {
        GameLogic gl=new GameLogic(new String[][]{{"Ser"}});
        assertFalse(gl.isBotSolved());
        assertFalse(gl.isBotUsed());
        gl.setBotSolved(true);
        gl.setBotUsed(true);
        assertTrue(gl.isBotSolved());
        assertTrue(gl.isBotUsed());
        gl.setBotSolved(false);
        assertFalse(gl.isBotSolved());
        assertTrue(gl.isBotUsed());
    }
    @Test
    @DisplayName("updateGrid resets hit list and recalculates win condition")
    void updateGridResetsState() {
        String[][] g1={{"Ser"}};
        GameLogic gl=new GameLogic(g1);
        assertTrue(gl.isWin());
        String[][] g2={{"Laser","Mishen_cc"}};
        gl.updateGrid(g2);
        assertFalse(gl.isWin(), "New grid has a target; win flag must reset");
        assertTrue(gl.getHitTargets().isEmpty(), "Hits list must be cleared on update");
        assertTrue(gl.isBotButtonEnabled());
        gl.addHitTarget("0_1_k");
        assertTrue(gl.isWin());
    }
    @DisplayName("copyGrid делает глубокую копию")
    @Test void copyGridIsDeep() {
        String[][] src = { {"Mishen_x"} };
        GameLogic gl = new GameLogic(src);
        src[0][0] = "Ser";
        assertEquals("Mishen_x", gl.getGrid()[0][0],
                "Внутренняя копия должна быть неизменна");
    }
    @Test @DisplayName("Constructor handles 100×100 empty grid < 50ms")
    void bigEmptyGridConstructorPerformance() {
        String[][] big=new String[100][100];
        for(String[] row:big) java.util.Arrays.fill(row, "Ser");

        assertTimeoutPreemptively(Duration.ofMillis(50), () -> new GameLogic(big));
    }
    @Test @DisplayName("Manual botButton disable persists through addHitTarget")
    void botButtonManualDisable() {
        String[][] grid={{"Mishen_x"}};
        GameLogic gl=new GameLogic(grid);
        gl.setBotButtonEnabled(false);
        assertFalse(gl.isBotButtonEnabled());
        gl.addHitTarget("0_0_k");
        assertFalse(gl.isBotButtonEnabled(), "Manual disable should persist (still false)");
    }
    @Test @DisplayName("updateGrid with fewer targets triggers immediate win if hits ≥ new total")
    void updateGridFewerTargets() {
        String[][] g1={{"Mishen_a"},{"Mishen_b"}};
        GameLogic gl=new GameLogic(g1);
        gl.addHitTarget("0_0_k");
        gl.addHitTarget("1_0_k");
        assertTrue(gl.isWin());
        String[][] g2={{"Ser"}};
        gl.updateGrid(g2);
        assertTrue(gl.isWin(), "Grid without targets should result in immediate win");
    }
    @Test @DisplayName("Targets counted only when startsWith(\"Mishen\")")
    void mixedTargetTokens() {
        String[][] grid={{"Mishen_good"},{"Mishenka"},{"xMishen"}};
        GameLogic gl=new GameLogic(grid);
        assertFalse(gl.isWin());
        gl.addHitTarget("0_0_a");
        assertFalse(gl.isWin());
        gl.addHitTarget("1_0_b");
        assertTrue(gl.isWin());
    }
    @Test
    @DisplayName("Пустой grid (0x0) — win=true, ничего не ломается")
    void emptyGridIsWin() {
        String[][] grid = new String[0][0];
        GameLogic gl = new GameLogic(grid);
        assertTrue(gl.isWin());
        assertTrue(gl.getHitTargets().isEmpty());
    }
    @Test
    @DisplayName("Нет мишеней — победа сразу")
    void noTargetsGrid() {
        String[][] grid = {{"Ser"}};
        GameLogic gl = new GameLogic(grid);
        assertTrue(gl.isWin());
    }
    @Test
    @DisplayName("setBotButtonEnabled: ручное управление не сбрасывается checkWinCondition")
    void botButtonManualOverride() {
        String[][] grid = {{"Mishen"}};
        GameLogic gl = new GameLogic(grid);
        gl.setBotButtonEnabled(false);
        assertFalse(gl.isBotButtonEnabled());
        gl.setBotButtonEnabled(true);
        assertTrue(gl.isBotButtonEnabled());
    }
    @Test
    @DisplayName("updateGrid очищает все хиты и win-флаг")
    void updateGridResetsHitsAndWin() {
        String[][] grid = {{"Mishen"}};
        GameLogic gl = new GameLogic(grid);
        gl.addHitTarget("0_0_k");
        assertTrue(gl.isWin());
        String[][] newGrid = {{"Mishen"},{"Mishen"}};
        gl.updateGrid(newGrid);
        assertFalse(gl.isWin());
        assertTrue(gl.getHitTargets().isEmpty());
    }
    @Test
    @DisplayName("Несколько мишеней, но попадание только по одной — не win")
    void notAllTargetsHit() {
        String[][] grid = {{"Mishen_a", "Mishen_b"}};
        GameLogic gl = new GameLogic(grid);
        gl.addHitTarget("0_0_x");
        assertFalse(gl.isWin());
        gl.addHitTarget("1_0_x");
        assertTrue(gl.isWin());
    }
    @Test
    @DisplayName("updateGrid: большой грид не тормозит (< 100ms)")
    void updateGridPerformance() {
        String[][] big = new String[100][100];
        for(String[] row:big) java.util.Arrays.fill(row, "Mishen");
        GameLogic gl = new GameLogic(big);
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> gl.updateGrid(big));
    }
    @Test
    @DisplayName("setBotButtonEnabled не влияет на win")
    void botButtonEnabledIndependence() {
        String[][] grid = {{"Mishen"}};
        GameLogic gl = new GameLogic(grid);
        gl.setBotButtonEnabled(false);
        assertFalse(gl.isWin());
    }

}
