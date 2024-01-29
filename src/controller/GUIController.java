package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import static java.awt.event.KeyEvent.*;

import exceptions.*;
import maze.Direction;
import view.GUIView;
import static maze.Direction.*;

/**
 * A controller for GUI handling.
 */
public class GUIController extends Controller<GUIView, KeyEvent>
        implements KeyListener, ActionListener {

    /**
     * Creates a new GUI controller.
     * @param filename The name of the file containing the maze data.
     */
    public GUIController(String filename)
            throws FileNotFoundException, MazeSizeMissmatchException, MazeMalformedException {
        super(filename);
        this.view = new GUIView(this.maze, filename);
    }

    @Override
    public Direction getDirection(KeyEvent input) {
        return switch (input.getKeyCode()) {
            case VK_UP, VK_W -> UP;
            case VK_DOWN, VK_S -> DOWN;
            case VK_LEFT, VK_A -> LEFT;
            case VK_RIGHT, VK_D -> RIGHT;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void run() {
        view.addListener(this);
        view.update();
        view.makeVisible();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (!mazeCompleted) {
            userMoved(event);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        autoSolve();
    }

}
