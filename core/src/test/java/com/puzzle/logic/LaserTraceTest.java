package com.puzzle.logic;

import com.puzzle.logic.Segment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LaserTraceTest {

    private static final float SIZE = 30f;
    private static final float GAP  = 0f;

    private static LaserTrace mkDefault(GameLogic logic, String[][] colMajorGrid) {
        return new LaserTrace(logic, colMajorGrid, SIZE, GAP, 0, 0);
    }
    private static LaserTrace mk(GameLogic logic, String[][] colMajor) {
        return new LaserTrace(logic, colMajor, SIZE, GAP, 0, 0);
    }
    private static String[][] toColMajor(String[][] rows) {
        int h = rows.length;
        int w = rows[0].length;
        String[][] out = new String[w][h];
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) out[x][y] = rows[y][x];
        return out;
    }
    @Test @DisplayName("Straight laser exits grid – single segment, no hits")
    void straightExit() {
        String[][] rows = {{"Ser","Ser","Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt   = mk(logic, grid);

        List<Segment> segs = lt.trace(0f, SIZE/2, 0f, new HashSet<>());
        assertEquals(1, segs.size());
        assertTrue(logic.isWin());
    }
    @Test @DisplayName("Block reflects horizontally – two segments")
    void blockReflection() {
        String[][] rows = {{"Ser","Block","Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt   = mk(logic, grid);
        List<Segment> segs = lt.trace(0f, SIZE/2, 0f, new HashSet<>());
        assertEquals(2, segs.size());
    }
    @Test @DisplayName("Laser hits Mishen_cp exactly once")
    void hitsTargetOnce() {
        String[][] rows = {{"Ser","Mishen_cp"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt   = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        lt.trace(0f, SIZE/2, 0f, hits);
        String expected = "1_0_mishen_cp";
        assertTrue(hits.contains(expected));
        assertTrue(logic.isWin());
        assertEquals(1, hits.size());
        lt.trace(0f, SIZE/2, 0f, hits);
        assertEquals(1, hits.size());
    }
    @Test @DisplayName("Block reflects vertically – two segments")
    void blockVerticalReflection() {
        String[][] rows = {{"Ser"}, {"Block"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt   = mk(logic, grid);
        float sx = SIZE/2, sy = -1;
        List<Segment> segs = lt.trace(sx, sy, 90f, new HashSet<>());
        assertEquals(2, segs.size(), "2 отрезка: до блока и после отражения вверх");
    }
    @Test @DisplayName("Hits Mishen_cl from left with correct key")
    void hitsLeftOffsetTarget() {
        String[][] rows = {{"Mishen_cl"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt   = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        lt.trace(1f, SIZE/2, 0f, hits);
        assertTrue(hits.contains("0_0_mishen_cl"));
        assertEquals(1, hits.size());
        assertTrue(logic.isWin());
    }
    @Test @DisplayName("Grid offset + spacing handled correctly (center hit cp)")
    void gridOffsetSpacing() {
        float spacing = 5f, cell = 20f;
        float gx = 100f, gy = 50f;
        String[][] rows = {{"Mishen_cp"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = new LaserTrace(logic, grid, cell, spacing, gx, gy);
        Set<String> hits = new HashSet<>();
        float sx = gx + 1f;
        float sy = gy + cell/2;
        lt.trace(sx, sy, 0f, hits);
        assertTrue(hits.contains("0_0_mishen_cp"));
    }
    @Test
    @DisplayName("Laser passes through Ser and pustoi cells")
    void laserPassesThroughEmptyCells() {
        String[][] rows = {{"Ser", "pustoi", "Mishen_cc"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mkDefault(logic, grid);
        Set<String> hits = new HashSet<>();
        List<Segment> segs = lt.trace(1f, SIZE / 2f, 0f, hits);
        assertEquals(1, segs.size(), "Expected 1 segment as laser passes through empty cells to target or edge");
        assertTrue(hits.contains("2_0_mishen_cc"), "Laser should hit the target after passing Ser and pustoi");
        assertEquals(1, hits.size());
    }
    @Test
    @DisplayName("Попадание в мишень с разной позицией")
    void hitsAllMishenTypes() {
        for (String pos : List.of("nl","nn","np","cl","cc","cp","tl","tn","tp")) {
            String[][] rows = {{ "Mishen_" + pos }};
            String[][] grid = toColMajor(rows);
            GameLogic logic = new GameLogic(grid);
            LaserTrace lt = mk(logic, grid);
            Set<String> hits = new HashSet<>();
            // центр, в зависимости от позиции
            float base = SIZE/2;
            float offX = 0, offY = 0;
            switch (pos) {
                case "nl" -> { offX=-SIZE/3; offY=-SIZE/3; }
                case "nn" -> { offY=-SIZE/3; }
                case "np" -> { offX=SIZE/3; offY=-SIZE/3; }
                case "cl" -> { offX=-SIZE/3; }
                case "cp" -> { offX=SIZE/3; }
                case "tl" -> { offX=-SIZE/3; offY=SIZE/3; }
                case "tn" -> { offY=SIZE/3; }
                case "tp" -> { offX=SIZE/3; offY=SIZE/3; }
            }
            lt.trace(base+offX, base+offY, 0f, hits);
            String expKey = String.format("0_0_mishen_%s", pos);
            assertTrue(hits.contains(expKey));
            assertTrue(logic.isWin());
        }
    }
    @Test @DisplayName("Нет попадания по мишени, если вне толеранса")
    void missTargetDueToTolerance() {
        String[][] rows = {{"Mishen_cp"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        float sx = 0f;
        float sy = SIZE - 0.1f;
        lt.trace(sx, sy, 0f, hits);
        assertFalse(hits.contains("0_0_mishen_cp"));
        assertFalse(logic.isWin());
    }
    @Test @DisplayName("Блок сбоку, лазер отражается по X")
    void reflectX() {
        String[][] rows = {{"Ser", "Block"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        List<Segment> segs = lt.trace(0f, SIZE/2, 0f, new HashSet<>());
        assertEquals(2, segs.size());
        Segment s2 = segs.get(1);
        assertTrue(s2.ex() < s2.sx());
    }
    @Test @DisplayName("Ячейка с неизвестным значением не ломает трассировку")
    void cellWithUnknownValue() {
        String[][] rows = {{"Ser", "XXX", "Mishen_cp"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        List<Segment> segs = lt.trace(0f, SIZE/2, 0f, hits);
        assertTrue(hits.contains("2_0_mishen_cp"));
    }
    @Test
    @DisplayName("Laser выходит сразу из сетки (1x1) — одна вершина, один сегмент, win если Ser")
    void laserImmediatelyExitsSmallGrid() {
        String[][] rows = {{"Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        List<Segment> segs = lt.trace(-10f, -10f, 45f, new HashSet<>());
        assertEquals(1, segs.size());
        assertTrue(logic.isWin());
    }

    @Test
    @DisplayName("Отражение по диагонали, смена направления по обеим осям")
    void diagonalReflectionBothAxes() {
        String[][] rows = {
            {"Block","Ser"},
            {"Ser", "Ser"}
        };
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        List<Segment> segs = lt.trace(SIZE/2, SIZE/2, 135f, new HashSet<>());
        assertTrue(segs.size() > 1, "Должно быть хотя бы два сегмента (отражение)");
    }
    @Test
    @DisplayName("Попадание в Mishen_tn чуть вне tolerance не засчитывается")
    void missTargetJustOutsideTolerance() {
        String[][] rows = {{"Mishen_tn"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        float center = SIZE / 2;
        float offY = SIZE/3;
        float y = center + offY + 7.1f;
        lt.trace(center, y, 0f, hits);
        assertFalse(hits.contains("0_0_mishen_tn"));
    }
    @Test
    @DisplayName("Проход лазера через угол сетки (угловой старт)")
    void laserThroughCorner() {
        String[][] rows = {{"Ser","Ser"},{"Ser","Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        List<Segment> segs = lt.trace(0f, 0f, 45f, new HashSet<>());
        assertEquals(1, segs.size());
        Segment s = segs.get(0);
        assertTrue(s.ex() > s.sx() && s.ey() > s.sy(), "Лазер ушел вправо-вниз");
    }

    @Test
    @DisplayName("Старт внутри блока: не вылетает за пределы, корректно отражается")
    void laserStartsInsideBlock() {
        String[][] rows = {{"Block","Ser"},{"Ser","Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        List<Segment> segs = lt.trace(SIZE/2, SIZE/2, 0f, new HashSet<>());
        assertTrue(segs.size() > 0, "Всегда хотя бы один сегмент");
    }
    @Test
    @DisplayName("Ветка: если (nx - gridStartX) % (cellSize + cellSpacing) > cellSize — ci становится -1")
    void traceCiBecomesMinusOne() {
        String[][] rows = {{"Ser","Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        float nx = SIZE + SIZE + 1f;
        List<Segment> segs = lt.trace(nx, SIZE/2, 0f, new HashSet<>());
        assertEquals(1, segs.size());
    }

    @Test
    @DisplayName("Ветка: если (ny - gridStartY) % (cellSize + cellSpacing) > cellSize — cj становится -1")
    void traceCjBecomesMinusOne() {
        String[][] rows = {{"Ser"}, {"Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        float ny = SIZE + SIZE + 1f;
        List<Segment> segs = lt.trace(SIZE/2, ny, 90f, new HashSet<>());
        assertEquals(1, segs.size());
    }
    @Test
    @DisplayName("Старт на границе между двумя ячейками, лазер идет по оси")
    void laserStartsOnCellBorder() {
        String[][] rows = {{"Ser", "Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        float sx = SIZE;
        List<Segment> segs = lt.trace(sx, SIZE/2, 0f, new HashSet<>());
        assertEquals(1, segs.size());
    }

    @Test
    @DisplayName("Многоразовые трассировки не дублируют попадания (hitTargets)")
    void repeatedTracesNoDuplicateHits() {
        String[][] rows = {{"Ser", "Mishen_cp"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        lt.trace(0f, SIZE/2, 0f, hits);
        int before = hits.size();
        lt.trace(0f, SIZE/2, 0f, hits);
        assertEquals(before, hits.size());
    }
    @Test
    @DisplayName("Grid spacing > 0 не ломает трассировку")
    void spacingWorks() {
        float spacing = 5f, cell = 20f;
        float gx = 100f, gy = 50f;
        String[][] rows = {{"Ser", "Mishen_cp"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = new LaserTrace(logic, grid, cell, spacing, gx, gy);
        Set<String> hits = new HashSet<>();
        float sx = gx + 1f;
        float sy = gy + cell/2;
        lt.trace(sx, sy, 0f, hits);
        assertTrue(hits.contains("1_0_mishen_cp"));
    }

    @Test
    @DisplayName("trace не падает на больших сетках")
    void bigGridPerformance() {
        int N = 30;
        String[][] rows = new String[N][N];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) rows[i][j] = "Ser";
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        assertDoesNotThrow(() -> lt.trace(0f, SIZE/2, 0f, new HashSet<>()));
    }
    @Test
    @DisplayName("Нестандартные названия ячеек — трассировка не падает")
    void strangeCellNamesDontCrash() {
        String[][] rows = {{"Ser", "Laser_foo_bar", "XXX", "Mishen_cc"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        List<Segment> segs = lt.trace(0f, SIZE / 2f, 0f, hits);
        assertTrue(hits.contains("3_0_mishen_cc"));
    }
    @Test
    @DisplayName("Laser_foo_bar: некорректный формат лазера не мешает трассировке")
    void laserWithInvalidFormat() {
        String[][] rows = {{"Laser_foo_bar", "Mishen_cc"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        assertDoesNotThrow(() -> lt.trace(0f, SIZE/2, 0f, hits));
        assertTrue(hits.contains("1_0_mishen_cc") || hits.isEmpty());
    }

    @Test
    @DisplayName("Mishen_foo: попадание по мишени с несуществующим постфиксом не ломает трассировку")
    void mishenWithInvalidPostfix() {
        String[][] rows = {{"Ser", "Mishen_foo"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        Set<String> hits = new HashSet<>();
        assertDoesNotThrow(() -> lt.trace(0f, SIZE/2, 0f, hits));
    }

    @Test
    @DisplayName("trace корректно работает с grid из одной пустой строки")
    void emptyRowGrid() {
        String[][] grid = {{}};
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        assertDoesNotThrow(() -> lt.trace(0f, 0f, 0f, new HashSet<>()));
    }
    @Test
    @DisplayName("trace корректно работает при попытке пройти вне сетки — ничего не ломается")
    void traceWithStartOutsideGrid() {
        String[][] rows = {{"Ser", "Ser"}};
        String[][] grid = toColMajor(rows);
        GameLogic logic = new GameLogic(grid);
        LaserTrace lt = mk(logic, grid);
        assertDoesNotThrow(() -> lt.trace(-100f, -100f, 0f, new HashSet<>()));
    }

}
