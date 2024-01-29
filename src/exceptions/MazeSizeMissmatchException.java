package exceptions;

/**
 * Thrown when the grid in a maze file has dimensions that don't match what is specified at the
 * start of the file.
 */
public class MazeSizeMissmatchException extends Exception {

    public MazeSizeMissmatchException(String message) {
        super(message);
    }

}
