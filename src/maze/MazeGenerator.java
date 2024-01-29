package maze;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * A helper class for generating random mazes.
 */
class MazeGenerator {

    private static final char WALL = '#';
    private static final char SPACE = ' ';
    /** The character that forms paths originating from the start of the maze. */
    private static final char MAIN_CHAR = 'a';
    /** The character that forms paths originating from wall chunks within the maze. */
    private static final char FILLER_CHAR = 'b';

    /** The width/height of the inner cells of the maze (excluding the outer walls). */
    private final int size;
    /** The inner cells of the maze. */
    private final char[][] cells;
    private static final Position startPos = new Position(0, 0);
    private final Position endPos;
    /** Whether the maze is solveable. Initially this is {@code false} and is set to
     *  {@code true} once a path is constructed from the start to the end of the maze. */
    private boolean solveable;
    private final Random random;

    /**
     * Creates a new MazeGenerator.
     * @param size The width/height of the maze to generate.
     */
    private MazeGenerator(int size) {
        this.random = new Random();
        this.solveable = false;
        this.size = size - 2;
        this.endPos = new Position(this.size - 1, this.size - 1);
        if (size < 5 || size % 2 != 1) {
            throw new IllegalArgumentException("Invalid size");
        }
        this.cells = new char[this.size][this.size];
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.cells[i][j] = WALL;
            }
        }
    }

    /**
     * Makes a random path starting at {@code pathStart} and ending when either another path or a
     * dead end has been reached, without looping back on itself.
     *
     * @param pathStart The position to start the path at.
     * @param pathCell  The character that will be used to represent this path.
     */
    private void make_path(Position pathStart, char pathCell) {

        Position pos = pathStart.clone();
        setCell(pathStart, pathCell);
        while (true) {

            // Finds which directions the path can go to next
            HashMap<Direction, Boolean> possibleDirs = new HashMap<>();
            for (Direction testDir : Direction.ALL_DIRECTIONS) {
                Position testPos = pos.movedIn(testDir).movedIn(testDir);
                char testCell = getCell(testPos);
                boolean end = (testCell == MAIN_CHAR || testCell == FILLER_CHAR)
                        && testCell != pathCell;
                if (testCell == WALL || end) {
                    possibleDirs.put(testDir, end);
                }
            }

            if (!possibleDirs.isEmpty()) {

                // Path continues in one of the possible directions
                int index = random.nextInt(possibleDirs.size());
                Map.Entry<Direction, Boolean> entry =
                        possibleDirs.entrySet().stream().toList().get(index);
                Direction chosenDir = entry.getKey();
                boolean end = entry.getValue();
                Position trackedPos = null;
                for (int i = 0; i < 2; i++) {
                    // Continues the path two cells in the given direction and puts the
                    // appropriate characters there
                    if (i == 0) {
                        trackedPos = pos.movedIn(chosenDir);
                    } else {
                        trackedPos = pos.movedIn(chosenDir).movedIn(chosenDir);
                    }
                    setCell(trackedPos, pathCell);
                }
                if (trackedPos.equals(endPos)) {
                    solveable = true;
                }
                if (end) {
                    // This path has reached another previously formed path so it should end
                    return;
                }
                pos = trackedPos;

            } else {
                return;
            }

        }
    }

    /**
     * Populates the inner cells of the maze with random paths.
     */
    private void populate() {

        setCell(startPos, MAIN_CHAR);

        // Routes from start
        while (!solveable) {
            Position[] starts = getNodesOfCell(MAIN_CHAR);
            for (Position chosenNode : starts) {
                make_path(chosenNode, MAIN_CHAR);
                if (solveable) {
                    break;
                }
            }
        }

        // Removing wall chunks
        Position[] walls = getNodesOfCell(WALL);
        while (walls.length != 0) {
            int index = random.nextInt(walls.length);
            this.make_path(walls[index], FILLER_CHAR);
            walls = getNodesOfCell(WALL);
        }

        // Replaces the path characters with spaces
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Position pos = new Position(i, j);
                if (getCell(pos) == MAIN_CHAR || getCell(pos) == FILLER_CHAR) {
                    setCell(pos, SPACE);
                }
            }
        }

        setCell(startPos, 'S');
        setCell(endPos, 'E');

    }

    /**
     * Gets the nodes of this maze (all even-valued coordinates within the inner cells).
     * @return The nodes of this maze (all even-valued coordinates within the inner cells).
     */
    private Position[] getNodes() {
        ArrayList<Position> nodes = new ArrayList<>();
        int max = (size + 1) / 2;
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                nodes.add(new Position(i * 2, j * 2));
            }
        }
        return nodes.toArray(new Position[0]);
    }

    /**
     * Gets all nodes that contain the given cell.
     * @param cell The cell to check for.
     * @return All nodes that contain the given cell.
     */
    private Position[] getNodesOfCell(char cell) {
        return Arrays.stream(getNodes()).filter(node -> (getCell(node) == cell))
                .toList().toArray(new Position[0]);
    }

    /**
     * Changes one of the inner cells at a given position.
     * @param pos The position where the cells should be edited.
     * @param newChar The new cell character.
     */
    private void setCell(Position pos, char newChar) {
        cells[pos.getRow()][pos.getCol()] = newChar;
    }

    /**
     * Gets the cell at a given position.
     * @param pos The position to check.
     * @return The cell at the given position or {@code '!'} if the position is out of the bounds
     * of the maze.
     */
    private char getCell(Position pos) {
        int[] coords = {pos.getRow(), pos.getCol()};
        for (int coord : coords) {
            if (coord < 0 || coord >= cells.length) {
                return '!';
            }
        }
        return cells[pos.getRow()][pos.getCol()];
    }

    /**
     * Surrounds the maze with walls ({@code '#'}) and returns the result.
     * @return The maze surrounded by walls.
     */
    private char[][] surround() {
        char[][] finalCells = new char[size + 2][size + 2];
        for (int i = 0; i < size + 2; i++) {
            for (int j = 0; j < size + 2; j++) {
                char cell;
                if (i == 0 || i == size + 1 || j == 0 || j == size + 1) {
                    cell = WALL;
                } else {
                    cell = cells[i - 1][j - 1];
                }
                finalCells[i][j] = cell;
            }
        }
        return finalCells;
    }

    /**
     * Generates a random maze with the given size.
     *
     * @param size the size of the maze.
     * @return The 2D array of characters for the newly generated maze.
     */
    public static char[][] generate(int size) {
        MazeGenerator k = new MazeGenerator(size);
        k.populate();
        return k.surround();
    }

}
