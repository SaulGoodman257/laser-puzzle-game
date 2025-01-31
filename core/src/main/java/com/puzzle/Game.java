package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private Set<String> hitTargets;
    private int totalTargets;
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
        hitTargets = new HashSet<>();
        totalTargets = 0;
        drawGrid();
        checkWinCondition();
    }

    public void drawGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                float x = gridStartX + i * (cellSize + cellSpacing);
                float y = gridStartY + j * (cellSize + cellSpacing);
                String key = i + "_" + j;
                switch (grid[i][j]) {
                    case "Ser":
                        Image serImage = new Image(serTexture);
                        serImage.setSize(cellSize, cellSize);
                        serImage.setPosition(x, y);
                        actorsMap.put(key, serImage);
                        stage.addActor(serImage);
                        break;
                    case "pustoi":
                        break;
                    case "Block":
                        Image blockImage = new Image(blockTexture);
                        blockImage.setSize(cellSize, cellSize);
                        blockImage.setPosition(x, y);
                        makeDraggable(blockImage, i, j);
                        actorsMap.put(key, blockImage);
                        stage.addActor(blockImage);
                        break;
                    default:
                        if (grid[i][j].startsWith("Laser")) {
                            String[] parts = grid[i][j].split("_");
                            String position = parts[1];
                            float angle = Float.parseFloat(parts[2]);
                            float offsetX = 0;
                            float offsetY = 0;
                            switch (position) {
                                case "nl":
                                    offsetX = -cellSize / 3;
                                    offsetY = -cellSize / 3;
                                    break;
                                case "nn":
                                    offsetX = 0;
                                    offsetY = -cellSize / 3;
                                    break;
                                case "np":
                                    offsetX = cellSize / 3;
                                    offsetY = -cellSize / 3;
                                    break;
                                case "cl":
                                    offsetX = -cellSize / 3;
                                    offsetY = 0;
                                    break;
                                case "cc":
                                    offsetX = 0;
                                    offsetY = 0;
                                    break;
                                case "cp":
                                    offsetX = cellSize / 3;
                                    offsetY = 0;
                                    break;
                                case "tl":
                                    offsetX = -cellSize / 3;
                                    offsetY = cellSize / 3;
                                    break;
                                case "tn":
                                    offsetX = 0;
                                    offsetY = cellSize / 3;
                                    break;
                                case "tp":
                                    offsetX = cellSize / 3;
                                    offsetY = cellSize / 3;
                                    break;
                            }
                            drawLaserLine(x + offsetX, y + offsetY, angle);
                        } else if (grid[i][j].startsWith("Mishen")) {
                            totalTargets++;
                            String[] parts = grid[i][j].split("_");
                            String position = parts[1];
                            float offsetX = 0;
                            float offsetY = 0;
                            switch (position) {
                                case "nl":
                                    offsetX = -cellSize / 3;
                                    offsetY = -cellSize / 3;
                                    break;
                                case "nn":
                                    offsetX = 0;
                                    offsetY = -cellSize / 3;
                                    break;
                                case "np":
                                    offsetX = cellSize / 3;
                                    offsetY = -cellSize / 3;
                                    break;
                                case "cl":
                                    offsetX = -cellSize / 3;
                                    offsetY = 0;
                                    break;
                                case "cc":
                                    offsetX = 0;
                                    offsetY = 0;
                                    break;
                                case "cp":
                                    offsetX = cellSize / 3;
                                    offsetY = 0;
                                    break;
                                case "tl":
                                    offsetX = -cellSize / 3;
                                    offsetY = cellSize / 3;
                                    break;
                                case "tn":
                                    offsetX = 0;
                                    offsetY = cellSize / 3;
                                    break;
                                case "tp":
                                    offsetX = cellSize / 3;
                                    offsetY = cellSize / 3;
                                    break;
                            }
                            Image mishenImage = new Image(mishenTexture);
                            mishenImage.setPosition(x + cellSize / 2 - mishenTexture.getWidth() / 2 + offsetX,
                                y + cellSize / 2 - mishenTexture.getHeight() / 2 + offsetY);
                            actorsMap.put(key + "_mishen_" + position, mishenImage);
                            stage.addActor(mishenImage);
                        }
                        break;
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
                } else if (cellType.startsWith("Mishen")) {
                    String[] parts = cellType.split("_");
                    String position = parts[1];
                    float cellCenterX = gridStartX + cellI * (cellSize + cellSpacing) + cellSize / 2;
                    float cellCenterY = gridStartY + cellJ * (cellSize + cellSpacing) + cellSize / 2;
                    float offsetX = 0;
                    float offsetY = 0;
                    switch (position) {
                        case "nl":
                            offsetX = -cellSize / 3;
                            offsetY = -cellSize / 3;
                            break;
                        case "nn":
                            offsetX = 0;
                            offsetY = -cellSize / 3;
                            break;
                        case "np":
                            offsetX = cellSize / 3;
                            offsetY = -cellSize / 3;
                            break;
                        case "cl":
                            offsetX = -cellSize / 3;
                            offsetY = 0;
                            break;
                        case "cc":
                            offsetX = 0;
                            offsetY = 0;
                            break;
                        case "cp":
                            offsetX = cellSize / 3;
                            offsetY = 0;
                            break;
                        case "tl":
                            offsetX = -cellSize / 3;
                            offsetY = cellSize / 3;
                            break;
                        case "tn":
                            offsetX = 0;
                            offsetY = cellSize / 3;
                            break;
                        case "tp":
                            offsetX = cellSize / 3;
                            offsetY = cellSize / 3;
                            break;
                    }
                    float targetCenterX = cellCenterX + offsetX;
                    float targetCenterY = cellCenterY + offsetY;
                    float tolerance = 5;
                    String targetKey = cellI + "_" + cellJ + "_mishen_" + position;
                    if (Math.abs(endX - targetCenterX) < tolerance && Math.abs(endY - targetCenterY) < tolerance) {
                        if (!hitTargets.contains(targetKey)) {
                            hitTargets.add(targetKey);
                            Actor actor = actorsMap.get(targetKey);
                            if (actor instanceof Image) {
                                ((Image) actor).setDrawable(new TextureRegionDrawable(mishenPopalTexture));
                            }
                        }

                        shapeRenderer.rectLine(startX, startY, endX, endY, laserWidth);
                        checkWinCondition();
                    }
                }
            }
            endX += dirX;
            endY += dirY;
        }
    }
    private void checkWinCondition() {
        if (hitTargets.size() == totalTargets) {
            isWin = true;
        } else {
            isWin = false;
        }
    }

    public void drawLaserLines() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].startsWith("Laser")) {
                    float x = gridStartX + i * (cellSize + cellSpacing);
                    float y = gridStartY + j * (cellSize + cellSpacing);
                    String[] parts = grid[i][j].split("_");
                    String position = parts[1];
                    float angle = Float.parseFloat(parts[2]);
                    float offsetX = 0;
                    float offsetY = 0;
                    switch (position) {
                        case "nl":
                            offsetX = -cellSize / 3;
                            offsetY = -cellSize / 3;
                            break;
                        case "nn":
                            offsetX = 0;
                            offsetY = -cellSize / 3;
                            break;
                        case "np":
                            offsetX = cellSize / 3;
                            offsetY = -cellSize / 3;
                            break;
                        case "cl":
                            offsetX = -cellSize / 3;
                            offsetY = 0;
                            break;
                        case "cc":
                            offsetX = 0;
                            offsetY = 0;
                            break;
                        case "cp":
                            offsetX = cellSize / 3;
                            offsetY = 0;
                            break;
                        case "tl":
                            offsetX = -cellSize / 3;
                            offsetY = cellSize / 3;
                            break;
                        case "tn":
                            offsetX = 0;
                            offsetY = cellSize / 3;
                            break;
                        case "tp":
                            offsetX = cellSize / 3;
                            offsetY = cellSize / 3;
                            break;
                    }
                    drawLaserLine(x + offsetX, y + offsetY, angle);
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
                redrawLasers();
            }
        });
    }
    public boolean isWin() {
        return isWin;
    }
    private void redrawLasers() {
        hitTargets.clear();
        for (Map.Entry<String, Actor> entry : actorsMap.entrySet()) {
            if (entry.getKey().contains("_mishen_")) {
                ((Image) entry.getValue()).setDrawable(new TextureRegionDrawable(mishenTexture));
            }
        }

        checkWinCondition();
    }

    public void dispose() {
        serTexture.dispose();
        blockTexture.dispose();
        mishenTexture.dispose();
        mishenPopalTexture.dispose();
        shapeRenderer.dispose();
    }
}
