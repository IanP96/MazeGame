package maze;

import exceptions.MazeUnsolveableException;
import exceptions.WallInTheWayException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MazeTest {

    private Maze maze1;
    private Maze maze2;

    /** Maze data with the end points at their expected locations (corners of the maze). */
    private static char[][] data1 = {
            {'#', '#', '#', '#', '#', '#', '#'},
            {'#', 'S', '#', ' ', ' ', ' ', '#'},
            {'#', ' ', '#', '#', '#', ' ', '#'},
            {'#', ' ', '#', ' ', ' ', ' ', '#'},
            {'#', ' ', '#', ' ', '#', ' ', '#'},
            {'#', ' ', ' ', ' ', '#', 'E', '#'},
            {'#', '#', '#', '#', '#', '#', '#'}
    };
    /** Maze data with the end points at abnormal (but still valid) locations. */
    private static char[][] data2 = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#'},
            {'#', ' ', 'S', ' ', '#', '#', '#', ' ', '#', ' ', '#'},
            {'#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#'},
            {'#', ' ', ' ', ' ', '#', ' ', ' ', ' ', 'E', ' ', '#'},
            {'#', '#', '#', '#', '#', ' ', ' ', ' ', ' ', ' ', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };

    @Before
    public void setUp() throws Exception {
        maze1 = new Maze(data1);
        maze2 = new Maze(data2);
    }

    @After
    public void tearDown() throws Exception {
    }

    private static void ensureSolveable(Maze maze) {
        boolean solved;
        try {
            maze.autoSolve();
            solved = true;
        } catch (MazeUnsolveableException e) {
            solved = false;
        }
        assertTrue("Maze should be solveable", solved);
    }

    /**
     * Ensures maze end points are initialised properly (they are at the right locations).
     */
    @Test
    public void endPoints() {
        assertTrue("Maze 1 has the wrong start point",
                maze1.isStartPos(new Position(1, 1)));
        assertTrue("Maze 2 has the wrong start point",
                maze2.isStartPos(new Position(2, 2)));
        assertEquals("Maze 1 has the wrong end point",
                Cell.END, maze1.getCell(new Position(5, 5)));
        assertEquals("Maze 2 has the wrong end point",
                Cell.END, maze2.getCell(new Position(4, 8)));
    }

    /**
     * Ensures a randomly-generated maze is solveable.
     */
    @Test
    public void generatedMazeIsSolveable() {
        ensureSolveable(Maze.generate(11));  // 11 is just an arbitrary size
    }

    /**
     * Ensures that solveable mazes can be solved.
     */
    @Test
    public void mazesAreSolveable() {
        ensureSolveable(maze1);
        ensureSolveable(maze2);
    }

    /**
     * Ensures an unsolveable maze can be recognised.
     */
    @Test
    public void mazeUnsolveable() {
         char[][] unsolveableData = {
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
                {'#', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#'},
                {'#', ' ', 'S', ' ', '#', '#', '#', ' ', '#', ' ', '#'},
                {'#', ' ', ' ', ' ', ' ', ' ', ' ', '#', '#', '#', '#'},
                {'#', ' ', ' ', ' ', '#', ' ', ' ', '#', 'E', ' ', '#'},
                {'#', '#', '#', '#', '#', ' ', ' ', '#', ' ', ' ', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
        };
        Maze maze = new Maze(unsolveableData);
        assertThrows("Maze should be unsolveable", MazeUnsolveableException.class,
                maze::autoSolve);
    }

    /**
     * Ensures the maze can identify when it has been solved.
     */
    @Test
    public void identifiesWhenSolved() throws MazeUnsolveableException {
        maze1.autoSolve();
        assertTrue("The end of maze 1 should have been found", maze1.endFound());
    }

    /**
     * Ensures moving updates the user's position.
     */
    @Test
    public void canMove() throws WallInTheWayException {
        maze1.moveIn(Direction.DOWN);
        assertTrue("User position should be updated after moving down",
                maze1.isUserPos(new Position(2, 1)));
    }

    /**
     * Ensures that moving into a wall throws an exception.
     */
    @Test
    public void identifiesWall() {
        assertThrows("Moving into a wall should throw an exception",
                WallInTheWayException.class, () -> maze1.moveIn(Direction.RIGHT));
    }

    /**
     * Ensures that walls and paths are located at the correct positions.
     */
    @Test
    public void locateWallsAndPaths() {
        assertEquals("Maze 1 should have a wall at this position", Cell.WALL,
                maze1.getCell(new Position(0, 0)));
        assertEquals("Maze 2 should have a wall at this position", Cell.WALL,
                maze1.getCell(new Position(2, 4)));
        assertEquals("Maze 1 should have a path at this position", Cell.PATH,
                maze1.getCell(new Position(2, 1)));
        assertEquals("Maze 2 should have a path at this position", Cell.PATH,
                maze1.getCell(new Position(3, 3)));
    }

    /**
     * Ensures the maze can recognise visited and backtracked positions.
     */
    @Test
    public void visitedAndBacktrackedPositions() throws WallInTheWayException {
        maze1.moveIn(Direction.DOWN);
        Position pos = new Position(2, 1);
        assertTrue("Positions should be visited once the user moves to that position",
                maze1.wasVisited(pos));
        maze1.moveIn(Direction.UP);
        assertTrue("Positions should become backtracked when the user retraces their steps",
                maze1.wasBacktracked(pos));
    }

    /**
     * Ensures the maze's dimensions match the maze data given.
     */
    @Test
    public void correctDimensions() {
        assertEquals("Maze 2 should have 7 rows", 7, maze2.getNumRows());
        assertEquals("Maze 2 should have 11 columns", 11, maze2.getNumCols());
    }

}
