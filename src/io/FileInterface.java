package io;

import exceptions.MazeMalformedException;
import exceptions.MazeSizeMissmatchException;

import java.io.FileNotFoundException;

/**
 * Represents an interface for maze file operations.
 * This interface provides a contract for loading mazes from files and converting them into a 2D character array.
 * The loaded mazes should comply with the given specifications.
 * <p>
 * A typical maze file:
 * - Begins with the dimensions of the maze (width and height).
 * - Followed by the maze data, with characters representing walls, paths, start, and end positions.
 * </p>
 * <p>
 * Note: Implementing classes should handle various validation scenarios and throw the appropriate exceptions as detailed in the {@link #load(String)} method.
 * </p>
 */
public interface FileInterface {

    /**
     * Loads a maze from the specified filename and converts it into a 2D character array.
     * <p>
     * This method attempts to read a maze file with the following expectations:
     * - The first line should contain the maze's dimensions, separated by a space (e.g., "10 15").
     * - Subsequent lines should provide the maze data with specific characters representing the maze elements.
     * </p>
     * <p>
     * Exception Handling:
     * - Throws {@link MazeMalformedException} if the maze data doesn't match the given format.
     * - Throws {@link MazeSizeMissmatchException} if the maze data doesn't match the specified dimensions.
     * - Throws {@link IllegalArgumentException} for other general validation errors, such as invalid characters.
     * - Throws {@link FileNotFoundException} if the specified maze file is not found.
     * </p>
     *
     * @param filename The path to the maze file to be loaded.
     * @return A 2D character array representing the loaded maze.
     * @throws MazeMalformedException      If the maze data is not correctly formatted.
     * @throws MazeSizeMissmatchException  If the maze dimensions do not match the provided size.
     * @throws IllegalArgumentException     For other validation errors.
     * @throws FileNotFoundException        If the maze file is not found.
     */
    public char[][] load(String filename) throws MazeMalformedException, MazeSizeMissmatchException, IllegalArgumentException, FileNotFoundException;

}