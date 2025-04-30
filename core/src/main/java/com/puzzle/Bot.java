package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.puzzle.Render.LaserTracer;
import com.puzzle.logic.GameLogic;

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
    public static class Move {
        public int blockRow;
        public int blockCol;
        public int serRow;
        public int serCol;
        Move(int blockRow, int blockCol, int serRow, int serCol) {
            this.blockRow = blockRow;
            this.blockCol = blockCol;
            this.serRow = serRow;
            this.serCol = serCol;
        }
    }
    private static class Node {
        String[][] grid;
        List<Move> moves;
        Node(String[][] grid, List<Move> moves) {
            this.grid = grid;
            this.moves = moves;
        }
    }
    public List<Move> solveWithPath() {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(copyGrid(this.grid), new ArrayList<>()));
        visitedStates.clear();
        visitedStates.add(gridToString(this.grid));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (isSolved(node.grid)) {
                return node.moves;
            }
            for (Move move : findPossibleMoves(node.grid)) {
                String[][] newGrid = applyMove(node.grid, move);
                String key = gridToString(newGrid);
                if (!visitedStates.contains(key)) {
                    visitedStates.add(key);
                    List<Move> newMoves = new ArrayList<>(node.moves);
                    newMoves.add(move);
                    queue.add(new Node(newGrid, newMoves));
                }
            }
        }
        return null;
    }
    private String[][] applyMove(String[][] grid, Move move) {
        String[][] newGrid = copyGrid(grid);
        String temp = newGrid[move.blockRow][move.blockCol];
        newGrid[move.blockRow][move.blockCol] = newGrid[move.serRow][move.serCol];
        newGrid[move.serRow][move.serCol] = temp;
        return newGrid;
    }
    private boolean isSolved(String[][] g) {
        GameLogic logic = new GameLogic(g);
        final float CELL = 85f, GAP = 15f;
        float startX = (Gdx.graphics.getWidth()  - (g.length        * CELL) - ((g.length        - 1) * GAP)) / 2f;
        float startY = (Gdx.graphics.getHeight() - (g[0].length     * CELL) - ((g[0].length     - 1) * GAP)) / 2f;
        Stage dummyStage          = new Stage();
        Map<String, Actor> actors = new HashMap<>();
        LaserTracer tracer = new LaserTracer(logic, dummyStage, actors, CELL, GAP, startX, startY);
        tracer.draw();
        boolean solved = logic.isWin();
        tracer.dispose();
        dummyStage.dispose();
        return solved;
    }
}
