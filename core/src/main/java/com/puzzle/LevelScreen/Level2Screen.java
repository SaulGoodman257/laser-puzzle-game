package com.puzzle.LevelScreen;

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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.puzzle.Bot;
import com.puzzle.MainGame;
import com.puzzle.Render.GameView;
import com.puzzle.UI.PlayScreen;
import com.puzzle.logic.GameLogic;


public class Level2Screen implements Screen {

    private final MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Texture level2Image;
    private Image backgroundImage;
    private Texture level2_back;
    private Texture level2_next;
    private Texture level2_nazad;
    private Texture level2_bot;
    private Sound buttonClickSound;
    private Preferences progressPrefs;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private Texture congratulationsTexture;
    private GameLogic logic;
    private GameView view;
    private boolean isWin = false;
    private Stage congratulationStage;
    private String[][] level2Grid = {
        {"pustoi","Laser_tp_309","pustoi"},
        {"Ser","Ser","Block"},
        {"Ser","Block","Ser"},
        {"Ser","Mishen_cp","Block"}
    };
    public Level2Screen(final MainGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(camera), game.batch);
        congratulationStage = new Stage(new ScreenViewport(), game.batch);
        Gdx.input.setInputProcessor(stage);
        level2Image = new Texture(Gdx.files.internal("level2_menu.png"));
        level2_back = new Texture(Gdx.files.internal("level2_back.png"));
        level2_next = new Texture(Gdx.files.internal("level2_next.png"));
        level2_nazad = new Texture(Gdx.files.internal("level2_nazad.png"));
        level2_bot = new Texture(Gdx.files.internal("level2_bot.png"));
        congratulationsTexture = new Texture(Gdx.files.internal("congratilations2.png"));
        backgroundImage = new Image(level2Image);
        backgroundImage.setSize(gameWidth, gameHeight);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);
        progressPrefs = Gdx.app.getPreferences("LevelProgress");
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("music_button.mp3"));
        game.playLevelMusic();
        logic = new GameLogic(level2Grid);
        view  = new GameView(logic, stage, game);
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
        TextButton botButton = new TextButton("", textButtonStyle);
        botButton.setBounds(853, 155, 200, 60);
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level2_back).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level2Image).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                int maxUnlocked = progressPrefs.getInteger("maxUnlockedLevel", 2);
                if (maxUnlocked >= 3) {
                    backgroundImage.setDrawable(new Image(level2_next).getDrawable());
                    Gdx.graphics.setCursor(game.getDragCursor());
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level2Image).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        botButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level2_bot).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level2Image).getDrawable());
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nazadButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                backgroundImage.setDrawable(new Image(level2_nazad).getDrawable());
                Gdx.graphics.setCursor(game.getDragCursor());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                backgroundImage.setDrawable(new Image(level2Image).getDrawable());
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
                game.setScreen(new Level1Screen(game));
                Gdx.graphics.setCursor(game.getCustomCursor());
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int maxUnlocked = progressPrefs.getInteger("maxUnlockedLevel", 2);
                if (maxUnlocked >= 3) {
                    buttonClickSound.play(game.getGlobalVolume());
                    game.setScreen(new Level3Screen(game));
                    Gdx.graphics.setCursor(game.getCustomCursor());
                }
            }
        });
        botButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (logic.isBotButtonEnabled()) {
                    logic.setBotUsed(true);
                    Bot bot = new Bot(level2Grid);
                    String[][] solvedGrid = bot.getSolvedGrid();
                    botButton.setTouchable(Touchable.disabled);
                    if (solvedGrid != null) {
                        logic.updateGrid(solvedGrid);
                        view.refreshGrid();
                        logic.setBotSolved(true);
                        view.redrawLasers();
                    } else {
                        System.out.println("unluck");
                    }
                    logic.setBotButtonEnabled(false);
                }
            }
        });

        stage.addActor(botButton);
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
    @Override
    public void show() {

    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        view.render(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        if (logic.isWin() && !isWin) {
            isWin = true;
            int prev = progressPrefs.getInteger("maxUnlockedLevel", 2);
            if (prev < 3) {
                progressPrefs.putInteger("maxUnlockedLevel", 3);
                progressPrefs.flush();
            }
            showCongratulations();
            if (logic.isBotSolved()) {
                view.redrawLasers();
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
        level2Image.dispose();
        view.dispose();
    }
}
