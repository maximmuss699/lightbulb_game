/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A manager class for game seaves. Used for creating saves and adding new moves to the file.
 */
package cz.vut.ija.game.service;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.GameSave;

/**
 * Manages the saving and recording of moves for a game. This class facilitates
 * recording moves, saving the game state, and retrieving the current game save.
 */
public class GameSaveManager {
    /**
     * The game board being managed.
     */
    private final GameBoard board;
    /**
     * Service for saving and loading games.
     */
    private final GameSaveService saveService;
    /**
     * Size of the board as string (e.g. "5×5").
     */
    private final String boardSize;
    /**
     * Number of light bulbs on the board.
     */
    private final int bulbCount;
    /**
     * Current game save instance.
     */
    private GameSave currentSave;
    /**
     * Track if new moves were made since last save.
     */
    private boolean newMovesMade = false;

    /**
     * Create a new game save manager.
     *
     * @param board     board to save
     * @param boardSize size of the board
     * @param bulbCount number of bulbs on the board
     */
    public GameSaveManager(GameBoard board, String boardSize, int bulbCount) {
        this.board = board;
        this.boardSize = boardSize;
        this.bulbCount = bulbCount;
        this.saveService = new GameSaveService();
        this.currentSave = saveService.createSaveFromBoard(board, boardSize, bulbCount);
    }

    /**
     * Saves the current move.
     *
     * @param row         row
     * @param col         column
     * @param oldRotation old rotation
     * @param newRotation new rotation
     */
    public void recordMove(int row, int col, int oldRotation, int newRotation) {
        currentSave.addMove(row, col, oldRotation, newRotation);
        newMovesMade = true;
    }

    /**
     * Saves the current game state.
     *
     * @param completed was the game finished?
     */
    public void saveGame(boolean completed) {
        if (newMovesMade) {
            saveService.saveGame(currentSave, completed);
            newMovesMade = false;
        }
    }

    /**
     * Gets the current save object.
     *
     * @return current game save
     */
    public GameSave getCurrentSave() {
        return currentSave;
    }
}