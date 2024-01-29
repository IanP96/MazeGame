package io;

import java.io.FileNotFoundException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;

import exceptions.*;

/**
 * A utility class for reading maze data from a file.
 */
public class FileLoader implements FileInterface {

    /** The minimum dimension of the maze. */
    private static final int MIN_DIMENSION = 5;
    /** The maximum dimension of the maze. */
    private static final int MAX_DIMENSION = 999;

    /**
     * Generates an error message for an error relating to a file.
     * @param msg The body of the message.
     * @param filename The name of the file where the error was caused.
     * @return An error message including the message body and filename.
     */
    private static String errMsg(String msg, String filename) {
        return "%s (filename: %s)".formatted(msg, filename);
    }

    /**
     * Checks if the given character corresponds to a valid maze cell
     * ({@code '#'}, {@code 'S'}, {@code 'E'}, {@code '.'} or {@code ' '}).
     * @param testChar The character to validate.
     * @return Whether the given character corresponds to a valid maze cell.
     */
    public static boolean validChar(char testChar) {
        for (char validChar : new char[]{' ', '.', '#', 'S', 'E'}) {
            if (validChar == testChar) {
                return true;
            }
        }
        return false;
    }

    @Override
    public char[][] load(String filename) throws MazeMalformedException, MazeSizeMissmatchException,
            IllegalArgumentException, FileNotFoundException {

        // Processing file lines
        List<String> lines;
        try {
            lines = File.readLines(filename);
        } catch (FileNotFoundException e) {
            // Rethrowing so that the error message can be included
            throw new FileNotFoundException(FileLoader.errMsg("File could not be found", filename));
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException(errMsg("File is empty", filename));
        }

        // Get dimensions
        int[] dimensions = getDimensions(lines.get(0), filename);
        int height = dimensions[0];
        int width = dimensions[1];

        // Check maze height
        if (lines.size() - 1 != height) {
            throw new MazeSizeMissmatchException(
                    errMsg("Number of maze rows doesn't match specified height", filename));
        }

        // Extract maze characters and check for valid dimensions/characters
        char[][] mazeData = new char[height][width];
        boolean atEdge;
        boolean startFound = false;
        boolean endFound = false;
        for (int row = 0; row < height; row++) {

            String line = lines.get(row + 1);
            if (line.length() != width) {
                throw new MazeSizeMissmatchException(
                        errMsg("Maze contains a row that doesn't match the given width", filename));
            }

            // Checks each cell
            for (int col = 0; col < width; col++) {
                char cell = line.charAt(col);
                if (!validChar(cell)) {
                    throw new IllegalArgumentException(errMsg(
                            "Invalid maze character found: '%s'".formatted(cell), filename));
                }

                // Ensures edges only contain wall (#) characters
                atEdge = row == 0 || row == height - 1 || col == 0 || col == width - 1;
                if (atEdge && cell != '#') {
                    throw new MazeMalformedException(errMsg(
                            "Invalid character at edge of maze", filename));
                }

                // Ensures there aren't too many start/end points
                if (cell == 'S') {
                    if (startFound) {
                        throw new MazeMalformedException(errMsg(
                                "Multiple start points found", filename));
                    }
                    startFound = true;
                }
                if (cell == 'E') {
                    if (endFound) {
                        throw new MazeMalformedException(errMsg(
                                "Multiple end points found", filename));
                    }
                    endFound = true;
                }

                mazeData[row][col] = cell;
            }

        }

        // Ensures start/end points were found
        if (!startFound || !endFound) {
            throw new MazeMalformedException(errMsg("Missing start and/or end point", filename));
        }

        return mazeData;

    }

    /**
     * Extracts maze dimensions from the first line of a file, while also checking for valid
     * formatting and suitable dimensions (e.g. checking that dimensions aren't even or
     * negative).
     * @param line The first line of the file.
     * @param filename The name of the file (needed for error messages).
     * @return An array containing the height and width of the maze (in that order).
     */
    private int[] getDimensions(String line, String filename) {

        // Splits the line
        String[] tokens = line.split(" ");
        if (tokens.length != 2 || tokens[0].isEmpty() || tokens[1].isEmpty()) {
            throw new IllegalArgumentException(errMsg(
                    "First line must contain dimensions separated by a single space", filename));
        }

        // Extracts the height and width
        int height;
        int width;
        try {
            height = Integer.parseInt(tokens[0]);
            width = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    errMsg("Dimensions must be valid numbers", filename));
        }

        // Ensures dimensions are in the valid range and odd
        for (int dimension : new int[]{height, width}) {
            if (dimension < MIN_DIMENSION || dimension > MAX_DIMENSION) {
                throw new IllegalArgumentException(
                        errMsg("Height and width must be between %s and %s (inclusive)"
                                .formatted(MIN_DIMENSION, MAX_DIMENSION), filename));
            }
            if (dimension % 2 != 1) {
                throw new IllegalArgumentException(errMsg("Dimensions must be odd", filename));
            }
        }

        return new int[]{height, width};

    }

}
