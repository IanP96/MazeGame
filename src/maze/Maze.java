package maze;

import java.util.*;
import java.util.function.Consumer;

import exceptions.MazeUnsolveableException;
import exceptions.WallInTheWayException;

/**
 * A maze containing walls, traversable routes and start/end points.
 */
public class Maze {

    private final Cell[][] cells;
    private Position userPos;
    private Position startPos;
    private final int numRows;
    private final int numCols;
    /** Positions that the user/autosolver has visited (excludes backtracked positions). */
    private final ArrayList<Position> visitedPositions;
    /** Positions that the user/autosolver has visited,
     * where they ended up going back the way they came. */
    private final ArrayList<Position> backtrackedPositions;

    /**
     * Creates a new maze from a two-dimensional character array.
     * @param mazeText The two-dimensional array of characters.
     * @require mazeText contains valid characters and dimensions
     * (as checked in {@link io.FileLoader}).
     */
    public Maze(char[][] mazeText) {

        // Setting instance variables
        numRows = mazeText.length;
        numCols = mazeText[0].length;
        visitedPositions = new ArrayList<>();
        backtrackedPositions = new ArrayList<>();

        // Populates the array of cells and sets the user position
        cells = new Cell[numRows][numCols];
        forEachPos(pos -> {
            Cell cell = Cell.newCell(mazeText[pos.getRow()][pos.getCol()]);
            cells[pos.getRow()][pos.getCol()] = cell;
            if (cell.equals(Cell.START)) {
                startPos = new Position(pos);
            }
        });

        // Final setup
        initialise();

    }

    /**
     * Generates a random maze with the given size.
     * @param size the size of the maze.
     * @return The newly generated maze.
     */
    public static Maze generate(int size) {
        return new Maze(MazeGenerator.generate(size));
    }

    /**
     * Moves the user back to the start and resets the visited/backtracked positions.
     */
    private void initialise() {
        visitedPositions.clear();
        backtrackedPositions.clear();
        userPos = startPos;
        visitedPositions.add(startPos);
    }

    /**
     * Gets the cell at a given position.
     * @param pos The position that the cell is found at.
     * @return The cell at the given position.
     */
    public Cell getCell(Position pos) {
        return pos.select(cells);
    }

    /**
     * Checks if the user is located at the given position.
     * @param pos The position to check.
     * @return Whether the position given is the same as the user position.
     */
    public boolean isUserPos(Position pos) {
        return userPos.equals(pos);
    }

    /**
     * Checks if the start is located at the given position.
     * @param pos The position to check.
     * @return Whether the position given is the same as the start position.
     */
    public boolean isStartPos(Position pos) {
        return startPos.equals(pos);
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    /**
     * Checks if the given position has been visited by the user (excluding backtracked routes).
     * @param pos The position to check.
     * @return Whether the user has visited the given position.
     */
    public boolean wasVisited(Position pos) {
        return visitedPositions.contains(pos);
    }

    /**
     * Checks if the given position has been backtracked by the user.
     * @param pos The position to check.
     * @return Whether the user has backtracked through the given position.
     */
    public boolean wasBacktracked(Position pos) {
        return backtrackedPositions.contains(pos);
    }

    /**
     * Attemps to move the user in a given direction.
     * @param dir The direction that the user wants to move in.
     * @throws WallInTheWayException When there is a wall blocking the user's movement.
     */
    public void moveIn(Direction dir) throws WallInTheWayException {
        Position newPos = userPos.movedIn(dir);
        if (getCell(newPos) == Cell.WALL) {
            throw new WallInTheWayException();
        }
        if (wasVisited(newPos)) {
            // If the user has already visited newPos,
            // all positions visited since then should become backtracked
            int index = visitedPositions.lastIndexOf(newPos) + 1;
            List<Position> positionsVisitedSince =
                    List.copyOf(visitedPositions.subList(index, visitedPositions.size()));
            // Copying (using List.copyOf) is needed to avoid concurrent modification exceptions
            visitedPositions.removeAll(positionsVisitedSince);
            backtrackedPositions.addAll(positionsVisitedSince);
        } else {
            if (wasBacktracked(newPos)) {
                backtrackedPositions.remove(newPos);
            }
            visitedPositions.add(newPos);
        }
        userPos = newPos;
    }

    /**
     * Checks whether the user can move in a given direction (i.e. there isn't a wall there).
     * @param dir The direction the user could move in.
     * @return Whether the user can move in the given direction.
     */
    public boolean canMove(Direction dir) {
        return getCell(userPos.movedIn(dir)) != Cell.WALL;
    }

    /**
     * Find whether the end of the maze has been reached.
     * @return Whether the end of the maze has been reached.
     */
    public boolean endFound() {
        return getCell(userPos) == Cell.END;
    }

    public void forEachPos(Consumer<Position> action) {
        for (int row = 0; row < getNumRows(); row++) {
            for (int col = 0; col < getNumCols(); col++) {
                action.accept(new Position(row, col));
            }
        }
    }

    /**
     * Solves the maze programmatically.
     * @throws MazeUnsolveableException When the maze can't be solved programmatically.
     */
    public void autoSolve() throws MazeUnsolveableException {

        // Goes back to the start
        initialise();

        while (!endFound()) {

            // Sees which directions you can go in without going the way you came
            Direction possibleDirection = null;
            for (Direction dir : Direction.ALL_DIRECTIONS) {
                if (canMove(dir) && !wasVisited(userPos.movedIn(dir))
                        && !wasBacktracked(userPos.movedIn(dir))) {
                    possibleDirection = dir;
                    break;
                }
            }

            // A suitable direction was found
            if (possibleDirection != null) {
                try {
                    moveIn(possibleDirection);
                } catch (WallInTheWayException ignored) {
                    // We have ensured that the user can move in this direction
                    // so this exception won't be raised
                }

            // No suitable direction was found
            } else {

                // If the solver has returned to the start and all adjacent cells have been
                // backtracked, then the autosolver has gone in every possible direction to no
                // avail, hence the maze is unsolveable
                if (userPos.equals(startPos)) {
                    throw new MazeUnsolveableException();
                }

                // Retraces its steps (goes back to where it was before)
                Position lastPos = visitedPositions.get(visitedPositions.size() - 2);
                try {
                    moveIn(Direction.directionTo(userPos, lastPos));
                } catch (WallInTheWayException ignored) {
                    // We have ensured that the user can move in this direction
                    // so this exception won't be raised
                }

            }

        }

    }

}
