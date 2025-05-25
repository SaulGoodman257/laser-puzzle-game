package com.puzzle.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.puzzle.CreateLevelScreen;
import com.puzzle.MainGame;
import com.puzzle.logic.LaserTrace;
import com.puzzle.Render.LaserView;
import com.puzzle.logic.RedactorLogic;
import java.util.HashMap;
import java.util.Map;

public class RedactorScreen implements Screen {
    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture imageredactor;
    private Music playMusic;
    private Image backgroundImage;
    private Texture menu_redactorback;
    private Texture menu_redactorok;
    private Texture menu_redactorsave;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private TextField widthField;
    private TextField heightField;
    private TextField levelNumberField;
    private boolean gridCreated = false;
    private float cellSize = 85;
    private float cellSpacing = 15;
    private float mishenSize = 30;
    private float laserSize = 30;
    private float gridStartX;
    private float gridStartY;
    private Texture serTexture;
    private Texture blockTexture;
    private Texture pustoiTexture;
    private Image draggedImage;
    private String draggedImageType;
    private Image blockImage;
    private Image serImage;
    private Image pustoiImage;
    private Texture mishenTexture;
    private Image mishenImage;
    private Map<String, Image> cellImages = new HashMap<>();
    private boolean laserPlaced = false;
    private Image laserImage;
    private float laserRotation = 0;
    private String currentLaserPosition = "cc";
    private float laserX, laserY;
    private LaserTrace laserTrace;
    private LaserView laserView;
    private RedactorLogic logic = new RedactorLogic();

