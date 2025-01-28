package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.Map;

public class Game {
    private final MainGame game;
    private String[][] grid;
    private Stage stage;
    private Texture serTexture;
    private Texture blockTexture;
    private Texture mishenTexture;
    private Texture mishenPopalTexture;
    private boolean isWin = false;
    private Image grayImage;
    private ShapeRenderer shapeRenderer;
    private float cellSize = 100;
    private float cellSpacing = 20;
    private float gridStartX;
    private float gridStartY;
    private Map<String, Actor> actorsMap;
    private float laserWidth = 5;

    public Game(String[][] grid, Stage stage, MainGame game) {
        this.grid = grid;
        this.stage = stage;
        this.game = game;
        serTexture = new Texture(Gdx.files.internal("Ser.png"));
        blockTexture = new Texture(Gdx.files.internal("Block.png"));
        mishenTexture = new Texture(Gdx.files.internal("Mishen.png"));
        mishenPopalTexture = new Texture(Gdx.files.internal("Mishen.popal.png"));
        shapeRenderer = new ShapeRenderer();
        gridStartX = (Gdx.graphics.getWidth() - (grid.length * cellSize) - ((grid.length - 1) * cellSpacing)) / 2;
        gridStartY = (Gdx.graphics.getHeight() - (grid[0].length * cellSize) - ((grid[0].length - 1) * cellSpacing)) / 2;
        actorsMap = new HashMap<>();
        drawGrid();
    }

