package com.puzzle;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.*;

public class Bot {
    private String[][] grid;
    private int rows;
    private int cols;
    private Set<String> visitedStates;
    public Bot(String[][] grid) {
        this.grid = copyGrid(grid);
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.visitedStates = new HashSet<>();
    }
    private String[][] copyGrid(String[][] grid) {
        String[][] newGrid = new String[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], grid[i].length);
        }
        return newGrid;
    }
    private String gridToString(String[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : grid) {
            sb.append(Arrays.toString(row));
        }
        return sb.toString();
    }
    public String[][] getSolvedGrid() {
        Queue<String[][]> queue = new LinkedList<>();
        queue.add(this.grid);
        visitedStates.add(gridToString(this.grid));
        while (!queue.isEmpty()) {
            String[][] currentGrid = queue.poll();
            if (isSolved(currentGrid)) {
                return currentGrid;
            }
            List<Move> possibleMoves = findPossibleMoves(currentGrid);
            for (Move move : possibleMoves) {
                String[][] newGrid = applyMove(currentGrid, move);
                String newGridString = gridToString(newGrid);
                if (!visitedStates.contains(newGridString)) {
                    queue.add(newGrid);
                    visitedStates.add(newGridString);
                }
            }
        }
        return null;
    }
    private List<Move> findPossibleMoves(String[][] grid) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j].equals("Block")) {
                    for (int newI = 0; newI < rows; newI++) {
                        for (int newJ = 0; newJ < cols; newJ++) {
                            if (grid[newI][newJ].equals("Ser")) {
                                moves.add(new Move(i, j, newI, newJ));
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
    private static class Move {
        int blockRow, blockCol, serRow, serCol;
        Move(int blockRow, int blockCol, int serRow, int serCol) {
            this.blockRow = blockRow;
            this.blockCol = blockCol;
            this.serRow = serRow;
            this.serCol = serCol;
        }
    }
    private String[][] applyMove(String[][] grid, Move move) {
        String[][] newGrid = copyGrid(grid);
        String temp = newGrid[move.blockRow][move.blockCol];
        newGrid[move.blockRow][move.blockCol] = newGrid[move.serRow][move.serCol];
        newGrid[move.serRow][move.serCol] = temp;
        return newGrid;
    }
    private boolean isSolved(String[][] grid) {
        Game tempGame = new Game(grid, new Stage(), new MainGame());
        tempGame.drawLaserLines();
        tempGame.checkWinCondition();
        return tempGame.isWin();
    }
}
