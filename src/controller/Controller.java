package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;

import exceptions.*;
import io.FileLoader;
import maze.*;
import view.View;

/**
 * A controller for the maze game.
 * @param <V> The type of view associated with this controller:
 *            <ul>
 *                <li>{@link view.TextView} for text displays</li>
 *                <li>{@link view.GUIView} for GUIs</li>
 *            </ul>
 * @param <I> The type of user input given:
 *            <ul>
 *                <li>{@link String} for console input</li>
 *                <li>{@link KeyEvent} for key press input</li>
 *            </ul>
 */
public abstract class Controller<V extends View, I> {

    /** The size of a randomly generated maze. */
    private static int DEFAULT_SIZE = 101;

    protected final Maze maze;
    protected V view;
    /** Whether the maze has been completed (when the maze has been solved by the
     * user/autosolver, or the autosolver failed to solve the maze). */
    protected boolean mazeCompleted;

    /**
     * Creates a new controller.
     * @param filename The name of the file containing the maze data.
     */
    public Controller(String filename)
            throws FileNotFoundException, MazeSizeMissmatchException, MazeMalformedException {
        if (filename != null) {
            this.maze = new Maze(new FileLoader().load(filename));
        } else {
            this.maze = Maze.generate(DEFAULT_SIZE);
        }
        this.mazeCompleted = false;
    }

    /**
     * Runs the controller.
     */
    public abstract void run();

    /**
     * Handles user input by navigating through the maze.
     * @param input The user input (either a {@link String} from the scanner
     *              or a {@link KeyEvent} from a
     *              {@link KeyListener}).
     */
    protected void userMoved(I input) {

        // Converts the input to a direction
        Direction dir;
        try {
            dir = getDirection(input);
        } catch (IllegalArgumentException e) {
            view.warnInvalidDirection();
            return;
        }

        // Tries to move in that direction
        boolean success = false;
        try {
            maze.moveIn(dir);
            success = true;
        } catch (WallInTheWayException e) {
            view.warnWallInTheWay();
        }

        // Updates the view if the move was successful
        if (success) {
            view.update();
            if (maze.endFound()) {
                view.congratulate();
                end();
            }
        }

    }

    /**
     * Converts user input to a {@link Direction}.
     * @param input The user input (either a {@link String} from the scanner
     *              or a {@link KeyEvent}).
     * @return The {@link Direction} corresponding to the user input.
     */
    public abstract Direction getDirection(I input);

    /**
     * Attempts to autosolve the maze and alerts the user
     * if the end was found.
     */
    protected void autoSolve() {
        try {
            maze.autoSolve();
            view.update();
            view.autoSolverSucceeded();
        } catch (MazeUnsolveableException e) {
            view.update();
            view.warnUnsolveable();
        }
        end();
    }

    /**
     * Ends the maze game and does any other required cleanup.
     */
    protected void end() {
        mazeCompleted = true;
    }

}
