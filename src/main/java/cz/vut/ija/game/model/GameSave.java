/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A class used for saving the game. It implements the serializable interface and saves the content into a file.
 */
package cz.vut.ija.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a saved state of a game, including board configuration,
 * moves performed, and game metadata.
 * <p>
 * It contains a list of moves performed during gameplay,
 * save date, board size, bulb count, and completion status.
 */
public class GameSave implements Serializable {

    /**
     * Date when the game was saved.
     */
    private Date saveDate;
    /**
     * Board size string (e.g. "5×5").
     */
    private String boardSize;
    /**
     * Number of light bulbs on the board.
     */
    private int bulbCount;
    /**
     * Whether the game was completed successfully.
     */
    private boolean completed;

    /**
     * Initial types of all tiles.
     */
    private String[][] initialBoardTypes;
    /**
     * Initial rotations of all tiles.
     */
    private int[][] initialBoardRotations;

    /**
     * Solution rotations for all tiles.
     */
    private int[][] solutionRotations;
    /**
     * List of all moves made in the game.
     */
    private List<GameMove> moves;

    /**
     * Creates a new empty game save.
     */
    public GameSave() {
        this.moves = new ArrayList<>();
        this.saveDate = new Date();
    }

    /**
     * Represents a single move in the saved game.
     */
    public static class GameMove implements Serializable {
        /**
         * Row index of the move.
         */
        private int row;
        /**
         * Column index of the move.
         */
        private int col;
        /**
         * Original rotation before the move.
         */
        private int oldRotation;
        /**
         * New rotation after the move.
         */
        private int newRotation;

        /**
         * Creates a new game move.
         *
         * @param row         row index
         * @param col         column index
         * @param oldRotation original rotation
         * @param newRotation new rotation
         */
        public GameMove(int row, int col, int oldRotation, int newRotation) {
            this.row = row;
            this.col = col;
            this.oldRotation = oldRotation;
            this.newRotation = newRotation;
        }

        /**
         * Gets the row index.
         *
         * @return row index
         */
        public int getRow() {
            return row;
        }

        /**
         * Gets the column index.
         *
         * @return column index
         */
        public int getCol() {
            return col;
        }

        /**
         * Gets the old rotation.
         *
         * @return old rotation in degrees
         */
        public int getOldRotation() {
            return oldRotation;
        }

        /**
         * Gets the new rotation.
         *
         * @return new rotation in degrees
         */
        public int getNewRotation() {
            return newRotation;
        }
    }

    /**
     * Gets the save date.
     *
     * @return date when game was saved
     */
    public Date getSaveDate() {
        return saveDate;
    }

    /**
     * Gets the board size.
     *
     * @return board size string (e.g. "5×5")
     */
    public String getBoardSize() {
        return boardSize;
    }

    /**
     * Sets the board size.
     *
     * @param boardSize board size string
     */
    public void setBoardSize(String boardSize) {
        this.boardSize = boardSize;
    }

    /**
     * Gets the number of light bulbs.
     *
     * @return bulb count
     */
    public int getBulbCount() {
        return bulbCount;
    }

    /**
     * Sets the bulb count.
     *
     * @param bulbCount number of light bulbs
     */
    public void setBulbCount(int bulbCount) {
        this.bulbCount = bulbCount;
    }

    /**
     * Checks if the game was completed.
     *
     * @return true if completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets whether the game is completed.
     *
     * @param completed true if completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Gets the initial board tile types.
     *
     * @return 2D array of tile type strings
     */
    public String[][] getInitialBoardTypes() {
        return initialBoardTypes;
    }

    /**
     * Sets the initial board tile types.
     *
     * @param initialBoardTypes 2D array of tile type strings
     */
    public void setInitialBoardTypes(String[][] initialBoardTypes) {
        this.initialBoardTypes = initialBoardTypes;
    }

    /**
     * Gets the initial board rotations.
     *
     * @return 2D array of initial rotations
     */
    public int[][] getInitialBoardRotations() {
        return initialBoardRotations;
    }

    /**
     * Sets the initial board rotations.
     *
     * @param initialBoardRotations 2D array of initial rotations
     */
    public void setInitialBoardRotations(int[][] initialBoardRotations) {
        this.initialBoardRotations = initialBoardRotations;
    }

    /**
     * Gets the list of moves.
     *
     * @return list of moves
     */
    public List<GameMove> getMoves() {
        return moves;
    }

    /**
     * Adds a move to the list.
     *
     * @param row         row index
     * @param col         column index
     * @param oldRotation original rotation
     * @param newRotation new rotation
     */
    public void addMove(int row, int col, int oldRotation, int newRotation) {
        moves.add(new GameMove(row, col, oldRotation, newRotation));
    }

    /**
     * Gets the solution rotations.
     *
     * @return 2D array of solution rotations
     */
    public int[][] getSolutionRotations() {
        return solutionRotations;
    }

    /**
     * Sets the solution rotations.
     *
     * @param solutionRotations 2D array of solution rotations
     */
    public void setSolutionRotations(int[][] solutionRotations) {
        this.solutionRotations = solutionRotations;
    }
}
