package view;

import maze.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.EventListener;

/**
 * A view for creating and udpating the GUI.
 */
public class GUIView extends View<Color> {

    /** The title for the GUI window. */
    private static final String TITLE = "Solve the maze (%s)";
    private static final String INVALID_DIRECTION =
            "Invalid key pressed. Use either the WASD keys or the arrow keys to navigate.";

    /** The maximum height of the window. */
    private static final int MAX_HEIGHT = 600;
    /** The maximum width of the window. */
    private static final int MAX_WIDTH = 1000;

    private final JFrame frame;
    /** The panels corresponding to each maze cell. */
    private final JPanel[][] panels;
    /** The button that autosolves the maze when pressed. */
    private final JButton button;

    /**
     * Creates a new GUI view with the given maze.
     * @param maze The maze to display in this view.
     * @param filename The filename of the maze (this will be printed to the console).
     */
    public GUIView(Maze maze, String filename) {

        // Initial setup
        super(maze, filename);

        // Cell appearances
        cellAppearance.put(Cell.PATH, Colours.EMPTY);
        cellAppearance.put(Cell.WALL, Colours.WALL);
        cellAppearance.put(Cell.START, Colours.START);
        cellAppearance.put(Cell.END, Colours.END);
        appearanceView = new Color[maze.getNumRows()][maze.getNumCols()];
        maze.forEachPos(pos -> {
            appearanceView[pos.getRow()][pos.getCol()] = cellAppearance.get(maze.getCell(pos));
        });

        // Special appearances
        playerAppearance = Colours.PLAYER;
        startAppearance = Colours.START;
        visitedAppearance = Colours.VISITED;
        backtrackedAppearance = Colours.BACKTRACKED;

        // Window setup
        this.frame = new JFrame(
                TITLE.formatted(filename != null ? filename : "auto-generated maze"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension windowSize = getWindowDimensions(maze.getNumCols(), maze.getNumRows());

        // Adding panels and layout
        GridLayout gridLayout = new GridLayout(maze.getNumRows(), maze.getNumCols());
        JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(windowSize);
        mainPanel.setLayout(gridLayout);
        panels = new JPanel[maze.getNumRows()][maze.getNumCols()];
        this.maze.forEachPos(pos -> {
            JPanel panel = new JPanel();
            mainPanel.add(panel);
            panels[pos.getRow()][pos.getCol()] = panel;
        });

        // Button
        frame.add(new JPanel(), BorderLayout.SOUTH);
        button = new JButton("Autosolve");
        frame.add(button, BorderLayout.SOUTH);

        // Final window setup
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();

    }

    /**
     * Makes the window visible.
     */
    public void makeVisible() {
        frame.setVisible(true);
    }

    /**
     * A utility class for cell colours.
     */
    private static class Colours {
        private static final Color EMPTY = Color.WHITE;
        private static final Color WALL = Color.BLACK;
        private static final Color START = Color.YELLOW;
        private static final Color END = Color.GREEN;
        private static final Color PLAYER = Color.BLUE;
        private static final Color VISITED = Color.LIGHT_GRAY;
        private static final Color BACKTRACKED = Color.RED;
    }

    /**
     * Gets suitable window dimensions that preserve the aspect ratio of the maze without
     * exceeding the maximum dimensions.
     * @param numCols The number of columns in the maze.
     * @param numRows The number of rows in the maze.
     * @return The suitable dimensions for this maze.
     */
    private static Dimension getWindowDimensions(int numCols, int numRows) {
        double aspectRatio = (double) numCols / numRows;
        int possibleWidth = (int) (MAX_HEIGHT * aspectRatio);
        if (possibleWidth <= MAX_WIDTH) {
            return new Dimension(possibleWidth, MAX_HEIGHT);
        } else {
            return new Dimension(MAX_WIDTH, (int) (MAX_HEIGHT / aspectRatio));
        }
    }

    @Override
    public void updateAt(Position pos, Color appearance) {
        pos.select(panels).setBackground(appearance);
    }

    @Override
    public void congratulate() {
        showMessage(CONGRATULATIONS);
        button.setEnabled(false);
    }

    @Override
    public void warnInvalidDirection() {
        showMessage(INVALID_DIRECTION);
    }

    @Override
    public void warnWallInTheWay() {
        // In the GUI, nothing happens if you move into a wall
    }

    @Override
    public void warnUnsolveable() {
        showMessage(UNSOLVEABLE);
        button.setEnabled(false);
    }

    @Override
    public void autoSolverSucceeded() {
        showMessage(AUTOSOLVED);
        button.setEnabled(false);
    }

    public void addListener(EventListener listener) {
        frame.addKeyListener((KeyListener) listener);
        button.addKeyListener((KeyListener) listener);
        button.addActionListener((ActionListener) listener);
    }

    /**
     * Shows a message dialog.
     * @param msg The message to put in the dialog.
     */
    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
    }

}