    public RedactorScreen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(camera), game.batch);
        Gdx.input.setInputProcessor(stage);
        imageredactor = new Texture(Gdx.files.internal("menu_redactor.png"));
        menu_redactorback = new Texture(Gdx.files.internal("menu_redactorback.png"));
        menu_redactorok = new Texture(Gdx.files.internal("menu_redactorok.png"));
        menu_redactorsave = new Texture(Gdx.files.internal("menu_redactorsave.png"));
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundImage = new Image(imageredactor);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("music_play.mp3"));
        playMusic.setLooping(true);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        widthField = new TextField("", skin);
        heightField = new TextField("", skin);
        levelNumberField = new TextField("", skin);
        widthField.setPosition(93, 550);
        heightField.setPosition(93, 340);
        levelNumberField.setPosition(93, 780);
        widthField.setSize(100, 40);
        heightField.setSize(100, 40);
        levelNumberField.setSize(100, 40);
        widthField.setMessageText("Width 1-9");
        heightField.setMessageText("Height 1-6");
        levelNumberField.setMessageText("Level 1-3");
        stage.addActor(widthField);
        stage.addActor(heightField);
        stage.addActor(levelNumberField);
        serTexture = new Texture(Gdx.files.internal("Ser.png"));
        blockTexture = new Texture(Gdx.files.internal("Block.png"));
        pustoiTexture = new Texture(Gdx.files.internal("pustoi.png"));
        mishenTexture = new Texture(Gdx.files.internal("Mishen.png"));
        createBlockMenu();
        createUI();
        laserImage = new Image(createLaserCircleTexture());
        laserImage.setSize(laserSize, laserSize);
        laserImage.setPosition(1749, 288);
        makeDraggable(laserImage, "Laser");
        stage.addActor(laserImage);
        laserImage.setTouchable(Touchable.disabled);
    }

    private void createUI() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();

        final TextButton backButton = new TextButton("", textButtonStyle);
        backButton.setBounds(783, 35, 350, 103);

        final TextButton okButton = new TextButton("", textButtonStyle);
        okButton.setBounds(50, 188, 192, 50);

        final TextButton saveButton = new TextButton("", textButtonStyle);
        saveButton.setBounds(861, 155, 192, 50);

        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_redactorback).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageredactor).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        okButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_redactorok).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageredactor).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        saveButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_redactorsave).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageredactor).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new CreateLevelScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                try {
                    int width = Integer.parseInt(widthField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    int levelNumber = Integer.parseInt(levelNumberField.getText());

                    if (levelNumber >= 1 && levelNumber <= 3 && height <= 6 && height >= 1 && width >= 1 && width <= 9) {
                        logic.createGrid(width, height);
                        gridCreated = true;
                        calculateGridStartPosition();
                        laserTrace = new LaserTrace(logic.getEditorLogic(), logic.getGrid(), cellSize, cellSpacing, gridStartX, gridStartY);
                        laserView = new LaserView(logic.getEditorLogic(), stage, cellSize, cellSpacing, gridStartX, gridStartY);
                        drawGrid();
                        addMouseWheelListenerToStage();
                        okButton.setTouchable(Touchable.disabled);
                        blockImage.setTouchable(Touchable.enabled);
                        serImage.setTouchable(Touchable.enabled);
                        pustoiImage.setTouchable(Touchable.enabled);
                        mishenImage.setTouchable(Touchable.enabled);
                        laserImage.setTouchable(Touchable.enabled);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter numbers only.");
                }
            }
        });
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                if (gridCreated) {
                    try {
                        int levelNumber = Integer.parseInt(levelNumberField.getText());
                        logic.saveLevel(levelNumber);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid level number format.");
                    }
                }
            }
        });
        stage.addActor(saveButton);
        stage.addActor(backButton);
        stage.addActor(okButton);
    }

    private Texture createLaserCircleTexture() {
        Pixmap pixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fillCircle(15, 15, 15);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void calculateGridStartPosition() {
        String[][] grid = logic.getGrid();
        if (grid != null) {
            gridStartX = (Gdx.graphics.getWidth() - (grid.length * cellSize) - ((grid.length - 1) * cellSpacing)) / 2;
            gridStartY = (Gdx.graphics.getHeight() - (grid[0].length * cellSize) - ((grid[0].length - 1) * cellSpacing)) / 2;
        }
    }

    private void createBlockMenu() {
        blockImage = new Image(blockTexture);
        blockImage.setPosition(1720, 810);
        blockImage.setSize(cellSize, cellSize);
        makeDraggable(blockImage, "Block");
        stage.addActor(blockImage);
        serImage = new Image(serTexture);
        serImage.setPosition(1720, 654);
        serImage.setSize(cellSize, cellSize);
        makeDraggable(serImage, "Ser");
        stage.addActor(serImage);
        pustoiImage = new Image(pustoiTexture);
        pustoiImage.setPosition(1720, 400);
        pustoiImage.setSize(cellSize, cellSize);
        makeDraggable(pustoiImage, "pustoi");
        stage.addActor(pustoiImage);
        blockImage.setTouchable(Touchable.disabled);
        serImage.setTouchable(Touchable.disabled);
        pustoiImage.setTouchable(Touchable.disabled);
        mishenImage = new Image(mishenTexture);
        mishenImage.setPosition(1749, 540);
        mishenImage.setSize(mishenSize, mishenSize);
        makeDraggable(mishenImage, "Mishen");
        stage.addActor(mishenImage);
        mishenImage.setTouchable(Touchable.disabled);
    }

    private void makeDraggable(final Image image, final String blockType) {
        image.addListener(new DragListener() {
            float startX, startY;
            String initialPosition;
            String initialCellKey;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (blockType.equals("Laser") && logic.countLasersOnGrid() >= 1) {
                    return;
                }
                startX = image.getX();
                startY = image.getY();
                initialCellKey = getCellKey(event.getStageX(), event.getStageY());
                if (blockType.equals("Mishen") || blockType.equals("Laser")) {
                    if (initialCellKey != null) {
                        initialPosition = blockType.equals("Mishen") ? logic.getMishenPositions().get(initialCellKey) : logic.getLaserPositions().get(initialCellKey);
                    }
                }
                draggedImage = new Image(image.getDrawable());
                if (blockType.equals("Mishen")) {
                    draggedImage.setSize(mishenSize, mishenSize);
                } else if (blockType.equals("Laser")) {
                    draggedImage.setSize(laserSize, laserSize);
                } else {
                    draggedImage.setSize(cellSize, cellSize);
                }
                draggedImageType = blockType;
                stage.addActor(draggedImage);
                draggedImage.setPosition(event.getStageX() - draggedImage.getWidth() / 2, event.getStageY() - draggedImage.getHeight() / 2);
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (draggedImage != null) {
                    draggedImage.setPosition(event.getStageX() - draggedImage.getWidth() / 2, event.getStageY() - draggedImage.getHeight() / 2);
                }
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (draggedImage == null) return;
                if (blockType.equals("Laser") && laserPlaced && !draggedImageType.equals("Laser")) {
                    draggedImage.remove();
                    draggedImage = null;
                    return;
                }
                float dropX = event.getStageX();
                float dropY = event.getStageY();
                int cellI = -1;
                int cellJ = -1;
                String[][] grid = logic.getGrid();
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        float cellStartX = gridStartX + i * (cellSize + cellSpacing);
                        float cellStartY = gridStartY + j * (cellSize + cellSpacing);
                        float cellEndX = cellStartX + cellSize;
                        float cellEndY = cellStartY + cellSize;
                        if (dropX >= cellStartX && dropX < cellEndX && dropY >= cellStartY && dropY < cellEndY) {
                            cellI = i;
                            cellJ = j;
                            break;
                        }
                    }
                }
                if (cellI != -1 && cellJ != -1) {
                    String targetCellKey = cellI + "_" + cellJ;
                    if (draggedImageType.equals("Mishen")) {
                        if (initialCellKey != null && !initialCellKey.equals(targetCellKey)) {
                            logic.setCell(Integer.parseInt(initialCellKey.split("_")[0]), Integer.parseInt(initialCellKey.split("_")[1]), "pustoi");
                            updateCellImage(Integer.parseInt(initialCellKey.split("_")[0]), Integer.parseInt(initialCellKey.split("_")[1]), "pustoi");
                        }
                        String currentPosition = logic.getMishenPositions().get(targetCellKey);
                        if (currentPosition == null) {
                            logic.setCell(cellI, cellJ, draggedImageType + "_cc");
                            updateCellImage(cellI, cellJ, draggedImageType + "_cc");
                        } else {
                            String newPosition = logic.getNewMishenPosition(currentPosition);
                            logic.setCell(cellI, cellJ, draggedImageType + "_" + newPosition);
                            updateCellImage(cellI, cellJ, draggedImageType + "_" + newPosition);
                        }
                    } else if (draggedImageType.equals("Laser")) {
                        if (initialCellKey != null && !initialCellKey.equals(targetCellKey)) {
                            logic.setCell(Integer.parseInt(initialCellKey.split("_")[0]), Integer.parseInt(initialCellKey.split("_")[1]), "pustoi");
                            updateCellImage(Integer.parseInt(initialCellKey.split("_")[0]), Integer.parseInt(initialCellKey.split("_")[1]), "pustoi");
                        }
                        String currentPosition = logic.getLaserPositions().get(targetCellKey);
                        if (currentPosition == null) {
                            logic.setCell(cellI, cellJ, draggedImageType + "_cc_0");
                            updateCellImage(cellI, cellJ, draggedImageType + "_cc_0");
                            currentLaserPosition = "cc";
                            float[] offset = calculateLaserOffset(currentLaserPosition);
                            laserX = gridStartX + cellI * (cellSize + cellSpacing) + offset[0];
                            laserY = gridStartY + cellJ * (cellSize + cellSpacing) + offset[1];
                        } else {
                            String[] parts = currentPosition.split("_");
                            String position = parts[0];
                            float rotation = parts.length > 1 ? Float.parseFloat(parts[1]) : 0;
                            String newPosition = logic.getNewLaserPosition(position);
                            logic.setCell(cellI, cellJ, draggedImageType + "_" + newPosition + "_" + rotation);
                            updateCellImage(cellI, cellJ, draggedImageType + "_" + newPosition + "_" + rotation);
                            currentLaserPosition = newPosition;
                            float[] offset = calculateLaserOffset(currentLaserPosition);
                            laserX = gridStartX + cellI * (cellSize + cellSpacing) + offset[0];
                            laserY = gridStartY + cellJ * (cellSize + cellSpacing) + offset[1];
                        }
                    } else {
                        logic.setCell(cellI, cellJ, draggedImageType);
                        updateCellImage(cellI, cellJ, draggedImageType);
                    }
                }
                if ((draggedImageType.equals("Mishen") || draggedImageType.equals("Laser")) && (cellI == -1 || cellJ == -1)) {
                    image.setPosition(startX, startY);
                }
                draggedImage.remove();
                draggedImage = null;
                draggedImageType = null;
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
    }

    private float[] calculateLaserOffset(String position) {
        float offsetX = 0;
        float offsetY = 0;
        switch (position) {
            case "nl":
                offsetX = 0;
                offsetY = 0;
                break;
            case "nn":
                offsetX = cellSize / 2 - laserSize / 2;
                offsetY = 0;
                break;
            case "np":
                offsetX = cellSize - laserSize;
                offsetY = 0;
                break;
            case "cl":
                offsetX = 0;
                offsetY = cellSize / 2 - laserSize / 2;
                break;
            case "cc":
                offsetX = cellSize / 2 - laserSize / 2;
                offsetY = cellSize / 2 - laserSize / 2;
                break;
            case "cp":
                offsetX = cellSize - laserSize;
                offsetY = cellSize / 2 - laserSize / 2;
                break;
            case "tl":
                offsetX = 0;
                offsetY = cellSize - laserSize;
                break;
            case "tn":
                offsetX = cellSize / 2 - laserSize / 2;
                offsetY = cellSize - laserSize;
                break;
            case "tp":
                offsetX = cellSize - laserSize;
                offsetY = cellSize - laserSize;
                break;
        }
        return new float[]{offsetX, offsetY};
    }

    private void addMouseWheelListenerToStage() {
        stage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                if (!gridCreated) return false;
                String[][] grid = logic.getGrid();
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        String cellKey = i + "_" + j;
                        float cellStartX = gridStartX + i * (cellSize + cellSpacing);
                        float cellStartY = gridStartY + j * (cellSize + cellSpacing);
                        float cellEndX = cellStartX + cellSize;
                        float cellEndY = cellStartY + cellSize;

                        if (x >= cellStartX && x < cellEndX && y >= cellStartY && y < cellEndY) {
                            if (grid[i][j].startsWith("Mishen")) {
                                String currentMishenPosition = grid[i][j].split("_")[1];
                                String newMishenPosition = logic.getNewMishenPositionByScroll(currentMishenPosition, amountY);
                                logic.setCell(i, j, "Mishen_" + newMishenPosition);
                                updateCellImage(i, j, "Mishen_" + newMishenPosition);
                                return true;
                            } else if (grid[i][j].startsWith("Laser")) {
                                String[] parts = grid[i][j].split("_");
                                String position = parts[1];
                                float rotation = parts.length > 2 ? Float.parseFloat(parts[2]) : 0;
                                float newRotation = (rotation + amountY * 2) % 360;
                                if (newRotation < 0) newRotation += 360;
                                logic.setCell(i, j, "Laser_" + position + "_" + newRotation);
                                updateCellImage(i, j, "Laser_" + position + "_" + newRotation);
                                float[] offset = calculateLaserOffset(position);
                                laserX = gridStartX + i * (cellSize + cellSpacing) + offset[0];
                                laserY = gridStartY + j * (cellSize + cellSpacing) + offset[1];
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    private String getCellKey(float x, float y) {
        String[][] grid = logic.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                float cellStartX = gridStartX + i * (cellSize + cellSpacing);
                float cellStartY = gridStartY + j * (cellSize + cellSpacing);
                if (x >= cellStartX && x < cellStartX + cellSize && y >= cellStartY && y < cellStartY + cellSize) {
                    return i + "_" + j;
                }
            }
        }
        return null;
    }

    private void updateCellImage(int i, int j, String blockType) {
        float x = gridStartX + i * (cellSize + cellSpacing);
        float y = gridStartY + j * (cellSize + cellSpacing);
        String cellKey = i + "_" + j;
        if (cellImages.containsKey(cellKey)) {
            cellImages.get(cellKey).remove();
            cellImages.remove(cellKey);
        }

        Texture texture = null;
        String[] parts = blockType.split("_");
        String type = parts[0];
        if (type.equals("Mishen") && parts.length > 1) {
            String position = parts[1];
            texture = mishenTexture;
            float offsetX = 0;
            float offsetY = 0;
            switch (position) {
                case "nl":
                    offsetX = -cellSize / 3f;
                    offsetY = -cellSize / 3f;
                    break;
                case "nn":
                    offsetX = 0;
                    offsetY = -cellSize / 3f;
                    break;
                case "np":
                    offsetX = cellSize / 3f;
                    offsetY = -cellSize / 3f;
                    break;
                case "cl":
                    offsetX = -cellSize / 3f;
                    offsetY = 0;
                    break;
                case "cc":
                    offsetX = 0;
                    offsetY = 0;
                    break;
                case "cp":
                    offsetX = cellSize / 3f;
                    offsetY = 0;
                    break;
                case "tl":
                    offsetX = -cellSize / 3f;
                    offsetY = cellSize / 3f;
                    break;
                case "tn":
                    offsetX = 0;
                    offsetY = cellSize / 3f;
                    break;
                case "tp":
                    offsetX = cellSize / 3f;
                    offsetY = cellSize / 3f;
                    break;
            }
            x = x + cellSize / 2 - mishenSize / 2 + offsetX;
            y = y + cellSize / 2 - mishenSize / 2 + offsetY;
        } else if (type.equals("Laser")) {
            String position = parts[1];
            float rotation = Float.parseFloat(parts[2]);
            texture = createLaserCircleTexture();
            float offsetX = 0;
            float offsetY = 0;
            switch (position) {
                case "nl":
                    offsetX = 0;
                    offsetY = 0;
                    break;
                case "nn":
                    offsetX = cellSize / 2 - laserSize / 2;
                    offsetY = 0;
                    break;
                case "np":
                    offsetX = cellSize - laserSize;
                    offsetY = 0;
                    break;
                case "cl":
                    offsetX = 0;
                    offsetY = cellSize / 2 - laserSize / 2;
                    break;
                case "cc":
                    offsetX = cellSize / 2 - laserSize / 2;
                    offsetY = cellSize / 2 - laserSize / 2;
                    break;
                case "cp":
                    offsetX = cellSize - laserSize;
                    offsetY = cellSize / 2 - laserSize / 2;
                    break;
                case "tl":
                    offsetX = 0;
                    offsetY = cellSize - laserSize;
                    break;
                case "tn":
                    offsetX = cellSize / 2 - laserSize / 2;
                    offsetY = cellSize - laserSize;
                    break;
                case "tp":
                    offsetX = cellSize - laserSize;
                    offsetY = cellSize - laserSize;
                    break;
            }
            x = x + offsetX;
            y = y + offsetY;
            laserRotation = rotation;
            laserX = x;
            laserY = y;
            currentLaserPosition = position;
        } else {
            switch (type) {
                case "Block":
                    texture = blockTexture;
                    break;
                case "Ser":
                    texture = serTexture;
                    break;
                case "pustoi":
                    break;
            }
        }
        if (texture != null) {
            Image newImage = new Image(texture);
            if (type.equals("Mishen")) {
                newImage.setSize(mishenSize, mishenSize);
            } else if (type.equals("Laser")) {
                newImage.setSize(laserSize, laserSize);
                newImage.setOrigin(laserSize / 2, laserSize / 2);
                newImage.setRotation(laserRotation);
                newImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String currentPosition = logic.getLaserPositions().get(cellKey);
                        if (currentPosition != null) {
                            String[] parts = currentPosition.split("_");
                            String position = parts[0];
                            float rotation = parts.length > 1 ? Float.parseFloat(parts[1]) : 0;
                            String newPosition = logic.getNewLaserPosition(position);
                            logic.setCell(i, j, "Laser_" + newPosition + "_" + rotation);
                            updateCellImage(i, j, "Laser_" + newPosition + "_" + rotation);
                            currentLaserPosition = newPosition;
                            float[] offset = calculateLaserOffset(currentLaserPosition);
                            laserX = gridStartX + i * (cellSize + cellSpacing) + offset[0];
                            laserY = gridStartY + j * (cellSize + cellSpacing) + offset[1];
                        }
                    }
                });
            } else {
                newImage.setSize(cellSize, cellSize);
                if ("Block".equals(type)) {
                    makeGridBlockDraggable(newImage, i, j);
                }
            }
            newImage.setPosition(x, y);
            stage.addActor(newImage);
            cellImages.put(cellKey, newImage);
        } else if (type.equals("pustoi")) {
        }
    }

    private void drawGrid() {
        if (!gridCreated) return;
        cellImages.clear();
        String[][] grid = logic.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                float x = gridStartX + i * (cellSize + cellSpacing);
                float y = gridStartY + j * (cellSize + cellSpacing);
                String cellKey = i + "_" + j;
                if (!cellImages.containsKey(cellKey)) {
                    final Image serImage = new Image(serTexture);
                    serImage.setSize(cellSize, cellSize);
                    serImage.setPosition(x, y);
                    stage.addActor(serImage);
                    cellImages.put(cellKey, serImage);
                }
            }
        }
    }

    @Override
    public void show() {
    }

    private void stopAndRewind() {
        playMusic.stop();
        playMusic.setPosition(0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        if (gridCreated && laserView != null) {
            laserView.draw();
        }
    }

    private void makeGridBlockDraggable(final Image img, final int i0, final int j0) {
        img.addListener(new DragListener() {
            float startX, startY;
            int ci = i0, cj = j0;

            @Override
            public void dragStart(InputEvent e, float x, float y, int p) {
                startX = img.getX();
                startY = img.getY();
                cellImages.remove(ci + "_" + cj);
                Gdx.graphics.setCursor(game.getDragCursor());
                img.toFront();
            }

            @Override
            public void drag(InputEvent e, float x, float y, int p) {
                img.moveBy(e.getStageX() - img.getX() - img.getWidth() / 2,
                    e.getStageY() - img.getY() - img.getHeight() / 2);
            }

            @Override
            public void dragStop(InputEvent e, float x, float y, int p) {
                int ni = -1, nj = -1;
                float px = e.getStageX();
                float py = e.getStageY();
                String[][] grid = logic.getGrid();
                outer:
                for (int i = 0; i < grid.length; i++)
                    for (int j = 0; j < grid[i].length; j++) {
                        float sx = gridStartX + i * (cellSize + cellSpacing);
                        float sy = gridStartY + j * (cellSize + cellSpacing);
                        if (px >= sx && px < sx + cellSize &&
                            py >= sy && py < sy + cellSize) {
                            ni = i;
                            nj = j;
                            break outer;
                        }
                    }
                boolean moved = ni != -1 && "Ser".equals(grid[ni][nj]);
                if (moved) {
                    String destKey = ni + "_" + nj;
                    Actor destActor = cellImages.remove(destKey);
                    if (destActor != null) destActor.remove();
                    if (!cellImages.containsKey(ci + "_" + cj))
                        updateCellImage(ci, cj, "Ser");
                    img.setPosition(gridStartX + ni * (cellSize + cellSpacing),
                        gridStartY + nj * (cellSize + cellSpacing));
                    cellImages.put(destKey, img);
                    logic.setCell(ci, cj, "Ser");
                    logic.setCell(ni, nj, "Block");
                    ci = ni;
                    cj = nj;
                } else {
                    img.setPosition(startX, startY);
                    cellImages.put(ci + "_" + cj, img);
                }
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        imageredactor.dispose();
        playMusic.dispose();
        serTexture.dispose();
        blockTexture.dispose();
        pustoiTexture.dispose();
        mishenTexture.dispose();
        if (laserView != null) {
            laserView.dispose();
        }
    }
}
