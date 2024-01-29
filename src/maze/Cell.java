package maze;

/**
 * A cell located at a given row/column in a {@link Maze}.
 */
public enum Cell {

    PATH, WALL, START, END;

    private Direction lastDir = null;

    /**
     * Creates a new cell from a maze file character.
     * @param fileChar The character to convert to a cell.
     * @return The cell equivalent of the given character.
     */
    public static Cell newCell(char fileChar) {
        return switch (fileChar) {
            case '#' -> WALL;
            case ' ', '.' -> PATH;
            case 'S' -> START;
            case 'E' -> END;
            default -> throw new IllegalArgumentException(
                    "Invalid maze component: %s".formatted(fileChar));
        };
    }

}
