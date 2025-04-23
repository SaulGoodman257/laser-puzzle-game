package com.puzzle.logic;

import java.util.HashSet;
import java.util.Set;

public class GameLogic {

    private String[][] grid;
    private final Set<String> hitTargets = new HashSet<>();
    private int totalTargets;
    private boolean isWin;
    private boolean botSolved;
    private boolean botUsed;
    private boolean botButtonEnabled = true;

    public GameLogic(String[][] grid) {
        this.grid = copyGrid(grid);
        countTargets();
        checkWinCondition();
    }

    private String[][] copyGrid(String[][] src) {
        String[][] dst = new String[src.length][];
        for (int i = 0; i < src.length; i++) dst[i] = src[i].clone();
        return dst;
    }

    private void countTargets() {
        for (String[] col : grid)
            for (String cell : col)
                if (cell.startsWith("Mishen")) totalTargets++;
    }

    public void updateGrid(String[][] newGrid) {
        this.grid = copyGrid(newGrid);
        hitTargets.clear();
        checkWinCondition();
    }

    public void addHitTarget(String key) {
        hitTargets.add(key);
        checkWinCondition();
    }

    public String[][] getGrid()        { return grid; }
    public Set<String> getHitTargets() { return hitTargets; }

    public boolean isWin()             { return isWin; }

    public boolean isBotSolved()       { return botSolved; }
    public void    setBotSolved(boolean v){ botSolved = v; }

    public boolean isBotUsed()         { return botUsed; }
    public void    setBotUsed(boolean v){ botUsed = v; }

    public boolean isBotButtonEnabled(){ return botButtonEnabled; }
    public void    setBotButtonEnabled(boolean v){ botButtonEnabled = v; }

    private void checkWinCondition() {
        int uniqueHits = 0;
        Set<String> cells = new HashSet<>();
        for (String t : hitTargets) {
            String[] p = t.split("_");
            if (cells.add(p[0] + "," + p[1])) uniqueHits++;
        }
        isWin = uniqueHits >= totalTargets;
        botButtonEnabled = !isWin;
    }
}
