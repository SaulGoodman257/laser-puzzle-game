package com.puzzle;

import com.puzzle.logic.GameLogic;
import com.puzzle.logic.LaserTrace;
import com.puzzle.logic.Segment;
import java.util.*;

public class Bot {

    private final String[][] startGrid;
    private final int rows, cols;
    private final float CELL, GAP;
    private final float startX, startY;
    private final Set<String> visited = new HashSet<>();

    public Bot(String[][] grid) {
        this(grid, 85f, 15f, 0f, 0f);
    }
    public Bot(String[][] grid, float cellSize, float gap, float startX, float startY) {
        this.startGrid = copyGrid(grid);
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.CELL = cellSize;
        this.GAP = gap;
        this.startX = startX;
        this.startY = startY;
    }

    private static String[][] copyGrid(String[][] g) {
        String[][] c = new String[g.length][];
        for (int i = 0; i < g.length; i++) c[i] = g[i].clone();
        return c;
    }

    private static String gridKey(String[][] g) {
        return Arrays.deepToString(g);
    }
    public List<Move> solveWithPath() {
        record Node(String[][] grid, List<Move> path) {}
        Queue<Node> q = new ArrayDeque<>();
        q.add(new Node(startGrid, new ArrayList<>()));
        visited.clear();
        visited.add(gridKey(startGrid));

        while (!q.isEmpty()) {
            Node n = q.poll();
            if (isSolved(n.grid)) return n.path;
            Set<String> laserPathCells = getLaserPathCells(n.grid);
            for (Move mv : findMoves(n.grid, laserPathCells)) {
                String[][] ng = applyMove(n.grid, mv);
                String key = gridKey(ng);
                if (visited.add(key)) {
                    List<Move> np = new ArrayList<>(n.path);
                    np.add(mv);
                    q.add(new Node(ng, np));
                }
            }
        }
        return null;
    }

    public record Move(int blockR, int blockC, int serR, int serC) {}

    private List<Move> findMoves(String[][] g, Set<String> laserPathCells) {
        List<Move> list = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if ("Block".equals(g[r][c])) {
                    String blockKey = r + "_" + c;
                    for (int sr = 0; sr < rows; sr++) {
                        for (int sc = 0; sc < cols; sc++) {
                            if ("Ser".equals(g[sr][sc])) {
                                String serKey = sr + "_" + sc;
                                if (laserPathCells.contains(blockKey) || laserPathCells.contains(serKey)) {
                                    list.add(new Move(r, c, sr, sc));
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    private static String[][] applyMove(String[][] g, Move m) {
        String[][] n = copyGrid(g);
        String t = n[m.blockR][m.blockC];
        n[m.blockR][m.blockC] = n[m.serR][m.serC];
        n[m.serR][m.serC] = t;
        return n;
    }

    private boolean isSolved(String[][] g) {
        GameLogic logic = new GameLogic(g);
        LaserTrace tracer = new LaserTrace(logic, g, CELL, GAP, startX, startY);
        Set<String> hits = logic.getHitTargets();
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                if (g[i][j].startsWith("Laser")) {
                    String[] p = g[i][j].split("_");
                    String pos = p[1];
                    float angle = Float.parseFloat(p[2]);
                    float offX = 0, offY = 0;
                    switch (pos) {
                        case "nl" -> { offX = -CELL / 3; offY = -CELL / 3; }
                        case "nn" -> { offY = -CELL / 3; }
                        case "np" -> { offX = CELL / 3; offY = -CELL / 3; }
                        case "cl" -> { offX = -CELL / 3; }
                        case "cp" -> { offX = CELL / 3; }
                        case "tl" -> { offX = -CELL / 3; offY = CELL / 3; }
                        case "tn" -> { offY = CELL / 3; }
                        case "tp" -> { offX = CELL / 3; offY = CELL / 3; }
                    }
                    float sx = startX + i * (CELL + GAP) + offX + CELL / 2;
                    float sy = startY + j * (CELL + GAP) + offY + CELL / 2;
                    tracer.trace(sx, sy, angle, hits);
                }
            }
        }
        return logic.isWin();
    }

    private Set<String> getLaserPathCells(String[][] g) {
        Set<String> pathCells = new HashSet<>();
        GameLogic logic = new GameLogic(g);
        LaserTrace tracer = new LaserTrace(logic, g, CELL, GAP, startX, startY);

        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                if (g[i][j].startsWith("Laser")) {
                    String[] p = g[i][j].split("_");
                    String pos = p[1];
                    float angle = Float.parseFloat(p[2]);
                    float offX = 0, offY = 0;
                    switch (pos) {
                        case "nl" -> { offX = -CELL / 3; offY = -CELL / 3; }
                        case "nn" -> { offY = -CELL / 3; }
                        case "np" -> { offX = CELL / 3; offY = -CELL / 3; }
                        case "cl" -> { offX = -CELL / 3; }
                        case "cp" -> { offX = CELL / 3; }
                        case "tl" -> { offX = -CELL / 3; offY = CELL / 3; }
                        case "tn" -> { offY = CELL / 3; }
                        case "tp" -> { offX = CELL / 3; offY = CELL / 3; }
                    }
                    float sx = startX + i * (CELL + GAP) + offX + CELL / 2;
                    float sy = startY + j * (CELL + GAP) + offY + CELL / 2;
                    List<Segment> segments = tracer.trace(sx, sy, angle, new HashSet<>());
                    for (Segment seg : segments) {
                        int startRow = (int) ((seg.sy() - startY) / (CELL + GAP));
                        int startCol = (int) ((seg.sx() - startX) / (CELL + GAP));
                        int endRow = (int) ((seg.ey() - startY) / (CELL + GAP));
                        int endCol = (int) ((seg.ex() - startX) / (CELL + GAP));
                        int r1 = Math.min(startRow, endRow);
                        int r2 = Math.max(startRow, endRow);
                        int c1 = Math.min(startCol, endCol);
                        int c2 = Math.max(startCol, endCol);
                        for (int r = r1; r <= r2; r++) {
                            for (int c = c1; c <= c2; c++) {
                                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                                    pathCells.add(r + "_" + c);
                                }
                            }
                        }
                    }
                }
            }
        }
        return pathCells;
    }
}
