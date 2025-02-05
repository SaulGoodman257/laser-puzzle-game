package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;


public class CreateLevel3Screen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture level3createImage;
    private Music PlayMusic;
    private Image backgroundImage;
    private Texture level3create_back;
    private Texture level3create_bot;
    private Texture level3create_nazad;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private Game gameLogic;
    private boolean isWin = false;
    private Stage congratulationStage;
    private String[][] level3create;
    public CreateLevel3Screen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(camera), game.batch);
        congratulationStage = new Stage(new ScreenViewport(), game.batch);
        Gdx.input.setInputProcessor(stage);
        level3createImage = new Texture(Gdx.files.internal("create_level3_menu.png"));
        level3create_back = new Texture(Gdx.files.internal("create_level3_back.png"));
        level3create_bot = new Texture(Gdx.files.internal("create_level3_bot.png"));
        level3create_nazad =  new Texture(Gdx.files.internal("create_level3_nazad.png"));
        backgroundImage = new Image(level3createImage);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        game.playLevelMusic();
        level3create = loadLevelData(3);
        gameLogic = new Game(level3create, stage, game);
        createUI();
    }
    private String[][] loadLevelData(int levelNumber) {
        Preferences prefs = Gdx.app.getPreferences("LevelData");
        String levelData = prefs.getString("level" + levelNumber, null);
        if (levelData != null) {
            Json json = new Json();
            return json.fromJson(String[][].class, levelData);
        } else {
            return new String[][] {
                {"Ser", "Ser", "Ser", "Ser"},
                {"Ser", "Ser", "Ser", "Ser"},
                {"Ser", "Ser", "Ser", "Ser"},
                {"Ser", "Ser", "Ser", "Ser"}
            };
        }
    }

    private void createUI() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        TextButton backButton = new TextButton("", textButtonStyle);
        backButton.setBounds(783, 35, 350, 103);
        TextButton nazadButton = new TextButton("", textButtonStyle);
        nazadButton.setBounds(350, 35, 110, 110);
        TextButton botButton = new TextButton("", textButtonStyle);
        botButton.setBounds(853, 155, 200, 60);
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level3create_back).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level3createImage).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        botButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level3create_bot).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level3createImage).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nazadButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level3create_nazad).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level3createImage).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.stopLevelMusic();
                game.getBackgroundMusic().play();
                game.rewindBackgroundMusic();
                game.playBackgroundMusic();
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new CreateLevelScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nazadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new CreateLevel2Screen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        botButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameLogic.isBotButtonEnabled()) {
                    gameLogic.setBotUsed(true);
                    Bot bot = new Bot(level3create);
                    String[][] solvedGrid = bot.getSolvedGrid();
                    botButton.setTouchable(Touchable.disabled);
                    if (solvedGrid != null) {
                        gameLogic.updateGrid(solvedGrid);
                        gameLogic.setBotSolved(true);
                    } else {
                        System.out.println("unluck");
                    }
                    gameLogic.setBotButtonEnabled(false);
                }
            }
        });

        stage.addActor(botButton);
        stage.addActor(backButton);
        stage.addActor(nazadButton);
    }

    public void playSettingsMusic() {
        if (!PlayMusic.isPlaying()) {
            PlayMusic.play();
        }
    }
    private void stopAndRewind() {
        PlayMusic.stop();
        PlayMusic.setPosition(0);
    }


    public void stopSettingsMusic() {
        PlayMusic.stop();
    }
    public void disposeSettingsMusic() {
        PlayMusic.dispose();
    }
    @Override
    public void show() {

    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        gameLogic.drawLaserLines();
        if (gameLogic.isWin() && !isWin) {
            isWin = true;
            if (gameLogic.isBotSolved()) {
                gameLogic.drawWinningGrid();
                gameLogic.redrawLasers();
            }
        }
        if (isWin) {
            congratulationStage.act(delta);
            congratulationStage.draw();
        }
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
        level3createImage.dispose();
        gameLogic.dispose();
        disposeSettingsMusic();
    }
}
