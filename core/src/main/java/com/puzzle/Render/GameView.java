package com.puzzle.Render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.puzzle.MainGame;
import com.puzzle.logic.GameLogic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameView {

    private final MainGame game;
    private final Stage stage;
    private final GameLogic logic;
    private final float cellSize = 85f;
    private final float cellSpacing = 15f;
    private final float gridStartX, gridStartY;
    private final Texture serTex   = new Texture(Gdx.files.internal("Ser.png"));
    private final Texture blockTex = new Texture(Gdx.files.internal("Block.png"));
    private final Texture mishenTex= new Texture(Gdx.files.internal("Mishen.png"));
    private final Texture mishenHit  = new Texture(Gdx.files.internal("Mishen.popal.png"));
    private final Map<String, Actor> actors = new HashMap<>();
    private Image grayImage;
    private final LaserView   laserView;

    public GameView(GameLogic logic, Stage stage, MainGame game) {
        this.logic = logic;
        this.stage = stage;
        this.game  = game;
        gridStartX = (Gdx.graphics.getWidth() - (logic.getGrid().length * cellSize) - ((logic.getGrid().length - 1) * cellSpacing)) / 2f;
        gridStartY = (Gdx.graphics.getHeight() - (logic.getGrid()[0].length * cellSize) - ((logic.getGrid()[0].length - 1) * cellSpacing)) / 2f;
        laserView = new LaserView(logic, stage,
            cellSize, cellSpacing,
            gridStartX, gridStartY);
        drawGrid();
    }



    void drawGrid() {
        String[][] grid = logic.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                float x = gridStartX + i*(cellSize+cellSpacing);
                float y = gridStartY + j*(cellSize+cellSpacing);
                String key = i + "_" + j;
                switch (grid[i][j]) {
                    case "Ser":
                        Image s = new Image(serTex);
                        s.setSize(cellSize, cellSize);
                        s.setPosition(x, y);
                        actors.put(key, s);
                        stage.addActor(s);
                        break;
                    case "Block":
                        Image b = new Image(blockTex);
                        b.setSize(cellSize, cellSize);
                        b.setPosition(x, y);
                        makeDraggable(b, i, j);
                        actors.put(key, b);
                        stage.addActor(b);
                        break;

                    case "pustoi":
                        break;
                    default:
                        if (grid[i][j].startsWith("Mishen")) {
                            String[] parts = grid[i][j].split("_");
                            String pos = parts[1];
                            float offX = 0, offY = 0;
                            switch (pos) {
                                case "nl": offX = -cellSize/3f; offY = -cellSize/3f; break;
                                case "nn": offY = -cellSize/3f; break;
                                case "np": offX =  cellSize/3f; offY = -cellSize/3f; break;
                                case "cl": offX = -cellSize/3f; break;
                                case "cc": break;
                                case "cp": offX =  cellSize/3f; break;
                                case "tl": offX = -cellSize/3f; offY =  cellSize/3f; break;
                                case "tn": offY =  cellSize/3f; break;
                                case "tp": offX =  cellSize/3f; offY =  cellSize/3f; break;
                            }
                            Image mish = new Image(mishenTex);
                            float size = cellSize / 3f;
                            mish.setSize(size, size);
                            mish.setPosition(x + cellSize / 2 - mishenTex.getWidth() / 2 + offX,
                                y + cellSize / 2 - mishenTex.getHeight() / 2 + offY);
                            actors.put(i + "_" + j + "_mishen_" + pos, mish);
                            stage.addActor(mish);
                        }
                        break;
                }
            }
        }
    }
    public void refreshGrid() {
        for (Iterator<Map.Entry<String, Actor>> it = actors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Actor> e = it.next();
            String key = e.getKey();
            if (!key.contains("_mishen_")) {
                e.getValue().remove();
                it.remove();
            }
        }
        drawGrid();
    }
    private void makeDraggable(final Image img, final int i0, final int j0) {
        img.addListener(new DragListener() {
            float startX, startY;
            int i = i0, j = j0;
            @Override
            public void dragStart(InputEvent e, float x, float y, int p) {
                if (logic.isWin()) return;
                startX = img.getX(); startY = img.getY();
                grayImage = new Image(serTex);
                grayImage.setSize(cellSize, cellSize);
                grayImage.setPosition(startX, startY);
                stage.addActor(grayImage);
                img.toFront();
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void drag(InputEvent e, float x, float y, int p) {
                if (logic.isWin()) return;
                img.moveBy(x - img.getWidth()/2, y - img.getHeight()/2);
            }
            @Override
            public void dragStop(InputEvent e, float x, float y, int p) {
                if (logic.isWin()) return;
                float endX = img.getX() + img.getWidth()/2;
                float endY = img.getY() + img.getHeight()/2;
                int ni = -1, nj = -1;
                String[][] grid = logic.getGrid();
                for (int k = 0; k < grid.length; k++)
                    for (int l = 0; l < grid[k].length; l++)
                        if ("Ser".equals(grid[k][l])) {
                            float csX = gridStartX + k*(cellSize+cellSpacing);
                            float csY = gridStartY + l*(cellSize+cellSpacing);
                            if (endX >= csX && endX < csX+cellSize &&
                                endY >= csY && endY < csY+cellSize) { ni=k; nj=l; }
                        }
                if (ni != -1) {
                    String[][] g = logic.getGrid();
                    String tmp = g[ni][nj]; g[ni][nj] = g[i][j]; g[i][j] = tmp;
                    String oldK = i+"_"+j, newK = ni+"_"+nj;
                    Actor oldA = actors.get(oldK), newA = actors.get(newK);
                    if (oldA!=null && newA!=null) {
                        oldA.setPosition(gridStartX+ni*(cellSize+cellSpacing),
                            gridStartY+nj*(cellSize+cellSpacing));
                        newA.setPosition(gridStartX+i*(cellSize+cellSpacing),
                            gridStartY+j*(cellSize+cellSpacing));
                        actors.put(oldK,newA); actors.put(newK,oldA);
                    }
                    i=ni; j=nj;
                } else img.setPosition(startX,startY);
                if (grayImage!=null) { grayImage.remove(); grayImage=null; }
                Gdx.graphics.setCursor(game.getCustomCursor());

                redrawLasers();
            }
        });
    }
    public void redrawLasers() {
        logic.getHitTargets().clear();
        for (Map.Entry<String,Actor> e : actors.entrySet())
            if (e.getKey().contains("_mishen_"))
                ((Image)e.getValue()).setDrawable(new TextureRegionDrawable(mishenTex));
        laserView.draw();
    }
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        laserView.draw();
        for (String k : logic.getHitTargets()) {
                   Actor a = actors.get(k);
                   if (a instanceof Image image &&
                           image.getDrawable() != null &&
                           image.getDrawable() != new TextureRegionDrawable(mishenHit)) {
                           image.setDrawable(new TextureRegionDrawable(mishenHit));
                       }
               }
    }
    public void dispose() {
        serTex.dispose(); blockTex.dispose(); mishenTex.dispose();mishenHit.dispose();
        laserView.dispose();
    }
}
