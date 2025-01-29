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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SettingsScreen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture settingsImage;
    private Music settingsMusic;
    private Image backgroundImage;
    private Texture menu_settigs_back;
    private Texture menu_settingsturnfull;
    private Texture menu_settingsturnfullback;
    private Texture menu_settingsturnwindows;
    private Texture menu_settingsturnwindowsback;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private Texture currentBackgroundTexture;
    private boolean isFullscreen;
    private Slider volumeSlider;

    public SettingsScreen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(camera), game.batch);
        Gdx.input.setInputProcessor(stage);
        settingsImage = new Texture(Gdx.files.internal("menu.settings.Png"));
        menu_settigs_back = new Texture(Gdx.files.internal("menu.settingsback.png"));
        menu_settingsturnfull=new Texture(Gdx.files.internal("menu_settingsturnfull.png"));
        menu_settingsturnfullback=new Texture(Gdx.files.internal("menu_settingsturnfullback.png"));
        menu_settingsturnwindows=new Texture(Gdx.files.internal("menu_settingsturnwindows.png"));
        menu_settingsturnwindowsback=new Texture(Gdx.files.internal("menu_settingsturnwindowsback.png"));
        backgroundImage = new Image(settingsImage);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        settingsMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        settingsMusic.setLooping(true);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        currentBackgroundTexture = settingsImage;
        isFullscreen = game.isFullscreen();
        updateBackgroundTexture();
        createUI();
        createVolumeSlider();
    }
    private void updateBackgroundTexture() {
        if(isFullscreen){
            backgroundImage.setDrawable(new Image(menu_settingsturnfull).getDrawable());
            currentBackgroundTexture = menu_settingsturnfull;
        } else {
            backgroundImage.setDrawable(new Image(menu_settingsturnwindows).getDrawable());
            currentBackgroundTexture = menu_settingsturnwindows;
        }
    }

    private void createUI() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        final TextButton backButton = new TextButton("", textButtonStyle);
        backButton.setBounds(783, 35, 350, 103);
        final TextButton fullButton = new TextButton("", textButtonStyle);
        fullButton.setBounds(735, 930, 395, 70);
        final TextButton windowButton = new TextButton("", textButtonStyle);
        windowButton.setBounds(735, 805, 347, 70);
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (currentBackgroundTexture == settingsImage) {
                    backgroundImage.setDrawable(new Image(menu_settigs_back).getDrawable());
                } else if (currentBackgroundTexture == menu_settingsturnfull) {
                    backgroundImage.setDrawable(new Image(menu_settingsturnfullback).getDrawable());
                } else if (currentBackgroundTexture == menu_settingsturnwindows) {
                    backgroundImage.setDrawable(new Image(menu_settingsturnwindowsback).getDrawable());
                }
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(currentBackgroundTexture).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        fullButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_settingsturnfull).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                updateBackgroundTexture();
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        windowButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_settingsturnwindows).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                updateBackgroundTexture();
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        backButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new MenuScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        fullButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                isFullscreen = true;
                game.setFullscreen(true);
                updateBackgroundTexture();
            }
        });
        windowButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                isFullscreen = false;
                game.setFullscreen(false);
                updateBackgroundTexture();
            }
        });
        stage.addActor(backButton);
        stage.addActor(fullButton);
        stage.addActor(windowButton);
    }
    private void createVolumeSlider() {
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        volumeSlider = new Slider(0f, 1f, 0.05f, false, skin);
        volumeSlider.setValue(game.getGlobalVolume());
        volumeSlider.setBounds(740, 750, 350, 50);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                game.setGlobalVolume(volume);
            }
        });
        stage.addActor(volumeSlider);
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

