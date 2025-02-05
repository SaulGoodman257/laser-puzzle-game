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

public class CreateLevelScreen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture imagecreatelevel;
    private Music playMusic;
    private Image backgroundImage;
    private Texture menu_createlevel_createlevel;
    private Texture menu_createlevel_back;
    private Texture menu_createlevel_2;
    private Texture menu_createlevel_1;
    private Texture menu_createlevel_3;
    private Sound buttonClickSound;
    private int gameWidth = 1920;
    private int gameHeight = 1080;

    public CreateLevelScreen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(camera), game.batch);
        Gdx.input.setInputProcessor(stage);
        imagecreatelevel = new Texture(Gdx.files.internal("menu_createlevel_menu.png"));
        menu_createlevel_createlevel = new Texture(Gdx.files.internal("menu_createlevel_createlevel.png"));
        menu_createlevel_back = new Texture(Gdx.files.internal("menu_createlevel_back.png"));
        menu_createlevel_2 = new Texture(Gdx.files.internal("menu_createlevel_2.png"));
        menu_createlevel_1 = new Texture(Gdx.files.internal("menu_createlevel_1.png"));
        menu_createlevel_3 = new Texture(Gdx.files.internal("menu_createlevel_3.png"));
        backgroundImage = new Image(imagecreatelevel);
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

        final TextButton createButton = new TextButton("", textButtonStyle);
        createButton.setBounds(783, 175, 350, 103);

        final TextButton oneButton = new TextButton("", textButtonStyle);
        oneButton.setBounds(710, 750, 110, 110);

        final TextButton twobutton = new TextButton("", textButtonStyle);
        twobutton.setBounds(900, 750, 110, 110);

        final TextButton threebutton = new TextButton("", textButtonStyle);
        threebutton.setBounds(1100, 750, 110, 110);



        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_createlevel_back).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imagecreatelevel).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        oneButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_createlevel_1).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imagecreatelevel).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        twobutton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_createlevel_2).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imagecreatelevel).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        threebutton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_createlevel_3).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imagecreatelevel).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        createButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(menu_createlevel_createlevel).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(imagecreatelevel).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new PlayScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new RedactorScreen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        oneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getBackgroundMusic().pause();
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new CreateLevel1Screen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        twobutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getBackgroundMusic().pause();
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new CreateLevel2Screen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        threebutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getBackgroundMusic().pause();
                buttonClickSound.play(game.getGlobalVolume());
                game.setScreen(new CreateLevel3Screen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });

        stage.addActor(createButton);
        stage.addActor(backButton);
        stage.addActor(oneButton);
        stage.addActor(twobutton);
        stage.addActor(threebutton);
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
        imagecreatelevel.dispose();
        playMusic.dispose();
    }
}

