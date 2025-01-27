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

public class SettingsScreen implements Screen {

    private final MainGame game;

    private OrthographicCamera camera;
    private Stage stage;

    private Texture settingsImage;
    private Music settingsMusic;
    private Image backgroundImage;
    private Texture menu_settigs_back;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;

    public SettingsScreen(final MainGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(new ScreenViewport(camera), game.batch);
        Gdx.input.setInputProcessor(stage);

        settingsImage = new Texture(Gdx.files.internal("menu.settings.Png"));
        menu_settigs_back = new Texture(Gdx.files.internal("menu.settingsback.png"));
        backgroundImage = new Image(settingsImage);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        settingsMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        settingsMusic.setLooping(true);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));

        createUI();
    }

    private void createUI() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();

        TextButton backButton = new TextButton("", textButtonStyle);
        backButton.setBounds(783, 35, 350, 103);
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_settigs_back).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(settingsImage).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        backButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(0.4f);
                game.setScreen(new MenuScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        stage.addActor(backButton);
    }

    public void playSettingsMusic() {
        if (!settingsMusic.isPlaying()) {
            settingsMusic.play();
        }
    }

    public void stopSettingsMusic() {
        settingsMusic.stop();
    }

    public void disposeSettingsMusic() {
        settingsMusic.dispose();
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
        settingsImage.dispose();
        disposeSettingsMusic();
    }
}

