package maze;

/**
 * A position in a maze specified by row/column numbers.
 */
public class Position implements Cloneable {

    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * A copy constructor for the Position class.
     * @param pos The position to copy.
     */
    public Position(Position pos) {
        this.row = pos.row;
        this.col = pos.col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Position otherPos) {
            return this.getRow() == otherPos.getRow() && this.getCol() == otherPos.getCol();
        } else {
            return false;
        }
    }

    /**
     * Gets a new position given by moving this position in a given direction.
     * @param dir The direction to move in.
     * @return The result of moving this position in the given direction.
     */
    public Position movedIn(Direction dir) {
        Position newPos = clone();
        newPos.row += dir.getRowChange();
        newPos.col += dir.getColChange();
        return newPos;
    }

    @Override
    public String toString() {
        return "(%s, %s)".formatted(row, col);
    }

    /**
     * Gets the item at a given position in a 2D array.
     * @param grid The 2D array to get the item from.
     * @return The item at a given position in a 2D array.
     */
    public <E> E select(E[][] grid) {
        return grid[row][col];
    }

    @Override
    public Position clone() {
        return new Position(this);
    }

}
