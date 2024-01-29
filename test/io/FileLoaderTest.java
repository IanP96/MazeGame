package io;

import exceptions.MazeMalformedException;
import exceptions.MazeSizeMissmatchException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileLoaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Checks that the maze data from a file matches the expected data.
     * @param filename The filename to load.
     * @param expected The character array that should be returned by loading the given file.
     */
    private void checkMazeData(String filename, char[][] expected) throws FileNotFoundException,
            MazeSizeMissmatchException, MazeMalformedException {
        char[][] mazeData = new FileLoader().load(filename);
        assertArrayEquals(
                "Array returned by loading %s doesn't match expected array".formatted(filename),
                expected, mazeData);
    }

    private char[][] checkNoErrors(String filename) {
        boolean valid = true;
        char[][] mazeData = null;
        try {
            mazeData = new FileLoader().load(filename);
        } catch (FileNotFoundException | MazeSizeMissmatchException | MazeMalformedException
                 | IllegalArgumentException e) {
            valid = false;
        }
        assertTrue("%s should load without errors".formatted(filename), valid);
        return mazeData;
    }

    private void checkValid(String filename, char[][] expected) throws FileNotFoundException,
            MazeSizeMissmatchException, MazeMalformedException {
        checkNoErrors(filename);
        checkMazeData(filename, expected);
    }

    private <T extends Exception> void testErrorThrown(String filename, Class<T> exception) {
        assertThrows("Loading %s should throw %s".formatted(filename, exception.getSimpleName()),
                exception, () -> {
                    char[][] mazeData = new FileLoader().load(filename);
                });
    }

    /**
     * Checks that maze001.txt, maze002.txt and maze003.txt load without errors.
     */
    @Test
    public void loadValidInitialMazes() {
        for (int i = 0; i < 3; i++) {
            checkNoErrors("txt/maze00%s.txt".formatted(i + 1));
        }
    }

    /**
     * Checks that the characters of maze001.txt are valid.
     */
    @Test
    public void checkArray() throws FileNotFoundException, MazeSizeMissmatchException,
            MazeMalformedException {
        char[][] expected = {
                {'#', '#', '#', '#', '#', '#', '#'}, {'#', 'S', '#', ' ', ' ', ' ', '#'},
                {'#', ' ', '#', '#', '#', ' ', '#'}, {'#', ' ', '#', ' ', ' ', ' ', '#'},
                {'#', ' ', '#', ' ', '#', ' ', '#'}, {'#', ' ', ' ', ' ', '#', 'E', '#'},
                {'#', '#', '#', '#', '#', '#', '#'}
        };
        checkMazeData("txt/maze001.txt", expected);
    }

    /**
     * Ensures that end points can be recognised as valid without being at the corners of the maze.
     */
    @Test
    public void abnormalEndPoints() throws FileNotFoundException, MazeSizeMissmatchException,
            MazeMalformedException {
        char[][] expected = {
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
                {'#', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#'},
                {'#', ' ', 'S', ' ', '#', '#', '#', ' ', '#', ' ', '#'},
                {'#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#'},
                {'#', ' ', ' ', ' ', '#', ' ', ' ', ' ', 'E', ' ', '#'},
                {'#', '#', '#', '#', '#', ' ', ' ', ' ', ' ', ' ', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
        };
        checkValid("txt/abnormal_end_points.txt", expected);
    }


    /**
     * Ensures that a {@link FileNotFoundException} can be thrown when needed.
     */
    @Test
    public void fileNotFound() {
        testErrorThrown("abc/def.invalid", FileNotFoundException.class);
    }

    /**
     * Ensures that the loader recognises an empty maze file and throws an exception.
     */
    @Test
    public void fileEmpty() {
        testErrorThrown("txt/empty.txt", IllegalArgumentException.class);
    }

    /**
     * Ensures the loader can recognise maze dimensions that aren't integers.
     */
    @Test
    public void nonNumericDimensions() {
        testErrorThrown("txt/non_numeric_dimensions.txt", IllegalArgumentException.class);
    }

    /**
     * Ensures the loader can recognise dimensions that are even and/or negative.
     */
    @Test
    public void invalidDimensions() {
        testErrorThrown("txt/invalid_dimensions.txt", IllegalArgumentException.class);
    }

    /**
     * Ensures the loader can recognise when the given dimensions at the top of a file don't
     * match the actual dimensions of the maze grid.
     */
    @Test
    public void incorrectDimensions() {
        testErrorThrown("txt/incorrect_dimensions.txt", MazeSizeMissmatchException.class);
    }

    /**
     * Ensures the loader can recognise when the width of each maze row isn't consistent.
     */
    @Test
    public void inconsistentDimensions() {
        testErrorThrown("txt/inconsistent_dimensions.txt", MazeSizeMissmatchException.class);
    }

    /**
     * Ensures the loader can recognise invalid characters in a maze.
     */
    @Test
    public void invalidChar() {
        testErrorThrown("txt/invalid_dimensions.txt", IllegalArgumentException.class);
    }

    @Test
    public void invalidWall() {
        testErrorThrown("txt/invalid_wall.txt", MazeMalformedException.class);
    }

    /**
     * Ensures the loader can recognise when there are multiple start points in a maze.
     */
    @Test
    public void multipleStarts() {
        testErrorThrown("txt/multiple_starts.txt", MazeMalformedException.class);
    }

    /**
     * Ensures the loader can recognise when there are missing start points in a maze.
     */
    @Test
    public void noStarts() {
        testErrorThrown("txt/no_starts.txt", MazeMalformedException.class);
    }

}
