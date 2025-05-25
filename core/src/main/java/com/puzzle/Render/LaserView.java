package com.puzzle.Render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.puzzle.logic.GameLogic;
import com.puzzle.logic.LaserTrace;
import com.puzzle.logic.Segment;
import java.util.List;

public class LaserView {

    private final GameLogic logic;
    private final LaserTrace tracer;
    private final Stage stage;
    private final float cellSize, cellSpacing, gridStartX, gridStartY;
    private final float laserWidth = 5f;
    private final float reflRadius = 10f;
    private final ShapeRenderer shape = new ShapeRenderer();

    public LaserView(GameLogic logic, Stage stage, float cellSize, float cellSpacing, float gridStartX, float gridStartY) {
        this.logic       = logic;
        this.stage       = stage;
        this.cellSize    = cellSize;
        this.cellSpacing = cellSpacing;
        this.gridStartX  = gridStartX;
        this.gridStartY  = gridStartY;
        this.tracer = new LaserTrace(
            logic,
            logic.getGrid(),
            cellSize,
            cellSpacing,
            gridStartX,
            gridStartY);
    }
    public void draw() {
        shape.setProjectionMatrix(stage.getCamera().combined);
        String[][] grid = logic.getGrid();
        var         hit = logic.getHitTargets();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].startsWith("Laser")) {
                    drawOneLaser(i, j, hit);
                }
            }
        }
    }
    private void drawOneLaser(int i, int j, java.util.Set<String> hit) {
        String[][] grid = logic.getGrid();
        String[] parts  = grid[i][j].split("_");
        String pos      = parts[1];
        float  angle    = Float.parseFloat(parts[2]);
        float x = gridStartX + i * (cellSize + cellSpacing);
        float y = gridStartY + j * (cellSize + cellSpacing);
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
        float sx = x + offX + cellSize / 2f;
        float sy = y + offY + cellSize / 2f;
        List<Segment> segs = tracer.trace(sx, sy, angle, hit);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(1, 0, 0, 1);
        shape.circle(sx, sy, 13f);
        for (Segment s : segs)
            shape.rectLine(s.sx(), s.sy(), s.ex(), s.ey(), laserWidth);
        for (int k = 0; k < segs.size() - 1; k++) {
            Segment s = segs.get(k);
            shape.circle(s.ex(), s.ey(), reflRadius);
        }
        shape.end();
    }
    public void dispose() {
        shape.dispose();
    }
}
