package cz.vut.ija.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a saved state of a game, including board configuration,
 * moves performed, and game metadata.
 *
 * It contains a list of moves performed during gameplay,
 * save date, board size, bulb count, and completion status.
 */
public class GameSave implements Serializable {
    
    // data for game identification
    private Date saveDate;
    private String boardSize;
    private int bulbCount;
    private boolean completed;

    // initial board configuration
    private String[][] initialBoardTypes;  
    private int[][] initialBoardRotations;

    private List<GameMove> moves;

    public GameSave() {
        this.moves = new ArrayList<>();
        this.saveDate = new Date();
    }

    // A class representing a single move performed during gameplay.
    public static class GameMove implements Serializable {
        private int row;
        private int col;
        private int oldRotation;
        private int newRotation;

        public GameMove(int row, int col, int oldRotation, int newRotation) {
            this.row = row;
            this.col = col;
            this.oldRotation = oldRotation;
            this.newRotation = newRotation;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getOldRotation() {
            return oldRotation;
        }

        public int getNewRotation() {
            return newRotation;
        }
    }

    // Getters and setters
    public Date getSaveDate() {
        return saveDate;
    }

    public String getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(String boardSize) {
        this.boardSize = boardSize;
    }

    public int getBulbCount() {
        return bulbCount;
    }

    public void setBulbCount(int bulbCount) {
        this.bulbCount = bulbCount;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String[][] getInitialBoardTypes() {
        return initialBoardTypes;
    }

    public void setInitialBoardTypes(String[][] initialBoardTypes) {
        this.initialBoardTypes = initialBoardTypes;
    }

    public int[][] getInitialBoardRotations() {
        return initialBoardRotations;
    }

    public void setInitialBoardRotations(int[][] initialBoardRotations) {
        this.initialBoardRotations = initialBoardRotations;
    }

    public List<GameMove> getMoves() {
        return moves;
    }

    // add a move to the list of moves performed during gameplay
    public void addMove(int row, int col, int oldRotation, int newRotation) {
        moves.add(new GameMove(row, col, oldRotation, newRotation));
    }

}
