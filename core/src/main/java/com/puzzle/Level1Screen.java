package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;


public class Level1Screen implements Screen {

    private final MainGame game;

    private OrthographicCamera camera;
    private Stage stage;
    private Texture level1Image;
    private Music PlayMusic;
    private Image backgroundImage;
    private Texture level1_back;
    private Texture level1_next;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private Game gameLogic;
    private String[][] level1Grid = {
        {"Ser", "Ser", "Block", "Ser"},
        {"Ser", "Ser", "Mishen", "Block"},
        {"Ser", "Ser", "Laser", "Ser"},
        {"Ser", "Ser", "Ser", "Block"}
    };

    public Level1Screen(final MainGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport(camera), game.batch);
        Gdx.input.setInputProcessor(stage);

        level1Image = new Texture(Gdx.files.internal("level1_menu.Png"));
        level1_back = new Texture(Gdx.files.internal("level1_back.png"));
        level1_next = new Texture(Gdx.files.internal("level1_next.png"));
        backgroundImage = new Image(level1Image);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        PlayMusic = Gdx.audio.newMusic(Gdx.files.internal("music_play.mp3"));
        PlayMusic.setLooping(true);
        PlayMusic.setVolume(0.5f);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        PlayMusic.play();
        gameLogic = new Game(level1Grid, stage, game);

        createUI();
    }

    private void createUI() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        TextButton backButton = new TextButton("", textButtonStyle);
        backButton.setBounds(783, 35, 350, 103);
        TextButton nextButton = new TextButton("", textButtonStyle);
        nextButton.setBounds(1460, 35, 110, 110);
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level1_back).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level1Image).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level1_next).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level1Image).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stopAndRewind();
                game.rewindBackgroundMusic();
                PlayMusic.stop();
                game.rewindBackgroundMusic();
                game.playBackgroundMusic();
                buttonClickSound.play(0.4f);
                game.setScreen(new PlayScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        stage.addActor(backButton);
        stage.addActor(nextButton);
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
        level1Image.dispose();
        disposeSettingsMusic();
    }
}
