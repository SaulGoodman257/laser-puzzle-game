package com.puzzle;

import com.badlogic.gdx.scenes.scene2d.Stage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Bot {
    private String[][] grid;
    private int rows;
    private int cols;
    public Bot(String[][] grid) {
        this.grid = copyGrid(grid);
        this.rows = grid.length;
        this.cols = grid[0].length;
    }
    private String[][] copyGrid(String[][] grid) {
        String[][] newGrid = new String[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], grid[i].length);
        }
        return newGrid;
    }
    public String[][] getSolvedGrid() {
        Queue<String[][]> queue = new LinkedList<>();
        queue.add(this.grid);
        while (!queue.isEmpty()) {
            String[][] currentGrid = queue.poll();
            if (isSolved(currentGrid)) {
                return currentGrid;
            }
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (currentGrid[i][j].equals("Block")) {
                        for (int newI = 0; newI < rows; newI++) {
                            for (int newJ = 0; newJ < cols; newJ++) {
                                if (currentGrid[newI][newJ].equals("Ser")) {
                                    String[][] newGrid = copyGrid(currentGrid);
                                    String temp = newGrid[i][j];
                                    newGrid[i][j] = newGrid[newI][newJ];
                                    newGrid[newI][newJ] = temp;
                                    queue.add(newGrid);
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
    private boolean isSolved(String[][] grid) {
        Game tempGame = new Game(grid, new Stage(), new MainGame());
        tempGame.drawLaserLines();
        tempGame.checkWinCondition();

        return tempGame.isWin();
    }
}
