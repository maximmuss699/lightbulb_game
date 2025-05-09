/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
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
    private final GameBoard model;
    private final BoardView view;

    // History stacks for undo/redo
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    private int moveCount = 0;

    // for saving game state
    private GameSaveManager saveManager;

    private TimeUpdateListener timeUpdateListener;

    // for timed mode
    private Timeline timer;

    /**
     * Listener for move count updates.
     */
    public interface MoveListener {
        void onMove(int newCount);
    }

    /**
     * Listener for receiving time updates
     */
    public interface TimeUpdateListener {
        void onTimeUpdate(int remainingTime);
    }


    private MoveListener moveListener;

    /**
     * Constructs a controller for a board of the given size.
     */
    public GameController(int rows, int cols) {
        this(new GameBoard(rows, cols), false, 0);
    }

    /**
     * Constructs a controller for an existing GameBoard.
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

    public void setTimeUpdateListener(TimeUpdateListener listener) {
        this.timeUpdateListener = listener;
    }

    /**
     * Exposes the current move count.
     * @return current move count
     */
    public int getMoveCount() {
        return moveCount;
    }

}