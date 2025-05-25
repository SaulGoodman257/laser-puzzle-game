package com.puzzle;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.puzzle.UI.MenuScreen;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainGameTest {

    Application app;
    Audio       audio;
    Files       files;
    Graphics    graphics;
    Input       input;
    Preferences prefs;
    FileHandle  musicFileHandle, cursorFileHandle, dragCursorFileHandle, levelMusicFileHandle;
    Music       music;
    Cursor      cursor, dragCursor;
    SpriteBatch batch;
    MockedConstruction<MenuScreen> menuScreenCons;
    @BeforeEach
    void setUp() {
        app      = mock(Application.class);
        audio    = mock(Audio.class);
        files    = mock(Files.class);
        graphics = mock(Graphics.class);
        input    = mock(Input.class);
        prefs    = mock(Preferences.class);
        Gdx.app      = app;
        Gdx.audio    = audio;
        Gdx.files    = files;
        Gdx.graphics = graphics;
        Gdx.input    = input;
        GL20 gl20 = mock(GL20.class, RETURNS_DEFAULTS);
        Gdx.gl20 = gl20;
        Gdx.gl   = gl20;
        musicFileHandle       = mock(FileHandle.class);
        cursorFileHandle      = mock(FileHandle.class);
        dragCursorFileHandle  = mock(FileHandle.class);
        levelMusicFileHandle  = mock(FileHandle.class);
        when(files.internal("music.mp3"))       .thenReturn(musicFileHandle);
        when(files.internal("cursor.png"))      .thenReturn(cursorFileHandle);
        when(files.internal("drag_cursor.png")) .thenReturn(dragCursorFileHandle);
        when(files.internal("music_play.mp3"))  .thenReturn(levelMusicFileHandle);
        music = mock(Music.class);
        when(audio.newMusic(any(FileHandle.class))).thenReturn(music);
        cursor     = mock(Cursor.class);
        dragCursor = mock(Cursor.class);
        when(graphics.newCursor(any(Pixmap.class), anyInt(), anyInt()))
            .thenReturn(cursor);
        Graphics.DisplayMode dm = Mockito.mock(
            Graphics.DisplayMode.class,
            withSettings()
                .useConstructor(2560, 1440, 60, 32)
                .defaultAnswer(CALLS_REAL_METHODS));
        when(graphics.getDisplayMode()).thenReturn(dm);
        when(prefs.getFloat(eq("globalVolume"),  anyFloat())).thenReturn(0.5f);
        when(prefs.getBoolean(eq("isFullscreen"), anyBoolean())).thenReturn(false);
        when(app.getPreferences(anyString())).thenReturn(prefs);
        batch = mock(SpriteBatch.class);
        menuScreenCons = Mockito.mockConstruction(MenuScreen.class,
            (mock, ctx) -> { /* ничего */});
    }
    @AfterEach
    void tearDown() {
        menuScreenCons.close();
    }
    @Test
    @DisplayName("setGlobalVolume корректно обновляет volume и Preferences")
    void setGlobalVolume_setsAndClamps() {
        MainGame game = new MainGame();
        game.backgroundMusic = music;
        game.prefs           = prefs;
        game.setGlobalVolume(0.5f);
        game.setGlobalVolume(2.5f);
        assertEquals(1f, game.getGlobalVolume(), 1e-5);
        verify(music, atLeastOnce()).setVolume(1f);
        verify(prefs).putFloat("globalVolume", 1f);
        game.setGlobalVolume(-10f);
        assertEquals(0f, game.getGlobalVolume(), 1e-5);
        verify(music, atLeastOnce()).setVolume(0f);
        verify(prefs, atLeastOnce()).putFloat("globalVolume", 0f);
    }

    @Test
    @DisplayName("updateAllAudioVolumes вызывает setVolume ровно один раз")
    void updateAllAudioVolumes_updatesVolume() {
        MainGame game = new MainGame();
        game.backgroundMusic = music;
        game.prefs           = prefs;
        game.setGlobalVolume(0.3f);
        reset(music);
        game.updateAllAudioVolumes();
        verify(music).setVolume(0.3f);
    }

    @Test
    @DisplayName("dispose освобождает все ресурсы")
    void dispose_releasesResources() {
        MainGame game = spy(new MainGame());
        game.batch           = batch;
        game.backgroundMusic = music;
        game.customCursor    = cursor;
        game.dragCursor      = dragCursor;
        doNothing().when(game).stopBackgroundMusic();
        game.dispose();
        verify(batch).dispose();
        verify(music).dispose();
        verify(cursor).dispose();
        verify(dragCursor).dispose();
        verify(game).stopBackgroundMusic();
    }

    @Test
    @DisplayName("playLevelMusic создаёт и воспроизводит levelMusic")
    void playLevelMusic_worksCorrectly() {
        MainGame game = new MainGame();
        game.backgroundMusic = music;
        game.prefs           = prefs;
        game.setGlobalVolume(0.8f);
        reset(music);
        when(music.isPlaying()).thenReturn(false);
        game.playLevelMusic();
        assertNotNull(game.levelMusic);
        verify(music).setLooping(true);
        verify(music).setVolume(0.8f);
        verify(music).play();
    }

    @Test
    @DisplayName("stopLevelMusic корректно останавливает levelMusic")
    void stopLevelMusic_worksCorrectly() {
        MainGame game   = new MainGame();
        game.levelMusic = music;

        game.stopLevelMusic();
        verify(music).stop();
    }

    @Test
    @DisplayName("rewindBackgroundMusic останавливает и сбрасывает позицию")
    void rewindBackgroundMusic_works() {
        MainGame game = new MainGame();
        game.backgroundMusic = music;

        game.rewindBackgroundMusic();
        verify(music).stop();
        verify(music).setPosition(0);
    }

    @Test
    @DisplayName("playBackgroundMusic включает музыку, если не играет")
    void playBackgroundMusic_playsIfNotPlaying() {
        MainGame game = new MainGame();
        game.backgroundMusic = music;

        when(music.isPlaying()).thenReturn(false);
        game.playBackgroundMusic();
        verify(music).play();
    }

    @Test
    @DisplayName("playBackgroundMusic не трогает уже играющую музыку")
    void playBackgroundMusic_doesNothingIfPlaying() {
        MainGame game = new MainGame();
        game.backgroundMusic = music;

        when(music.isPlaying()).thenReturn(true);
        game.playBackgroundMusic();
        verify(music, never()).play();
    }

    @Test
    @DisplayName("applyResolutionSettings корректно меняет режим окна")
    void applyResolutionSettings_changesWindowMode() {
        MainGame game  = new MainGame();
        game.prefs     = prefs;
        game.gameWidth = 1920;
        game.gameHeight= 1080;

        game.applyResolutionSettings();

        verify(graphics).setWindowedMode(1920, 1080);
        verify(prefs).putBoolean("isFullscreen", false);
        verify(prefs).flush();
    }

    @Test
    @DisplayName("getDragCursor / getCustomCursor возвращают правильные объекты")
    void getCursorMethods_work() {
        MainGame game = new MainGame();
        game.customCursor = cursor;
        game.dragCursor   = dragCursor;

        assertSame(cursor,     game.getCustomCursor());
        assertSame(dragCursor, game.getDragCursor());
    }
    @Test
    @DisplayName("setGlobalVolume обновляет volume для levelMusic, если он не null")
    void setGlobalVolume_updatesLevelMusicVolume() {
        MainGame game = new MainGame();
        Music bgMusic = mock(Music.class);
        Music lvlMusic = mock(Music.class);
        Preferences prefs = mock(Preferences.class);
        game.backgroundMusic = bgMusic;
        game.levelMusic = lvlMusic;
        game.prefs = prefs;
        game.setGlobalVolume(0.7f);
        verify(bgMusic).setVolume(0.7f);
        verify(lvlMusic).setVolume(0.7f);
        verify(prefs).putFloat("globalVolume", 0.7f);
        verify(prefs).flush();
    }
    @Test
    @DisplayName("updateAllAudioVolumes обновляет volume у levelMusic, если он есть")
    void updateAllAudioVolumes_updatesLevelMusicVolume() {
        MainGame game = new MainGame();
        Music bgMusic = mock(Music.class);
        Music lvlMusic = mock(Music.class);
        game.backgroundMusic = bgMusic;
        game.levelMusic = lvlMusic;
        game.setGlobalVolume(0.6f);
        reset(bgMusic, lvlMusic);
        game.updateAllAudioVolumes();
        verify(bgMusic).setVolume(0.6f);
        verify(lvlMusic).setVolume(0.6f);
    }
    @Test
    @DisplayName("playLevelMusic не вызывает play, если levelMusic уже играет")
    void playLevelMusic_doesNotReplayIfAlreadyPlaying() {
        MainGame game = new MainGame();
        Music lvlMusic = mock(Music.class);
        game.levelMusic = lvlMusic;
        game.prefs = prefs;
        game.setGlobalVolume(0.5f);
        when(lvlMusic.isPlaying()).thenReturn(true);
        game.playLevelMusic();
        verify(lvlMusic, never()).play();
    }
    @Test
    @DisplayName("getBackgroundMusic возвращает backgroundMusic")
    void getBackgroundMusic_returnsBackgroundMusic() {
        MainGame game = new MainGame();
        Music music = mock(Music.class);
        game.backgroundMusic = music;
        assertSame(music, game.getBackgroundMusic());
    }
    @Test
    @DisplayName("setFullscreen true включает fullscreen, если размеры совпадают и не был fullscreen")
    void setFullscreen_setsFullscreenIfPossible() {
        MainGame game = new MainGame();
        Graphics graphics = mock(Graphics.class);
        Preferences prefs = mock(Preferences.class);
        Gdx.graphics = graphics;
        game.prefs = prefs;
        game.gameWidth = 1920;
        game.gameHeight = 1080;
        game.isFullscreen = false;
        Graphics.DisplayMode dm = new Graphics.DisplayMode(1920, 1080, 60, 32) {};
        when(graphics.getDisplayMode()).thenReturn(dm);
        game.setFullscreen(true);
        verify(graphics).setFullscreenMode(dm);
        verify(prefs).putBoolean("isFullscreen", true);
        verify(prefs).flush();
        assertTrue(game.isFullscreen());
    }

    @Test
    @DisplayName("setFullscreen false выключает fullscreen, если был fullscreen")
    void setFullscreen_unsetsFullscreenIfNeeded() {
        MainGame game = new MainGame();
        Graphics graphics = mock(Graphics.class);
        Preferences prefs = mock(Preferences.class);
        Gdx.graphics = graphics;
        game.prefs = prefs;
        game.gameWidth = 1920;
        game.gameHeight = 1080;
        game.isFullscreen = true;
        Graphics.DisplayMode dm = new Graphics.DisplayMode(1920, 1080, 60, 32) {};
        when(graphics.getDisplayMode()).thenReturn(dm);
        game.setFullscreen(false);
        verify(graphics).setWindowedMode(1920, 1080);
        verify(prefs).putBoolean("isFullscreen", false);
        verify(prefs).flush();
        assertFalse(game.isFullscreen());
    }

}
