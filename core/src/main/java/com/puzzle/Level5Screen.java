package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;


public class Level5Screen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture level5Image;
    private Music PlayMusic;
    private Image backgroundImage;
    private Texture level5_back;
    private Texture level5_next;
    private Texture level5_nazad;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private Texture congratulationsTexture;
    private Image congratulationsImage;
    private Game gameLogic;
    private boolean isWin = false;
    private Stage congratulationStage;
    private String[][] level5Grid = {
        {"pustoi",               "Block",          "Ser",        "Ser" ,           "Ser",            "pustoi"               },
        {"Block",                 "Ser",         "pustoi",       "Ser" ,            "Mishen_cl",        "Ser"                  },
        {"Block",              "Ser",          "Ser",           "pustoi",         "pustoi",            "Ser"                  },
        {"Laser_cp_132.5",      "Ser",         "Block",           "Ser" ,         "Block",          "pustoi"               }
    };
    public Level5Screen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(camera), game.batch);
        congratulationStage = new Stage(new ScreenViewport(), game.batch);
        Gdx.input.setInputProcessor(stage);
        level5Image = new Texture(Gdx.files.internal("level5_menu.png"));
        level5_back = new Texture(Gdx.files.internal("level5_back.png"));
        level5_next = new Texture(Gdx.files.internal("level5_next.png"));
        level5_nazad = new Texture(Gdx.files.internal("level5_nazad.png"));
        congratulationsTexture = new Texture(Gdx.files.internal("congratilations5.png"));
        backgroundImage = new Image(level5Image);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        game.playLevelMusic();
        gameLogic = new Game(level5Grid, stage, game);
        createUI();
    }

    private void createUI() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        TextButton backButton = new TextButton("", textButtonStyle);
        backButton.setBounds(783, 35, 350, 103);
        TextButton nextButton = new TextButton("", textButtonStyle);
        nextButton.setBounds(1460, 35, 110, 110);
        TextButton nazadButton = new TextButton("", textButtonStyle);
        nazadButton.setBounds(350, 35, 110, 110);
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level5_back).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level5Image).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (isWin) {
                    backgroundImage.setDrawable(new Image(level5_next).getDrawable());
                    Gdx.graphics.setCursor(game.getDragCursor());
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level5Image).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nazadButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level5_nazad).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level5Image).getDrawable());
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
                game.setScreen(new PlayScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nazadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new Level4Screen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isWin) {
                    buttonClickSound.play(game.getGlobalVolume());
                    game.setScreen(new Level6Screen(game));
                    Gdx.graphics.setCursor(game.getCustomCursor());
                }
            }
        });

        stage.addActor(backButton);
        stage.addActor(nextButton);
        stage.addActor(nazadButton);
    }

    public void showCongratulations() {
        congratulationStage.clear();
        Image congratulationsImage = new Image(congratulationsTexture);
        congratulationsImage.setPosition(Gdx.graphics.getWidth() / 2f - congratulationsTexture.getWidth() / 2f, 600);
        congratulationsImage.getColor().a = 0;
        congratulationsImage.addAction(Actions.fadeIn(0.5f));
        congratulationStage.addActor(congratulationsImage);
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
            showCongratulations();
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
        level5Image.dispose();
        gameLogic.dispose();
        disposeSettingsMusic();
    }
}
