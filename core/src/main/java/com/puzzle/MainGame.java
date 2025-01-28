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
    private Cursor dragCursor;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private boolean isFullscreen = true;

    @Override
    public void create() {
        batch = new SpriteBatch();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(backgroundMusicFile));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.1f);
        playBackgroundMusic();
        Pixmap dragCursorPixmap = new Pixmap(Gdx.files.internal("drag_cursor.png"));
        dragCursor = Gdx.graphics.newCursor(dragCursorPixmap, 0, 0);
        dragCursorPixmap.dispose();

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
        dragCursor.dispose();
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

    public Cursor getDragCursor() {
        return dragCursor;
    }
    public Cursor getCustomCursor() {
        return customCursor;
    }
    public void setFullscreen(boolean fullscreen) {
        if (fullscreen != isFullscreen) { // Prevent unnecessary mode switching
            if (fullscreen) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(gameWidth, gameHeight);
            }
            isFullscreen = fullscreen; // Update fullscreen state
        }
    }
    public boolean isFullscreen() {
        return isFullscreen;
    }

}

