package com.puzzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Cursor;

public class MainGame extends Game {

    public SpriteBatch batch;
    private Music backgroundMusic;
    private String backgroundMusicFile = "music.mp3";
    private Cursor customCursor;

    @Override
    public void create() {
        batch = new SpriteBatch();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(backgroundMusicFile));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        playBackgroundMusic();

        Pixmap cursorPixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        int xHotspot = 0;
        int yHotspot = 0;
        customCursor = Gdx.graphics.newCursor(cursorPixmap, xHotspot, yHotspot);
        cursorPixmap.dispose();
        Gdx.graphics.setCursor(customCursor);

        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        stopBackgroundMusic();
        backgroundMusic.dispose();
        customCursor.dispose();
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }
    public void rewindBackgroundMusic() {
        backgroundMusic.stop();
        backgroundMusic.setPosition(0);
    }
    public void playBackgroundMusic() {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    public void stopBackgroundMusic() {
        backgroundMusic.stop();
    }
}

