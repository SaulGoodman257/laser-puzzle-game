package com.puzzle.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LaserTrace {

    private String[][] grid;
    private final float cellSize, cellSpacing;
    private final float gridStartX, gridStartY;
    private final GameLogic logic;

    public LaserTrace(GameLogic logic, String[][] grid, float cellSize, float cellSpacing, float gridStartX, float gridStartY) {
        this.logic       = logic;
        this.grid        = grid;
        this.cellSize    = cellSize;
        this.cellSpacing = cellSpacing;
        this.gridStartX  = gridStartX;
        this.gridStartY  = gridStartY;
    }
    public List<Segment> trace(float sx, float sy, float angle, Set<String> hitTargets) {
        this.grid = logic.getGrid();
        List<Segment> segs = new ArrayList<>();
        float dirX = (float) Math.cos(Math.toRadians(angle));
        float dirY = (float) Math.sin(Math.toRadians(angle));
        float ex = sx, ey = sy;
        while (true) {
            float nx = ex + dirX;
            float ny = ey + dirY;
            if (nx < gridStartX || nx > gridStartX + grid.length * (cellSize + cellSpacing) ||
                ny < gridStartY || ny > gridStartY + grid[0].length * (cellSize + cellSpacing)) {
                segs.add(new Segment(sx, sy, ex, ey));
                break;
            }
            int ci = (int) ((nx - gridStartX) / (cellSize + cellSpacing));
            int cj = (int) ((ny - gridStartY) / (cellSize + cellSpacing));
            if ((nx - gridStartX) % (cellSize + cellSpacing) > cellSize) ci = -1;
            if ((ny - gridStartY) % (cellSize + cellSpacing) > cellSize) cj = -1;
            if (ci >= 0 && cj >= 0 && ci < grid.length && cj < grid[0].length) {
                String cell = grid[ci][cj];
                if ("Block".equals(cell)) {
                    segs.add(new Segment(sx, sy, ex, ey));
                    float cellSX = gridStartX + ci * (cellSize + cellSpacing);
                    float cellSY = gridStartY + cj * (cellSize + cellSpacing);
                    float cellEX = cellSX + cellSize;
                    float cellEY = cellSY + cellSize;
                    if (nx >= cellSX && nx <= cellEX && (ey < cellSY || ey > cellEY)) dirY = -dirY;
                    else if (ny >= cellSY && ny <= cellEY && (ex < cellSX || ex > cellEX)) dirX = -dirX;
                    sx = ex;
                    sy = ey;
                }
                else if (cell.startsWith("Mishen")) {
                    String pos = cell.split("_")[1];
                    float ccx = gridStartX + ci * (cellSize + cellSpacing) + cellSize / 2f;
                    float ccy = gridStartY + cj * (cellSize + cellSpacing) + cellSize / 2f;
                    float offX = 0, offY = 0;
                    switch (pos) {
                        case "nl": offX = -cellSize / 3f; offY = -cellSize / 3f; break;
                        case "nn": offY = -cellSize / 3f; break;
                        case "np": offX =  cellSize / 3f; offY = -cellSize / 3f; break;
                        case "cl": offX = -cellSize / 3f; break;
                        case "cp": offX =  cellSize / 3f; break;
                        case "tl": offX = -cellSize / 3f; offY =  cellSize / 3f; break;
                        case "tn": offY =  cellSize / 3f; break;
                        case "tp": offX =  cellSize / 3f; offY =  cellSize / 3f; break;
                    }
                    float tX = ccx + offX;
                    float tY = ccy + offY;
                    float tol = 7f;
                    if (Math.abs(ex - tX) < tol && Math.abs(ey - tY) < tol) {
                        String key = ci + "_" + cj + "_mishen_" + pos;
                        if (hitTargets.add(key)) {
                            logic.addHitTarget(key);
                        }
                    }
                }
            }
            ex += dirX;
            ey += dirY;
        }
        return segs;
    }
}
