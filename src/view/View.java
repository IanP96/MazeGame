package view;

import maze.Cell;
import maze.Maze;
import maze.Position;

import java.awt.*;
import java.util.EnumMap;

import static view.Ansi.BLUE;
import static view.Ansi.colour;

/**
 * A view for the maze game.
 * @param <T> How each cell appears in this view ({@link String} for a {@link TextView}
 *           or {@link Color} for a {@link GUIView}).
 */
public abstract class View<T> {

    protected static final String CONGRATULATIONS =
            "Congratulations, you reached the end of the maze!";
    protected static final String UNSOLVEABLE = "The autosolver failed to solve this maze.";
    protected static final String AUTOSOLVED =
            "The autosolver successfully found the end of the maze.";
    protected Maze maze;
    private static final String START_MSG = colour("%nNow viewing: %s%n%n", BLUE);
    /** The appearance of each cell type in this view. */
    protected final EnumMap<Cell, T> cellAppearance;
    /** The appearance of this view at each row and column. */
    protected T[][] appearanceView;
    /** How a cell should appear when the player is at that cell. */
    protected T playerAppearance;
    /** How the start cell should appear. */
    protected T startAppearance;
    /** How any visited cells should appear. */
    protected T visitedAppearance;
    /** How any backtracked cells should appear. */
    protected T backtrackedAppearance;

    /**
     * Creates a new view.
     *
     * @param maze The maze that this view will display.
     * @param filename The filename of the maze (this will be printed to the console).
     */
    public View(Maze maze, String filename) {
        this.maze = maze;
        this.cellAppearance = new EnumMap<>(Cell.class);
        System.out.println(
                START_MSG.formatted(filename == null ? "auto-generated maze" : filename));
    }

    /**
     * Updates the view.
     */
    public void update() {
        maze.forEachPos(pos -> {
            T appearance;
            if (maze.isUserPos(pos)) {
                appearance = playerAppearance;
            } else if (maze.isStartPos(pos)) {
                appearance = startAppearance;
            } else if (maze.wasVisited(pos)) {
                appearance = visitedAppearance;
            } else if (maze.wasBacktracked(pos)) {
                appearance = backtrackedAppearance;
            } else {
                appearance = pos.select(appearanceView);
            }
            updateAt(pos, appearance);
        });
    }

    /**
     * Updates the view at a given position.
     * @param pos The position to update.
     * @param appearance How the view should appear at that position.
     */
    public abstract void updateAt(Position pos, T appearance);

    /**
     * Congratulates the player for finding the end of the maze.
     */
    public abstract void congratulate();

    /**
     * Alerts the user that their input couldn't be converted to a valid direction.
     */
    public abstract void warnInvalidDirection();

    /**
     * Alerts that there's a wall at the cell the player is trying to move to.
     */
    public abstract void warnWallInTheWay();

    /**
     * Alerts that the autosolver could not solve the maze.
     */
    public abstract void warnUnsolveable();

    /**
     * Informs the user that the autosolver successfully found the end of the maze.
     */
    public abstract void autoSolverSucceeded();

}
