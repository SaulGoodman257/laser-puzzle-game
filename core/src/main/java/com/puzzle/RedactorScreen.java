package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.HashMap;
import java.util.Map;

public class RedactorScreen implements Screen {
    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture imageredactor;
    private Music playMusic;
    private Image backgroundImage;
    private Texture menu_redactor;
    private Texture menu_redactorback;
    private Texture menu_redactorok;
    private Texture menu_redactorsave;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private TextField widthField;
    private TextField heightField;
    private TextField levelNumberField;
    private String[][] currentGrid;
    private ShapeRenderer shapeRenderer;
    private boolean gridCreated = false;
    private float cellSize = 85;
    private float cellSpacing = 15;
    private float mishenSize = 30;
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

    private Map<String, String> mishenPositions = new HashMap<>();
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
        levelNumberField.setSize(100,40);
        widthField.setMessageText("Width 1-9");
        heightField.setMessageText("Height 1-6");
        levelNumberField.setMessageText("Level 1-3");
        stage.addActor(widthField);
        stage.addActor(heightField);
        stage.addActor(levelNumberField);
        shapeRenderer = new ShapeRenderer();
        serTexture = new Texture(Gdx.files.internal("Ser.png"));
        blockTexture = new Texture(Gdx.files.internal("Block.png"));
        pustoiTexture = new Texture(Gdx.files.internal("pustoi.png"));
        mishenTexture = new Texture(Gdx.files.internal("Mishen.png"));
        createBlockMenu();
        createUI();
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

