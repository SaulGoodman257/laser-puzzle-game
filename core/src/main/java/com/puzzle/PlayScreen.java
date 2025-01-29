package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

public class PlayScreen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture imageplay;
    private Music playMusic;
    private Image backgroundImage;
    private Texture menu_play_back;
    private Texture menu_play_one;
    private Texture menu_play_two;
    private Texture menu_play_three;
    private Texture menu_play_four;
    private Texture menu_play_five;
    private Texture menu_play_six;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;

    public PlayScreen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(camera), game.batch);
        Gdx.input.setInputProcessor(stage);
        imageplay = new Texture(Gdx.files.internal("menu.play.Png"));
        menu_play_back = new Texture(Gdx.files.internal("menu.playback.png"));
        menu_play_one = new Texture(Gdx.files.internal("menu.play1.png"));
        menu_play_two = new Texture(Gdx.files.internal("menu.play2.png"));
        menu_play_three = new Texture(Gdx.files.internal("menu.play3.png"));
        menu_play_four = new Texture(Gdx.files.internal("menu.play4.png"));
        menu_play_five = new Texture(Gdx.files.internal("menu.play5.png"));
        menu_play_six = new Texture(Gdx.files.internal("menu.play6.png"));
        backgroundImage = new Image(imageplay);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("music_play.mp3"));
        playMusic.setLooping(true);
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        createUI();
    }

    private void createUI() {

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();

        final TextButton backButton = new TextButton("", textButtonStyle);
        backButton.setBounds(783, 35, 350, 103);

        final TextButton oneButton = new TextButton("", textButtonStyle);
        oneButton.setBounds(508, 810, 110, 110);

        final TextButton twobutton = new TextButton("", textButtonStyle);
        twobutton.setBounds(666, 810, 110, 110);

        final TextButton threebutton = new TextButton("", textButtonStyle);
        threebutton.setBounds(824, 810, 110, 110);

        final TextButton fourbutton = new TextButton("", textButtonStyle);
        fourbutton.setBounds(982, 810, 110, 110);

        final TextButton fivebutton = new TextButton("", textButtonStyle);
        fivebutton.setBounds(1140, 810, 110, 110);

        final TextButton sixbutton = new TextButton("", textButtonStyle);
        sixbutton.setBounds(1298, 810, 110, 110);


        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_play_back).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageplay).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        oneButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_play_one).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageplay).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        twobutton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_play_two).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageplay).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        threebutton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_play_three).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageplay).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        fourbutton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_play_four).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageplay).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        fivebutton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_play_five).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageplay).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        sixbutton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_play_six).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imageplay).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new MenuScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        oneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getBackgroundMusic().pause();
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new Level1Screen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        stage.addActor(backButton);
        stage.addActor(oneButton);
        stage.addActor(twobutton);
        stage.addActor(threebutton);
        stage.addActor(fourbutton);
        stage.addActor(fivebutton);
        stage.addActor(sixbutton);
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
        imageplay.dispose();
        playMusic.dispose();
    }
}

