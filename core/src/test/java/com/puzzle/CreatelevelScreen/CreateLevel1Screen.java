package com.puzzle.CreatelevelScreen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.puzzle.MainGame;
import com.puzzle.Render.GameView;
import com.puzzle.logic.GameLogic;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CreateLevel1ScreenTest {

    private final Application app = mock(Application.class);
    private final Files       files = mock(Files.class);
    private final Audio       audio = mock(Audio.class);
    private final Graphics    graphics = mock(Graphics.class);
    private final Input       input = mock(Input.class);
    private MockedConstruction<OrthographicCamera> camCons;
    private MockedConstruction<Stage>             stageCons;
    private MockedConstruction<Texture>           texCons;
    private MockedConstruction<Image>             imgCons;
    private MockedConstruction<TextButton>        btnCons;
    private MockedConstruction<BitmapFont>        fontCons;
    private MockedConstruction<GameLogic>         logicCons;
    private MockedConstruction<GameView>          viewCons;
    private MockedConstruction<Json>              jsonCons;
    private Preferences prefs;
    private MainGame    game;

    @BeforeEach
    void setUp() throws Exception {

        Gdx.app      = app;
        Gdx.files    = files;
        Gdx.audio    = audio;
        Gdx.graphics = graphics;
        Gdx.input    = input;

        when(graphics.getWidth()).thenReturn(1920);
        when(graphics.getHeight()).thenReturn(1080);

        FileHandle fh = mock(FileHandle.class, RETURNS_DEEP_STUBS);
        when(fh.nameWithoutExtension()).thenReturn("dummy");
        when(files.internal(anyString())).thenReturn(fh);
        when(audio.newSound(any())).thenReturn(mock(Sound.class));
        camCons = Mockito.mockConstruction(
            OrthographicCamera.class,
            (mock, ctx) -> {
                doNothing().when(mock).setToOrtho(anyBoolean(), anyFloat(), anyFloat());
            });

        stageCons = Mockito.mockConstruction(
            Stage.class,
            (mock, ctx) -> { doNothing().when(mock).dispose();
                when(mock.getActors()).thenReturn(new Array<>());
            });

        texCons   = Mockito.mockConstruction(Texture.class);
        imgCons   = Mockito.mockConstruction(Image.class);
        btnCons   = Mockito.mockConstruction(TextButton.class);
        fontCons  = Mockito.mockConstruction(BitmapFont.class);
        logicCons = Mockito.mockConstruction(GameLogic.class);
        viewCons  = Mockito.mockConstruction(GameView.class,
            (mock, ctx) -> doNothing().when(mock).dispose());
        jsonCons  = Mockito.mockConstruction(Json.class);

        game = mock(MainGame.class, RETURNS_DEEP_STUBS);
        doNothing().when(game).playLevelMusic();
        when(game.getGlobalVolume()).thenReturn(1f);
        prefs = mock(Preferences.class);
        when(app.getPreferences(eq("LevelData"))).thenReturn(prefs);
    }

    @AfterEach
    void tearDown() {
        camCons.close();  stageCons.close();  texCons.close();  imgCons.close();
        btnCons.close();  fontCons.close();  logicCons.close(); viewCons.close();
        jsonCons.close();
    }

    @Test
    void loadLevelData_whenPrefsNull_usesDefaultGrid() {
        when(prefs.getString(eq("level1"), isNull())).thenReturn(null);
        CreateLevel1Screen screen = new CreateLevel1Screen(game);
        assertNotNull(screen);
        jsonCons.constructed().forEach(j ->
            verify(j, never()).fromJson(any(), anyString()));
        assertFalse(logicCons.constructed().isEmpty());
    }

    @Test
    void loadLevelData_whenPrefsJson_usesParsedGrid() {
        String json = "[[\"A\",\"B\"],[null,\"C\"]]";
        when(prefs.getString(eq("level1"), isNull())).thenReturn(json);
        jsonCons.close();
        jsonCons = Mockito.mockConstruction(Json.class,
            (mock, ctx) -> when(mock.fromJson(eq(String[][].class), eq(json)))
                .thenReturn(new String[][]{{"A","B"},{null,"C"}}));
        CreateLevel1Screen screen = new CreateLevel1Screen(game);
        assertNotNull(screen);
        verify(jsonCons.constructed().get(0))
            .fromJson(String[][].class, json);
        assertFalse(logicCons.constructed().isEmpty());
    }

    @Test
    void constructor_createsCoreComponents() {
        when(prefs.getString(eq("level1"), isNull())).thenReturn(null);
        CreateLevel1Screen screen = new CreateLevel1Screen(game);
        assertNotNull(screen);
        assertFalse(camCons.constructed().isEmpty());
        assertFalse(stageCons.constructed().isEmpty());
        assertFalse(texCons.constructed().isEmpty());
        assertFalse(fontCons.constructed().isEmpty());
        assertFalse(btnCons.constructed().isEmpty());
        verify(game).playLevelMusic();
    }
}
