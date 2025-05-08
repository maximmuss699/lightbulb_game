package cz.vut.ija.game.controller;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.service.GameSaveManager;
import cz.vut.ija.game.view.BoardView;
import cz.vut.ija.game.view.TileClickEvent;
import cz.vut.ija.game.command.Command;
import cz.vut.ija.game.command.RotateCommand;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Controller that glues the GameBoard (model) and BoardView (view) together.
 */
public class GameController {
    private final GameBoard model;
    private final BoardView view;

    // History stacks for undo/redo
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    private int moveCount = 0;

    // for saving game state
    private GameSaveManager saveManager;

    /**
     * Listener for move count updates.
     */
    public interface MoveListener {
        void onMove(int newCount);
    }

    private MoveListener moveListener;

    /**
     * Constructs a controller for a board of the given size.
     */
    public GameController(int rows, int cols) {
        this(new GameBoard(rows, cols));
    }

    /**
     * Constructs a controller for an existing GameBoard.
     */
    public GameController(GameBoard board) {
        // Initialize model and view
        this.model = board;
        this.view = new BoardView(model);
        // Wire up event handling
        registerHandlers();
    }

    /**
     * Registers click handlers to execute and record commands.
     */
    private void registerHandlers() {
        view.addEventHandler(TileClickEvent.TILE_CLICK, evt -> {
            int row = evt.getRow();
            int col = evt.getCol();
            int oldRotation = model.getTile(row, col).getRotation();

            // Create, execute, and record a RotateCommand
            Command cmd = new RotateCommand(model, evt.getRow(), evt.getCol());
            cmd.execute();
            undoStack.push(cmd);
            redoStack.clear();

            // Update move count and notify listener
            moveCount++;
            if (moveListener != null) {
                moveListener.onMove(moveCount);
            }

            int newRotation = model.getTile(row, col).getRotation();
            if (saveManager != null) {
                saveManager.recordMove(row, col, oldRotation, newRotation);
            }
        });
    }

    /**
     * @return the view managed by this controller
     */
    public BoardView getView() {
        return view;
    }

    /**
     * Registers a listener to receive move count updates.
     */
    public void setOnMoveUpdated(MoveListener listener) {
        this.moveListener = listener;
    }

    /**
     * Undoes the last move, if available.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
            moveCount--;
            if (moveListener != null) {
                moveListener.onMove(moveCount);
            }
        }
    }

    /**
     * Redoes the last undone move, if available.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
            moveCount++;
            if (moveListener != null) {
                moveListener.onMove(moveCount);
            }
        }
    }

    /**
     * Sets the save manager responsible for handling game state saving and move recording.
     *
     * @param saveManager the instance of GameSaveManager to be used for managing game saves
     */
    public void setSaveManager(GameSaveManager saveManager) {
        this.saveManager = saveManager;
    }

}