    public void drawGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                float x = gridStartX + i * (cellSize + cellSpacing);
                float y = gridStartY + j * (cellSize + cellSpacing);
                float cellX = gridStartX + i * (cellSize + cellSpacing);
                float cellY = gridStartY + j * (cellSize + cellSpacing);
                Image image = null;
                String key = i + "_" + j;
                switch (grid[i][j]) {
                    case "Ser":
                        image = new Image(serTexture);
                        image.setSize(cellSize, cellSize);
                        image.setPosition(x, y);
                        break;
                    case "Block":
                        image = new Image(blockTexture);
                        image.setSize(cellSize, cellSize);
                        image.setPosition(x, y);
                        makeDraggable(image, i, j);
                        break;
                    case "Mishen":
                        image = new Image(mishenTexture);
                        image.setPosition(cellX + (cellSize - mishenTexture.getWidth()) / 2, cellY + (cellSize - mishenTexture.getHeight()) / 2);
                        break;
                    case "Laser":
                        break;
                    default:
                        break;
                }
                if (image != null) {
                    actorsMap.put(key, image);
                    stage.addActor(image);
                }
            }
        }
    }
    private void drawLaserLine(float x, float y, float angle) {
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1);
        float centerX = x + cellSize / 2;
        float centerY = y + cellSize / 2;
        float radius = 13;
        shapeRenderer.circle(centerX, centerY, radius);
        drawReflectedLaser(centerX, centerY, angle);
        shapeRenderer.end();
    }

    private void drawReflectedLaser(float startX, float startY, float angle) {
        shapeRenderer.setColor(1, 0, 0, 1);
        float radians = (float) Math.toRadians(angle);
        float endX = startX;
        float endY = startY;
        float laserDistance = cellSize * Math.max(grid.length, grid[0].length);
        float dirX = (float) Math.cos(radians);
        float dirY = (float) Math.sin(radians);
        while (true) {
            float nextX = endX + dirX;
            float nextY = endY + dirY;
            if (nextX < gridStartX || nextX > gridStartX + grid.length * (cellSize + cellSpacing) ||
                nextY < gridStartY || nextY > gridStartY + grid[0].length * (cellSize + cellSpacing)) {

                shapeRenderer.rectLine(startX, startY, endX, endY, laserWidth);
                break;
            }
            int cellI = (int) ((nextX - gridStartX) / (cellSize + cellSpacing));
            int cellJ = (int) ((nextY - gridStartY) / (cellSize + cellSpacing));
            if ((nextX - gridStartX) % (cellSize + cellSpacing) > cellSize) {
                cellI = -1;
            }
            if ((nextY - gridStartY) % (cellSize + cellSpacing) > cellSize) {
                cellJ = -1;
            }
            if (cellI >= 0 && cellI < grid.length && cellJ >= 0 && cellJ < grid[0].length) {
                String cellType = grid[cellI][cellJ];

                if (cellType.equals("Block")) {
                    float cellStartX = gridStartX + cellI * (cellSize + cellSpacing);
                    float cellStartY = gridStartY + cellJ * (cellSize + cellSpacing);
                    float cellEndX = cellStartX + cellSize;
                    float cellEndY = cellStartY + cellSize;
                    if (nextX >= cellStartX && nextX <= cellEndX && (endY < cellStartY || endY > cellEndY)) {
                        dirY = -dirY;
                    } else if (nextY >= cellStartY && nextY <= cellEndY && (endX < cellStartX || endX > cellEndX)) {
                        dirX = -dirX;
                    }
                    if (Math.abs(nextX - cellStartX) < 1) {
                        dirX = -Math.abs(dirX);
                    } else if (Math.abs(nextX - cellEndX) < 1) {
                        dirX = Math.abs(dirX);
                    }

                    if (Math.abs(nextY - cellStartY) < 1) {
                        dirY = -Math.abs(dirY);
                    } else if (Math.abs(nextY - cellEndY) < 1) {
                        dirY = Math.abs(dirY);
                    }
                    shapeRenderer.circle(endX, endY, 10);
                    shapeRenderer.rectLine(startX, startY, endX, endY, laserWidth);
                    startX = endX;
                    startY = endY;
                } else if (cellType.equals("Mishen")) {
                    if (!isWin) {
                        isWin = true;
                        String key = cellI + "_" + cellJ;
                        Actor actor = actorsMap.get(key);
                        if (actor instanceof Image) {
                            ((Image) actor).setDrawable(new TextureRegionDrawable(mishenPopalTexture));
                        }
                    }
                }
            }
            endX += dirX;
            endY += dirY;
        }
    }

    public void drawLaserLines() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].startsWith("Laser")) {
                    float x = gridStartX + i * (cellSize + cellSpacing);
                    float y = gridStartY + j * (cellSize + cellSpacing);
                    String[] parts = grid[i][j].split("_");
                    float angle = Float.parseFloat(parts[1]);
                    drawLaserLine(x, y, angle);
                }
            }
        }
    }

    private void makeDraggable(final Image image, final int initialI, final int initialJ) {
        image.addListener(new DragListener() {
            float startX, startY;
            int i = initialI;
            int j = initialJ;
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (isWin) return;
                startX = image.getX();
                startY = image.getY();
                grayImage = new Image(serTexture);
                grayImage.setSize(cellSize, cellSize);
                grayImage.setPosition(startX, startY);
                stage.addActor(grayImage);
                image.toFront();
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (isWin) return;
                image.moveBy(x - image.getWidth() / 2, y - image.getHeight() / 2);
            }
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (isWin) return;
                float endX = image.getX() + image.getWidth() / 2;
                float endY = image.getY() + image.getHeight() / 2;
                int newI = -1;
                int newJ = -1;
                for (int k = 0; k < grid.length; k++) {
                    for (int l = 0; l < grid[k].length; l++) {
                        if (grid[k][l].equals("Ser")) {
                            float cellStartX = gridStartX + k * (cellSize + cellSpacing);
                            float cellStartY = gridStartY + l * (cellSize + cellSpacing);
                            if (endX >= cellStartX && endX < cellStartX + cellSize &&
                                endY >= cellStartY && endY < cellStartY + cellSize) {
                                newI = k;
                                newJ = l;
                                break;
                            }
                        }
                    }
                }
                if (newI != -1 && newJ != -1) {
                    String temp = grid[newI][newJ];
                    grid[newI][newJ] = grid[i][j];
                    grid[i][j] = temp;

                    String oldKey = i + "_" + j;
                    String newKey = newI + "_" + newJ;
                    Actor oldActor = actorsMap.get(oldKey);
                    Actor newActor = actorsMap.get(newKey);

                    if (oldActor != null && newActor != null) {
                        oldActor.setPosition(gridStartX + newI * (cellSize + cellSpacing), gridStartY + newJ * (cellSize + cellSpacing));
                        newActor.setPosition(gridStartX + i * (cellSize + cellSpacing), gridStartY + j * (cellSize + cellSpacing));

                        actorsMap.put(oldKey, newActor);
                        actorsMap.put(newKey, oldActor);
                    }

                    i = newI;
                    j = newJ;
                } else {
                    image.setPosition(startX, startY);
                }
                if (grayImage != null) {
                    grayImage.remove();
                    grayImage = null;
                }
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
    }
    public void dispose() {
        serTexture.dispose();
        blockTexture.dispose();
        mishenTexture.dispose();
        mishenPopalTexture.dispose();
        shapeRenderer.dispose();
    }
}
