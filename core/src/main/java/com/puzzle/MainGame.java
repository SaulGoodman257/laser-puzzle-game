package com.puzzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.MathUtils;

public class MainGame extends Game {
    public SpriteBatch batch;
    private Music backgroundMusic;
    private String backgroundMusicFile = "music.mp3";
    private Cursor customCursor;
    private Cursor dragCursor;
    private int gameWidth = 1920;
    private int gameHeight = 1080;
    private boolean isFullscreen = false;
    private float globalVolume = 0.5f;
    private Preferences prefs;
    private Music levelMusic;
    private boolean isLoggedIn = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(backgroundMusicFile));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(globalVolume);
        playBackgroundMusic();
        Pixmap dragCursorPixmap = new Pixmap(Gdx.files.internal("drag_cursor.png"));
        dragCursor = Gdx.graphics.newCursor(dragCursorPixmap, 0, 0);
        dragCursorPixmap.dispose();
        prefs = Gdx.app.getPreferences("Settings");
        globalVolume = prefs.getFloat("globalVolume", 0.5f);
        isFullscreen = prefs.getBoolean("isFullscreen", false);
        applyResolutionSettings();
        backgroundMusic.setVolume(globalVolume);
        Pixmap cursorPixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        int xHotspot = 0;
        int yHotspot = 0;
        customCursor = Gdx.graphics.newCursor(cursorPixmap, xHotspot, yHotspot);
        cursorPixmap.dispose();
        Gdx.graphics.setCursor(customCursor);
        setScreen(new MenuScreen(this));
    }
    public float getGlobalVolume() {
        return globalVolume;
    }
    public void setGlobalVolume(float volume) {
        this.globalVolume = MathUtils.clamp(volume, 0f, 1f);
        updateAllAudioVolumes();
        prefs.putFloat("globalVolume", globalVolume);
        prefs.flush();
    }
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        prefs.putBoolean("isLoggedIn", isLoggedIn);
        prefs.flush();
    }
    public void updateAllAudioVolumes() {
        backgroundMusic.setVolume(globalVolume);
    }
    @Override
    public void dispose() {
        batch.dispose();
        stopBackgroundMusic();
        backgroundMusic.dispose();
        customCursor.dispose();
        dragCursor.dispose();
    }
    public void playLevelMusic() {
        if (levelMusic == null) {
            levelMusic = Gdx.audio.newMusic(Gdx.files.internal("music_play.mp3"));
            levelMusic.setLooping(true);
            levelMusic.setVolume(globalVolume);
        }
        if (!levelMusic.isPlaying()) {
            levelMusic.play();
        }
    }

    public void stopLevelMusic() {
        if (levelMusic != null) {
            levelMusic.stop();
        }
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
    private void applyResolutionSettings() {
        int screenWidth = Gdx.graphics.getDisplayMode().width;
        int screenHeight = Gdx.graphics.getDisplayMode().height;
        if (screenWidth > gameWidth || screenHeight > gameHeight) {
            isFullscreen = false;
            prefs.putBoolean("isFullscreen", false);
            prefs.flush();
            Gdx.graphics.setWindowedMode(gameWidth, gameHeight);
        } else if (screenWidth == gameWidth && screenHeight == gameHeight && isFullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(gameWidth, gameHeight);
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
        int screenWidth = Gdx.graphics.getDisplayMode().width;
        int screenHeight = Gdx.graphics.getDisplayMode().height;
        if (fullscreen && (screenWidth != gameWidth || screenHeight != gameHeight)) {
            return;
        }
        if (fullscreen != isFullscreen) {
            if (fullscreen) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(gameWidth, gameHeight);
            }
            isFullscreen = fullscreen;
            prefs.putBoolean("isFullscreen", isFullscreen);
            prefs.flush();
        }
    }
    public boolean isFullscreen() {
        return isFullscreen;
    }

}

