package com.puzzle.Render;


import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.puzzle.Bot;
import com.puzzle.Bot.Move;
import com.puzzle.logic.GameLogic;


public class BotVisualizer {
    private final GameLogic logic;
    private final GameView view;
    private final float stepDelay;

    public BotVisualizer(GameLogic logic, GameView view, float stepDelay) {
        this.logic = logic;
        this.view = view;
        this.stepDelay = stepDelay;
    }

    public void visualize() {
        java.util.List<Move> path = new Bot(logic.getGrid()).solveWithPath();
        if (path == null || path.isEmpty()) {
            System.out.println("Бот решение не найдено или пусто");
            return;
        }
        logic.setBotButtonEnabled(false);
        for (int i = 0; i < path.size(); i++) {
            final Move m = path.get(i);
            Timer.schedule(new Task() {
                @Override
                public void run() {
                    String[][] g = logic.getGrid();
                    String tmp = g[m.blockRow][m.blockCol];
                    g[m.blockRow][m.blockCol] = g[m.serRow][m.serCol];
                    g[m.serRow][m.serCol] = tmp;
                    logic.updateGrid(g);
                    view.refreshGrid();
                    view.redrawLasers();
                }
            }, i * stepDelay);
        }

        Timer.schedule(new Task() {
            @Override
            public void run() {
                logic.setBotSolved(true);
                view.redrawLasers();
            }
        }, path.size() * stepDelay);
    }
}
