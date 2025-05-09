/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Main class for handeling the logic between view and model.
 */
package cz.vut.ija.game.controller;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.service.GameSaveManager;
import cz.vut.ija.game.view.BoardView;
import cz.vut.ija.game.view.GameTimeUpEvent;
import cz.vut.ija.game.view.TileClickEvent;
import cz.vut.ija.game.command.Command;
import cz.vut.ija.game.command.RotateCommand;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Controller that glues the GameBoard (model) and BoardView (view) together.
 */
public class GameController {
    /**
     * The game board model.
     */
    private final GameBoard model;
    /**
     * The game board view.
     */
    private final BoardView view;

    /**
     * Stack for storing commands that can be undone.
     */
    private final Deque<Command> undoStack = new ArrayDeque<>();
    /**
     * Stack for storing commands that can be redone.
     */
    private final Deque<Command> redoStack = new ArrayDeque<>();
    /**
     * Counter for tracking number of moves made.
     */
    private int moveCount = 0;

    /**
     * Manager for saving game state.
     */
    private GameSaveManager saveManager;

    /**
     * Listener that receives time updates.
     */
    private TimeUpdateListener timeUpdateListener;

    /**
     * Timer for timed game mode.
     */
    private Timeline timer;

    /**
     * Listener that receives move count updates.
     */
    public interface MoveListener {
        /**
         * Called when move count changes.
         *
         * @param newCount the new move count
         */
        void onMove(int newCount);
    }

    /**
     * Listener for receiving time updates
     */
    public interface TimeUpdateListener {
        /**
         * Called when time is updated.
         *
         * @param remainingTime the remaining time in seconds
         */
        void onTimeUpdate(int remainingTime);
    }

    /**
     * Listener that receives move count updates.
     */
    private MoveListener moveListener;

    /**
     * Constructs a controller for a board of the given size.
     *
     * @param rows number of rows
     * @param cols number of columns
     */
    public GameController(int rows, int cols) {
        this(new GameBoard(rows, cols), false, 0);
    }

    /**
     * Constructs a controller for an existing GameBoard.
     *
     * @param board            existing game board
     * @param timedModeEnabled whether timed mode is enabled
     * @param timeLimit        time limit in seconds
     */
    public GameController(GameBoard board, boolean timedModeEnabled, int timeLimit) {
        // Initialize model and view
        this.model = board;
        this.view = new BoardView(model, false);
        this.view.setController(this);

        board.setTimedModeEnabled(timedModeEnabled);
        board.setTimeLimit(timeLimit);

        if (timedModeEnabled) {
            setupTimer();
        }

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
     * Creates a new timer using TimeLine
     */
    private void setupTimer() {
        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    int time = model.getRemainingTime();
                    time--;
                    model.setRemainingTime(time);

                    if (timeUpdateListener != null) {
                        timeUpdateListener.onTimeUpdate(time);
                    }

                    if (time <= 0) {
                        timer.stop();
                        handleTimeUp();
                    }
                })
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }


    /**
     * When time is up, pop up a new alert and save the game.
     */
    private void handleTimeUp() {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("TIME UP");
            alert.setHeaderText(null);
            alert.setContentText("TIME UP! Game will be saved and available for replay without timer.");

            alert.setOnHidden(dialogEvent -> {
                if (saveManager != null) {
                    saveManager.saveGame(false);
                }

                view.fireEvent(new GameTimeUpEvent());
            });

            alert.showAndWait();
        });

    }

    /**
     * Stops the timer.
     */
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Gets the view managed by this controller.
     *
     * @return the board view
     */
    public BoardView getView() {
        return view;
    }

    /**
     * Registers a listener to receive move count updates.
     *
     * @param listener the move listener
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

    /**
     * Sets the time update listener.
     *
     * @param listener the listener to receive time updates
     */
    public void setTimeUpdateListener(TimeUpdateListener listener) {
        this.timeUpdateListener = listener;
    }

    /**
     * Exposes the current move count.
     *
     * @return current move count
     */
    public int getMoveCount() {
        return moveCount;
    }

}