package exceptions;

/**
 * Thrown when a maze file has an incorrectly formed maze
 * (e.g. invalid walls or invalid start/end points).
 */
public class MazeMalformedException extends Exception {

    public MazeMalformedException(String message) {
        super(message);
    }

}
