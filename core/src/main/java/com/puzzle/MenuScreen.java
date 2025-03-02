package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture menuImage;
    private Texture settingsHoverImage;
    private Texture playHoverImage;
    private Texture exitHoverImage;
    private Image backgroundImage;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private Dialog loginDialog;
    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;
    private TextButton skipButton; // Кнопка "Пропустить"
    private boolean loginDialogShown = false;


    public MenuScreen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera(gameWidth, gameHeight);
        camera.position.set(gameWidth / 2, gameHeight / 2, 0);
        camera.update();
        stage = new Stage(new ScreenViewport(camera), game.batch);
        Gdx.input.setInputProcessor(stage);
        menuImage = new Texture(Gdx.files.internal("menu.png"));
        settingsHoverImage = new Texture(Gdx.files.internal("Settings.png"));
        playHoverImage = new Texture(Gdx.files.internal("play.png"));
        exitHoverImage = new Texture(Gdx.files.internal("Exit.png"));
        backgroundImage = new Image(menuImage);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        createUI();
    }
    private void createUI() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        final TextButton playButton = new TextButton("", textButtonStyle);
        playButton.setBounds(779, 550, 350, 103);

        final TextButton settingsButton = new TextButton("", textButtonStyle);
        settingsButton.setBounds(779, 430, 350, 94);

        final TextButton exitButton = new TextButton("", textButtonStyle);
        exitButton.setBounds(779, 298, 350, 103);
        playButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(playHoverImage).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(menuImage).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        settingsButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(settingsHoverImage).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(menuImage).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(exitHoverImage).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(menuImage).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new PlayScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new SettingsScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                Gdx.app.exit();
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        stage.addActor(playButton);
        stage.addActor(settingsButton);
        stage.addActor(exitButton);
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
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
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
        menuImage.dispose();
        settingsHoverImage.dispose();
        playHoverImage.dispose();
        exitHoverImage.dispose();
        buttonClickSound.dispose();
        if (loginDialog != null) {
            loginDialog.remove();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
