package com.puzzle.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class RedactorLogic {
    private String[][] currentGrid;
    private Map<String, String> mishenPositions = new HashMap<>();
    private Map<String, String> laserPositions = new HashMap<>();
    private EditorGameLogic editorLogic;
    public RedactorLogic() {
    }

    public void createGrid(int width, int height) {
        currentGrid = new String[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                currentGrid[i][j] = "Ser";
            }
        }
        editorLogic = new EditorGameLogic(currentGrid);
    }

    public String[][] getGrid() {
        return currentGrid;
    }

    public GameLogic getEditorLogic() {
        return editorLogic;
    }

    public Map<String, String> getMishenPositions() {
        return mishenPositions;
    }

    public Map<String, String> getLaserPositions() {
        return laserPositions;
    }

    public void setCell(int i, int j, String type) {
        currentGrid[i][j] = type;
        String cellKey = i + "_" + j;
        if (type.startsWith("Mishen_")) {
            String position = type.split("_")[1];
            mishenPositions.put(cellKey, position);
        } else if (type.startsWith("Laser_")) {
            String[] parts = type.split("_");
            String position = parts[1];
            String rotation = parts[2];
            laserPositions.put(cellKey, position + "_" + rotation);
        } else {
            mishenPositions.remove(cellKey);
            laserPositions.remove(cellKey);
        }
    }

    public int countLasersOnGrid() {
        int laserCount = 0;
        for (int i = 0; i < currentGrid.length; i++) {
            for (int j = 0; j < currentGrid[i].length; j++) {
                if (currentGrid[i][j].startsWith("Laser")) {
                    laserCount++;
                }
            }
        }
        return laserCount;
    }

    public void saveLevel(int levelNumber) {
        if (levelNumber >= 1 && levelNumber <= 3) {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            String levelData = json.toJson(currentGrid);
            Preferences prefs = Gdx.app.getPreferences("LevelData");
            prefs.putString("level" + levelNumber, levelData);
            prefs.flush();
            System.out.println("Level saved successfully.");
        } else {
            System.out.println("Invalid level number. Enter 1, 2, or 3.");
        }
    }

    public String getNewMishenPosition(String currentPosition) {
        if (currentPosition.equals("cc")) return "tl";
        if (currentPosition.equals("tl")) return "tn";
        if (currentPosition.equals("tn")) return "tp";
        if (currentPosition.equals("tp")) return "cp";
        if (currentPosition.equals("cp")) return "cl";
        if (currentPosition.equals("cl")) return "nl";
        if (currentPosition.equals("nl")) return "nn";
        if (currentPosition.equals("nn")) return "np";
        if (currentPosition.equals("np")) return "cc";
        return "cc";
    }

    public String getNewLaserPosition(String currentPosition) {
        if (currentPosition.equals("cc")) return "tl";
        if (currentPosition.equals("tl")) return "tn";
        if (currentPosition.equals("tn")) return "tp";
        if (currentPosition.equals("tp")) return "cp";
        if (currentPosition.equals("cp")) return "cl";
        if (currentPosition.equals("cl")) return "nl";
        if (currentPosition.equals("nl")) return "nn";
        if (currentPosition.equals("nn")) return "np";
        if (currentPosition.equals("np")) return "cc";
        return "cc";
    }

    public String getNewMishenPositionByScroll(String currentPosition, float scrollAmount) {
        String[] positions = {"cc", "tl", "tn", "tp", "cp", "cl", "nl", "nn", "np"};
        int currentIndex = -1;
        for (int i = 0; i < positions.length; i++) {
            if (positions[i].equals(currentPosition)) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) return "cc";
        int newIndex = (currentIndex + (scrollAmount > 0 ? 1 : -1)) % positions.length;
        if (newIndex < 0) newIndex += positions.length;
        return positions[newIndex];
    }

    public static class EditorGameLogic extends GameLogic {
        private String[][] grid;
        private Set<String> hitTargets = new HashSet<>();

        public EditorGameLogic(String[][] grid) {
            super(grid);
            this.grid = grid;
        }

        @Override
        public String[][] getGrid() {
            return this.grid;
        }

        @Override
        public Set<String> getHitTargets() {
            return this.hitTargets;
        }

        @Override
        public void addHitTarget(String key) {
            this.hitTargets.add(key);
        }
    }
}
