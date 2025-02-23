package com.puzzle.dto;

public class LevelDto {
    private int levelNumber;
    private String levelData;
    public LevelDto() {
    }

    public LevelDto(int levelNumber, String levelData) {
        this.levelNumber = levelNumber;
        this.levelData = levelData;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getLevelData() {
        return levelData;
    }

    public void setLevelData(String levelData) {
        this.levelData = levelData;
    }
}