                    if (levelNumber >= 1 && levelNumber <= 3 && height <=6 && height >=1 && width >=1 && width <= 9) {
                        currentGrid = createGrid(width, height);
                        gridCreated = true;
                        calculateGridStartPosition();
                        drawGrid();
                        addMouseWheelListenerToStage();
                        okButton.setTouchable(Touchable.disabled);
                        blockImage.setTouchable(Touchable.enabled);
                        serImage.setTouchable(Touchable.enabled);
                        pustoiImage.setTouchable(Touchable.enabled);
                        mishenImage.setTouchable(Touchable.enabled);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter numbers only.");
                }
            }
        });
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gridCreated) {
                    saveLevel();
                }
            }
        });

        stage.addActor(saveButton);
        stage.addActor(backButton);
        stage.addActor(okButton);
    }
    private void saveLevel() {
        try {
            int levelNumber = Integer.parseInt(levelNumberField.getText());
            if (levelNumber >= 1 && levelNumber <= 3) {
                Json json = new Json();
                json.setOutputType(JsonWriter.OutputType.json);
                String levelData = json.toJson(currentGrid);
                Preferences prefs = Gdx.app.getPreferences("LevelData");
                prefs.putString("level" + levelNumber, levelData);
                prefs.flush();
                System.out.println("Level saved successfully.");
            } else {
                System.out.println("Invalid level number. Enter 1, 2, or 3.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid level number format.");
        }
    }

    private void calculateGridStartPosition() {
        if (currentGrid != null) {
            gridStartX = (Gdx.graphics.getWidth() - (currentGrid.length * cellSize) - ((currentGrid.length - 1) * cellSpacing)) / 2;
            gridStartY = (Gdx.graphics.getHeight() - (currentGrid[0].length * cellSize) - ((currentGrid[0].length - 1) * cellSpacing)) / 2;
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
        pustoiImage.setPosition(1720,400);
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
                if(blockType.equals("Mishen")){
                    startX = image.getX();
                    startY = image.getY();
                    initialCellKey = getCellKey(event.getStageX(), event.getStageY());
                }
                draggedImage = new Image(image.getDrawable());
                if (blockType.equals("Mishen")) {
                    draggedImage.setSize(mishenSize, mishenSize);
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
                float dropX = event.getStageX();
                float dropY = event.getStageY();
                int cellI = -1;
                int cellJ = -1;
                for (int i = 0; i < currentGrid.length; i++) {
                    for (int j = 0; j < currentGrid[i].length; j++) {
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
                    if (draggedImageType.equals("Mishen")) {
                        String targetCellKey = cellI + "_" + cellJ;
                        if (initialCellKey != null && !initialCellKey.equals(targetCellKey)) {
                            mishenPositions.remove(initialCellKey);
                            updateCellImage(Integer.parseInt(initialCellKey.split("_")[0]), Integer.parseInt(initialCellKey.split("_")[1]), "pustoi");
                        }
                        String currentPosition = mishenPositions.get(targetCellKey);
                        if (currentPosition == null) {
                            currentGrid[cellI][cellJ] = draggedImageType + "_cc";
                            mishenPositions.put(targetCellKey, "cc");
                            updateCellImage(cellI, cellJ, draggedImageType + "_cc");
                        } else {
                            String newPosition = getNewMishenPosition(currentPosition);
                            currentGrid[cellI][cellJ] = draggedImageType + "_" + newPosition;
                            mishenPositions.put(targetCellKey, newPosition);
                            updateCellImage(cellI, cellJ, draggedImageType + "_" + newPosition);
                        }
                    } else {
                        currentGrid[cellI][cellJ] = draggedImageType;
                        updateCellImage(cellI, cellJ, draggedImageType);
                    }
                }
                if (draggedImageType.equals("Mishen") && (cellI == -1 || cellJ == -1)) {
                    image.setPosition(startX, startY);
                }
                draggedImage.remove();
                draggedImage = null;
                draggedImageType = null;
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
            private String getNewMishenPosition(String currentPosition) {
                if (currentPosition.equals("cc")) return "tl";
                if (currentPosition.equals("tl")) return "tn";
                if (currentPosition.equals("tn")) return "tp";
                if (currentPosition.equals("tp")) return "cp";
                if (currentPosition.equals("cp")) return "cl";
                if (currentPosition.equals("cl")) return "nl";
                if (currentPosition.equals("nl")) return "nn";
                if (currentPosition.equals("nn")) return "np";
                if (currentPosition.equals("np")) return "cc";
                return "cc";
            }
        });
    }
    private void addMouseWheelListenerToStage() {
        stage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                if (!gridCreated) return false;
                for (int i = 0; i < currentGrid.length; i++) {
                    for (int j = 0; j < currentGrid[i].length; j++) {
                        String cellKey = i + "_" + j;
                        String currentPosition = mishenPositions.get(cellKey);
                        if (currentPosition != null) {
                            float cellStartX = gridStartX + i * (cellSize + cellSpacing);
                            float cellStartY = gridStartY + j * (cellSize + cellSpacing);
                            float cellEndX = cellStartX + cellSize;
                            float cellEndY = cellStartY + cellSize;
                            if (x >= cellStartX && x < cellEndX && y >= cellStartY && y < cellEndY) {
                                String newPosition = getNewMishenPositionByScroll(currentPosition, amountY);
                                currentGrid[i][j] = "Mishen_" + newPosition;
                                mishenPositions.put(cellKey, newPosition);
                                updateCellImage(i, j, "Mishen_" + newPosition);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
    }
    private String getNewMishenPositionByScroll(String currentPosition, float scrollAmount) {
        String[] positions = {"cc", "tl", "tn", "tp", "cp", "cl", "nl", "nn", "np"};
        int currentIndex = -1;
        for (int i = 0; i < positions.length; i++) {
            if (positions[i].equals(currentPosition)) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) return "cc";
        int newIndex = (currentIndex + (scrollAmount > 0 ? 1 : -1)) % positions.length;
        if (newIndex < 0) newIndex += positions.length;
        return positions[newIndex];
    }
    private String getCellKey(float x, float y) {
        for (int i = 0; i < currentGrid.length; i++) {
            for (int j = 0; j < currentGrid[i].length; j++) {
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
        if (parts.length > 1) {
            String position = parts[1];
            if (type.equals("Mishen")) {
                texture = mishenTexture;
                float offsetX = 0;
                float offsetY = 0;
                switch (position) {
                    case "nl":
                        offsetX = -mishenSize / 2;
                        offsetY = -mishenSize / 2;
                        break;
                    case "nn":
                        offsetX = 0;
                        offsetY = -mishenSize / 2;
                        break;
                    case "np":
                        offsetX = mishenSize / 2;
                        offsetY = -mishenSize / 2;
                        break;
                    case "cl":
                        offsetX = -mishenSize / 2;
                        offsetY = 0;
                        break;
                    case "cc":
                        offsetX = 0;
                        offsetY = 0;
                        break;
                    case "cp":
                        offsetX = mishenSize / 2;
                        offsetY = 0;
                        break;
                    case "tl":
                        offsetX = -mishenSize / 2;
                        offsetY = mishenSize / 2;
                        break;
                    case "tn":
                        offsetX = 0;
                        offsetY = mishenSize / 2;
                        break;
                    case "tp":
                        offsetX = mishenSize / 2;
                        offsetY = mishenSize / 2;
                        break;
                }
                x = x + cellSize / 2 - mishenSize / 2 + offsetX;
                y = y + cellSize / 2 - mishenSize / 2 + offsetY;
            }
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
            } else {
                newImage.setSize(cellSize, cellSize);
            }
            newImage.setPosition(x, y);
            stage.addActor(newImage);
            cellImages.put(cellKey, newImage);
        } else if (type.equals("pustoi")) {
        }
    }

    private String[][] createGrid(int width, int height) {
        String[][] grid = new String[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = "Ser";
            }
        }
        return grid;
    }
    private void drawGrid() {
        if (!gridCreated) return;
        cellImages.clear();
        for (int i = 0; i < currentGrid.length; i++) {
            for (int j = 0; j < currentGrid[i].length; j++) {
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
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        calculateGridStartPosition();
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
        shapeRenderer.dispose();
        serTexture.dispose();
    }
}
