import java.io.FileNotFoundException;

import controller.GUIController;
import controller.TextController;
import exceptions.*;

/**
 * Launcher class that processes command-line arguments and runs the game.
 */
public class Launcher {

    /**
     * Prints an error message to {@code System.err} and exits the program.
     * @param msg The error message to print
     */
    private static void printError(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    /**
     * Runs the maze game based on the given command-line arguments.
     * @param args The command-line arguments given when the program is run. If one of the
     *             arguments is {@code GUI} then the maze will open in a GUI. If another argument
     *             is given, that will be interpreted as the filename. A maximum of two arguments
     *             should be given.
     */
    public static void main(String[] args) {

        // Too many arguments
        if (args.length > 2) {
            printError("Too many command-line arguments given.");
        }

        // Processes the arguments
        String filename = null;
        boolean gui = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("GUI") && !gui) {
                gui = true;
            } else {
                if (filename == null) {
                    filename = arg;
                } else {
                    // Filename already given
                    printError("Arguments should only be \"GUI\" or the filename, nothing else");
                }
            }
        }

        // Runs the game
        try {
            if (gui) {
                new GUIController(filename).run();
            } else {
                new TextController(filename).run();
            }
        } catch (MazeMalformedException | MazeSizeMissmatchException | IllegalArgumentException
                 | FileNotFoundException e) {
            printError(e.getMessage());
        }

    }

}
