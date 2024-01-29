package controller;

import java.io.FileNotFoundException;
import java.util.Scanner;

import exceptions.*;
import maze.Direction;
import view.TextView;
import static maze.Direction.*;

/**
 * A controller for handling the text display.
 */
public class TextController extends Controller<TextView, String> {

    private final Scanner scanner;

    /**
     * Creates a new text controller.
     * @param filename The name of the file containing the maze data.
     */
    public TextController(String filename)
            throws FileNotFoundException, MazeSizeMissmatchException, MazeMalformedException {
        super(filename);
        this.view = new TextView(this.maze, filename);
        scanner = new Scanner(System.in);
    }

    @Override
    public Direction getDirection(String input) {

        // Input should be a single letter
        if (input.length() != 1) {
            throw new IllegalArgumentException();
        }

        char key = Character.toUpperCase(input.charAt(0));
        return switch (key) {
            case 'W' -> UP;
            case 'A' -> LEFT;
            case 'S' -> DOWN;
            case 'D'  -> RIGHT;
            default -> throw new IllegalArgumentException();
        };

    }

    @Override
    public void run() {
        view.update();
        while (!mazeCompleted) {
            view.prompt();
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("solve")) {
                autoSolve();
            } else {
                userMoved(input);
            }
        }
    }

    @Override
    protected void end() {
        super.end();
        scanner.close();
    }

}
