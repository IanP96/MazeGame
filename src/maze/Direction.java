package maze;

import java.util.function.Consumer;

/**
 * A direction in which the user can move inside a maze.
 */
public enum Direction {

    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowChange;
    private final int colChange;

    public static final Direction[] ALL_DIRECTIONS = {DOWN, RIGHT, UP, LEFT};

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }

    Direction(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    private static Direction create(int rowChange, int colChange) throws IllegalArgumentException {
        return switch (rowChange) {
            case 0 -> switch (colChange) {
                case 1 -> RIGHT;
                case -1 -> LEFT;
                default -> throw new IllegalArgumentException();
            };
            case 1 -> {
                if (colChange != 0) {
                    throw new IllegalArgumentException();
                }
                yield DOWN;
            }
            case -1 -> {
                if (colChange != 0) {
                    throw new IllegalArgumentException();
                }
                yield UP;
            }
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Gets the direction that {@code end} is located in with respect to {@code start}.
     * @param start The start position.
     * @param end The end position.
     * @return The direction that {@code end} is located in with respect to {@code start}.
     * @throws IllegalArgumentException When the positions aren't adjacent to each other.
     */
    public static Direction directionTo(Position start, Position end)
            throws IllegalArgumentException {
        int rowChange = end.getRow() - start.getRow();
        int colChange = end.getCol() - start.getCol();
        return Direction.create(rowChange, colChange);
    }

    /**
     * Finds whether this direction is opposite to another direction
     * (up is opposite to down, left is opposite to right).
     * @param other The other direction to check.
     * @return Whether this direction and the other direction are opposite to each other.
     */
    public boolean oppositeTo(Direction other) {
        return switch (this) {
            case UP -> other == DOWN;
            case DOWN -> other == UP;
            case LEFT -> other == RIGHT;
            case RIGHT -> other == LEFT;
        };
    }

}
