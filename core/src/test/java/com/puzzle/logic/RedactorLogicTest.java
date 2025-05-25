package com.puzzle.logic;

import org.junit.jupiter.api.*;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class RedactorLogicTest {
    Application app;
    Preferences prefs;

    @BeforeEach
    void setUp() {
        app = mock(Application.class);
        prefs = mock(Preferences.class);
        Gdx.app = app;
        when(app.getPreferences(eq("LevelData"))).thenReturn(prefs);
    }

    @Test @DisplayName("createGrid создает правильную сетку и EditorGameLogic")
    void createGrid_createsGrid() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(2, 3);
        String[][] grid = logic.getGrid();
        assertEquals(2, grid.length);
        assertEquals(3, grid[0].length);
        for (String[] col : grid)
            for (String cell : col)
                assertEquals("Ser", cell);
        assertNotNull(logic.getEditorLogic());
    }

    @Test @DisplayName("setCell: добавление Mishen и Laser, удаление их")
    void setCell_mishen_and_laser_and_remove() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(2, 2);
        logic.setCell(0, 0, "Mishen_cc");
        assertEquals("Mishen_cc", logic.getGrid()[0][0]);
        assertTrue(logic.getMishenPositions().containsKey("0_0"));
        assertEquals("cc", logic.getMishenPositions().get("0_0"));
        logic.setCell(1, 0, "Laser_cp_123.0");
        assertEquals("Laser_cp_123.0", logic.getGrid()[1][0]);
        assertTrue(logic.getLaserPositions().containsKey("1_0"));
        assertEquals("cp_123.0", logic.getLaserPositions().get("1_0"));
        logic.setCell(0, 0, "Block");
        assertFalse(logic.getMishenPositions().containsKey("0_0"));
        logic.setCell(1, 0, "Ser");
        assertFalse(logic.getLaserPositions().containsKey("1_0"));
    }

    @Test @DisplayName("countLasersOnGrid считает количество лазеров")
    void countLasersOnGrid_counts() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(2, 2);
        logic.setCell(0, 0, "Laser_cc_0");
        logic.setCell(1, 0, "Laser_tl_123");
        logic.setCell(1, 1, "Ser");
        assertEquals(2, logic.countLasersOnGrid());
    }

    @Test @DisplayName("getNewMishenPosition: корректная смена позиций по кругу")
    void getNewMishenPosition_cycle() {
        RedactorLogic logic = new RedactorLogic();
        String pos = "cc";
        int count = 0;
        for (int i = 0; i < 9; i++) {
            pos = logic.getNewMishenPosition(pos);
            count++;
        }
        assertEquals("cc", pos);
        assertEquals(9, count);
    }

    @Test @DisplayName("getNewLaserPosition: работает как getNewMishenPosition")
    void getNewLaserPosition_cycle() {
        RedactorLogic logic = new RedactorLogic();
        String pos = "cc";
        for (int i = 0; i < 9; i++) {
            pos = logic.getNewLaserPosition(pos);
        }
        assertEquals("cc", pos);
    }

    @Test @DisplayName("getNewMishenPositionByScroll: скролл вперед и назад, круг")
    void getNewMishenPositionByScroll_allCases() {
        RedactorLogic logic = new RedactorLogic();
        String[] positions = {"cc", "tl", "tn", "tp", "cp", "cl", "nl", "nn", "np"};
        for (int i = 0; i < positions.length; i++) {
            int nextIdx = (i + 1) % positions.length;
            assertEquals(positions[nextIdx], logic.getNewMishenPositionByScroll(positions[i], 1));
        }
        for (int i = 0; i < positions.length; i++) {
            int prevIdx = (i - 1 + positions.length) % positions.length;
            assertEquals(positions[prevIdx], logic.getNewMishenPositionByScroll(positions[i], -1));
        }
        assertEquals("cc", logic.getNewMishenPositionByScroll("BAD_POS", 1));
    }

    @Test @DisplayName("EditorGameLogic get/set hitTargets + getGrid")
    void editorGameLogic_works() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(1, 1);
        RedactorLogic.EditorGameLogic editor = (RedactorLogic.EditorGameLogic) logic.getEditorLogic();
        assertArrayEquals(logic.getGrid(), editor.getGrid());
        assertTrue(editor.getHitTargets().isEmpty());
        editor.addHitTarget("0_0_mishen_cp");
        assertTrue(editor.getHitTargets().contains("0_0_mishen_cp"));
    }

    @Test
    @DisplayName("saveLevel успешно сохраняет для уровней 1, 2, 3")
    void saveLevel_savesForValidLevels() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(2, 2);
        logic.setCell(0, 0, "Mishen_cc");
        for (int level = 1; level <= 3; level++) {
            logic.saveLevel(level);
            verify(prefs).putString(startsWith("level" + level), anyString());
            verify(prefs, atLeastOnce()).flush();
        }
    }

    @Test
    @DisplayName("saveLevel не сохраняет для невалидного номера")
    void saveLevel_doesNotSaveForInvalidLevels() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(2, 2);
        int[] invalidLevels = {0, -1, 4, 999};
        for (int level : invalidLevels) {
            logic.saveLevel(level);
        }
        verify(prefs, never()).putString(anyString(), anyString());
        verify(prefs, never()).flush();
    }

    @Test
    @DisplayName("saveLevel сериализует grid в Json")
    void saveLevel_serializesGridToJson() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(1, 1);
        logic.setCell(0, 0, "Block");
        logic.saveLevel(1);
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(prefs).putString(eq("level1"), jsonCaptor.capture());
        String json = jsonCaptor.getValue();
        assertTrue(json.contains("Block"), "Grid json должен содержать Block");
        assertTrue(json.contains("[[\"Block\"]]"), "Grid json корректен");
    }
    @Test
    @DisplayName("countLasersOnGrid: считает все варианты Laser")
    void countLasersOnGrid_handlesAllLaserFormats() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(2, 1);
        logic.setCell(0, 0, "Laser_cc_0");
        logic.setCell(1, 0, "Laser");
        assertEquals(2, logic.countLasersOnGrid());
    }
    @Test
    @DisplayName("EditorGameLogic: addHitTarget игнорирует повторное добавление")
    void editorGameLogic_addHitTargetIdempotent() {
        RedactorLogic logic = new RedactorLogic();
        logic.createGrid(1, 1);
        RedactorLogic.EditorGameLogic editor = (RedactorLogic.EditorGameLogic) logic.getEditorLogic();
        editor.addHitTarget("0_0_mishen_cp");
        editor.addHitTarget("0_0_mishen_cp");
        assertEquals(1, editor.getHitTargets().size());
    }
    @Test
    @DisplayName("getNewMishenPosition возвращает cc для невалидной позиции")
    void getNewMishenPosition_invalid_returnsCc() {
        RedactorLogic logic = new RedactorLogic();
        assertEquals("cc", logic.getNewMishenPosition("UNKNOWN"));
    }

}
