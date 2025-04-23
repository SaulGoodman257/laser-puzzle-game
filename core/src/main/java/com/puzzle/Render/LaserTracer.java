package com.puzzle.Render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.puzzle.logic.GameLogic;
import java.util.Map;
import java.util.Set;

public class LaserTracer {
    private final GameLogic logic;
    private final Stage stage;
    private final Map<String, Actor> actors;
    private final float cellSize, cellSpacing, gridStartX, gridStartY;
    private final float laserWidth = 5f;
    private final ShapeRenderer shape = new ShapeRenderer();
    private final Texture mishenTex  = new Texture(Gdx.files.internal("Mishen.png"));
    private final Texture mishenHit  = new Texture(Gdx.files.internal("Mishen.popal.png"));

    public LaserTracer(GameLogic logic, Stage stage, Map<String, Actor> actors, float cellSize, float cellSpacing, float gridStartX, float gridStartY) {
        this.logic = logic;
        this.stage = stage;
        this.actors = actors;
        this.cellSize= cellSize;
        this.cellSpacing = cellSpacing;
        this.gridStartX = gridStartX;
        this.gridStartY = gridStartY;
    }

    public void draw() { drawLaserLines(); }
    private void drawLaserLines() {
        String[][] grid = logic.getGrid();
        Set<String> hitTargets = logic.getHitTargets();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].startsWith("Laser")) {
                    float x = gridStartX + i * (cellSize + cellSpacing);
                    float y = gridStartY + j * (cellSize + cellSpacing);
                    String[] parts = grid[i][j].split("_");
                    String pos  = parts[1];
                    float  ang  = Float.parseFloat(parts[2]);
                    float offX = 0, offY = 0;
                    switch (pos) {
                        case "nl": offX = -cellSize/3; offY = -cellSize/3; break;
                        case "nn": offX = 0;            offY = -cellSize/3; break;
                        case "np": offX =  cellSize/3;  offY = -cellSize/3; break;
                        case "cl": offX = -cellSize/3;  offY = 0;           break;
                        case "cc": break;
                        case "cp": offX =  cellSize/3;  break;
                        case "tl": offX = -cellSize/3;  offY =  cellSize/3; break;
                        case "tn": offX = 0;            offY =  cellSize/3; break;
                        case "tp": offX =  cellSize/3;  offY =  cellSize/3; break;
                    }
                    drawLaserLine(x + offX, y + offY, ang);
                }
            }
        }
        logic.setBotButtonEnabled(!logic.isWin());
    }
    private void drawLaserLine(float x, float y, float angle) {
        shape.setProjectionMatrix(stage.getCamera().combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(1, 0, 0, 1);
        float cx = x + cellSize/2, cy = y + cellSize/2, r = 13;
        shape.circle(cx, cy, r);
        drawReflectedLaser(cx, cy, angle);
        shape.end();
    }

    private void drawReflectedLaser(float sx, float sy, float angle) {
        float dirX = (float) Math.cos(Math.toRadians(angle));
        float dirY = (float) Math.sin(Math.toRadians(angle));
        float ex = sx, ey = sy;
        String[][] grid = logic.getGrid();
        while (true) {
            float nx = ex + dirX, ny = ey + dirY;
            if (nx < gridStartX || nx > gridStartX + grid.length * (cellSize + cellSpacing) ||
                ny < gridStartY || ny > gridStartY + grid[0].length * (cellSize + cellSpacing)) {
                shape.rectLine(sx, sy, ex, ey, laserWidth);
                break;
            }
            int ci = (int) ((nx - gridStartX) / (cellSize + cellSpacing));
            int cj = (int) ((ny - gridStartY) / (cellSize + cellSpacing));
            if ((nx - gridStartX) % (cellSize + cellSpacing) > cellSize) ci = -1;
            if ((ny - gridStartY) % (cellSize + cellSpacing) > cellSize) cj = -1;
            if (ci >= 0 && ci < grid.length && cj >= 0 && cj < grid[0].length) {
                String cell = grid[ci][cj];
                if ("Block".equals(cell)) {
                    float cellSX = gridStartX + ci*(cellSize+cellSpacing);
                    float cellSY = gridStartY + cj*(cellSize+cellSpacing);
                    float cellEX = cellSX + cellSize;
                    float cellEY = cellSY + cellSize;
                    if (nx >= cellSX && nx <= cellEX && (ey < cellSY || ey > cellEY)) dirY = -dirY;
                    else if (ny >= cellSY && ny <= cellEY && (ex < cellSX || ex > cellEX)) dirX = -dirX;
                    shape.circle(ex, ey, 10);
                    shape.rectLine(sx, sy, ex, ey, laserWidth);
                    sx = ex; sy = ey;
                }
                else if (cell.startsWith("Mishen")) {
                    String pos = cell.split("_")[1];
                    float ccx = gridStartX + ci*(cellSize+cellSpacing) + cellSize/2;
                    float ccy = gridStartY + cj*(cellSize+cellSpacing) + cellSize/2;
                    float offX = 0, offY = 0;
                    switch (pos) {
                        case "nl": offX = -cellSize/3; offY = -cellSize/3; break;
                        case "nn": offY = -cellSize/3; break;
                        case "np": offX =  cellSize/3; offY = -cellSize/3; break;
                        case "cl": offX = -cellSize/3; break;
                        case "cp": offX =  cellSize/3; break;
                        case "tl": offX = -cellSize/3; offY =  cellSize/3; break;
                        case "tn": offY =  cellSize/3; break;
                        case "tp": offX =  cellSize/3; offY =  cellSize/3; break;
                    }
                    float tX = ccx + offX, tY = ccy + offY, tol = 7;
                    if (Math.abs(ex - tX) < tol && Math.abs(ey - tY) < tol) {
                        String key = ci + "_" + cj + "_mishen_" + pos;
                        if (logic.getHitTargets().add(key)) {
                            Actor a = actors.get(key);
                            if (a instanceof Image)
                                ((Image)a).setDrawable(new TextureRegionDrawable(mishenHit));
                        }
                        shape.rectLine(sx, sy, ex, ey, laserWidth);
                        logic.addHitTarget(ci + "_" + cj + "_mishen_" + pos);
                    }
                }
            }
            ex += dirX;
            ey += dirY;
        }
    }
    public void dispose() {
        shape.dispose();
        mishenTex.dispose();
        mishenHit.dispose();
    }
}
