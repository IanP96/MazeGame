package view;

import maze.*;
import static view.Ansi.*;

/**
 * A view for displaying the maze to the console.
 */
public class TextView extends View<String> {

    private static final String CONGRATULATIONS_FORMATTED = colour(CONGRATULATIONS, GREEN);
    private static final String USER_PROMPT =
            "Enter a direction to move %s or type %s to autosolve: "
            .formatted(colour("(W, A, S or D)", BLUE), colour("solve", BLUE));
    private static final String INVALID_DIRECTION =
            colour("Invalid direction, please try again.", RED);
    private static final String WALL_ERROR =
            colour("Could not move in the given direction as there was a wall.", RED);

    /**
     * A utility class for maze emoji.
     */
    private static class Emoji {
        private static final String EMPTY = "⬜️";
        private static final String WALL = "⬛";
        private static final String START = "🏁";
        private static final String END = "🏠";
        private static final String PLAYER = "👤";
        private static final String TRACKED = "🟦";
        private static final String BACKTRACKED = "🟥";
    }

    /**
     * Creates a new text view with the given maze.
     * @param maze The maze to display in this view.
     * @param filename The filename of the maze (this will be printed to the console).
     */
    public TextView(Maze maze, String filename) {

        super(maze, filename);

        // Cell appearances
        cellAppearance.put(Cell.PATH, Emoji.EMPTY);
        cellAppearance.put(Cell.WALL, Emoji.WALL);
        cellAppearance.put(Cell.START, Emoji.START);
        cellAppearance.put(Cell.END, Emoji.END);
        appearanceView = new String[maze.getNumRows()][maze.getNumCols()];
        maze.forEachPos(pos -> {
            appearanceView[pos.getRow()][pos.getCol()] = cellAppearance.get(maze.getCell(pos));
        });

        // Special appearances
        playerAppearance = Emoji.PLAYER;
        startAppearance = Emoji.START;
        visitedAppearance = Emoji.TRACKED;
        backtrackedAppearance = Emoji.BACKTRACKED;

    }

    @Override
    public void updateAt(Position pos, String appearance) {
        System.out.print(appearance);
        if (pos.getCol() + 1 == maze.getNumCols()) {
            // Goes to a new line when the end of a row has been reached
            System.out.println();
        }
    }

    @Override
    public void congratulate() {
        System.out.println(CONGRATULATIONS_FORMATTED);
    }

    @Override
    public void warnInvalidDirection() {
        System.out.println(INVALID_DIRECTION);
    }

    @Override
    public void warnWallInTheWay() {
        System.out.println(WALL_ERROR);
    }

    @Override
    public void warnUnsolveable() {
        System.out.println(colour(UNSOLVEABLE, RED));
    }

    @Override
    public void autoSolverSucceeded() {
        System.out.println(colour(AUTOSOLVED, BLUE));
    }

    /**
     * Prompts the user to enter a direction.
     */
    public void prompt() {
        System.out.print(USER_PROMPT);
    }

}
