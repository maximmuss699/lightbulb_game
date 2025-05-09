/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Implementation of the game board.
 * Used for creating new boards and different tiles.
 */
package cz.vut.ija.game.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model: a 2D grid of Tile objects.
 * Notifies observers when tiles change.
 */
public class GameBoard {
    /**
     * Number of rows and columns in the board.
     */
    private final int rows, cols;
    /**
     * 2D array of tiles.
     */
    private final Tile[][] tiles;
    /**
     * List of observers that will be notified of changes.
     */
    private final List<BoardObserver> observers = new ArrayList<>();

    /**
     * Stores the correct rotations for auto-solve.
     */
    private int[][] solutionRotations;

    /**
     * Whether timed mode is enabled.
     */
    private boolean timedModeEnabled;
    /**
     * Time limit in seconds.
     */
    private int timeLimit;
    /**
     * Remaining time in seconds.
     */
    private int remainingTime;

    /**
     * Creates a new game board with specified dimensions.
     *
     * @param rows number of rows
     * @param cols number of columns
     */
    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        tiles = new Tile[rows][cols];
        // make sure no cell is null!
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = new WireTile();
            }
        }
    }

    /**
     * Creates a game board from an existing tile array.
     *
     * @param initial 2D array of tiles
     */
    public GameBoard(Tile[][] initial) {
        this.rows = initial.length;
        this.cols = initial[0].length;
        this.tiles = new Tile[rows][cols];
        for (int r = 0; r < rows; r++) {
            if (initial[r].length != cols)
                throw new IllegalArgumentException("Jagged initial row");
            System.arraycopy(initial[r], 0, tiles[r], 0, cols);
        }
    }

    /**
     * Sets the solution rotations for auto-solving.
     *
     * @param sol 2D array of correct rotations
     */
    public void setSolutionRotations(int[][] sol) {
        this.solutionRotations = sol;
    }

    /**
     * Gets the solution rotations.
     *
     * @return 2D array of correct rotations
     */
    public int[][] getSolutionRotations() {
        return solutionRotations;
    }

    /**
     * Gets the number of rows.
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the number of columns.
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Gets the tile at specified position.
     *
     * @param row row index
     * @param col column index
     * @return the tile at position
     */
    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    /**
     * Rotates a tile and notifies observers.
     *
     * @param row row index
     * @param col column index
     */
    public void rotateTile(int row, int col) {
        tiles[row][col].rotate();
        notifyObservers(row, col);
    }

    /**
     * Sets a tile's rotation and notifies observers.
     *
     * @param row      row index
     * @param col      column index
     * @param rotation new rotation in degrees
     */
    public void setTileRotation(int row, int col, int rotation) {
        tiles[row][col].setRotation(rotation);
        notifyObservers(row, col);
    }

    /**
     * Sets the type of a tile.
     *
     * @param row  row index
     * @param col  column index
     * @param type tile type identifier
     */
    public void setTileType(int row, int col, String type) {
        int originalRotation = tiles[row][col].getRotation();

        Tile newTile;
        switch (type) {
            case "S":
                newTile = new SourceTile();
                break;
            case "B":
                newTile = new BulbTile();
                break;
            case "I":
                newTile = new WireTile();
                break;
            case "L":
                newTile = new LTile();
                break;
            case "T":
                newTile = new TTile();
                break;
            case "X":
                newTile = new XTile();
                break;
            default:
                newTile = new WireTile();
                break;
        }

        newTile.setRotation(originalRotation);
        tiles[row][col] = newTile;
        notifyObservers(row, col);
    }

    /**
     * Adds an observer to be notified of changes.
     *
     * @param o observer to add
     */
    public void addObserver(BoardObserver o) {
        observers.add(o);
    }

    /**
     * Notifies all observers of a change to a tile.
     *
     * @param row row of changed tile
     * @param col column of changed tile
     */
    private void notifyObservers(int row, int col) {
        for (BoardObserver o : observers) {
            o.tileChanged(row, col);
        }
    }

    /**
     * Finds the position of the single SourceTile on the board.
     *
     * @return the Position of the source, or null if none found
     */
    public Position findSource() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tiles[r][c] instanceof SourceTile) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    /**
     * Checks if the given position lies within the board boundaries.
     *
     * @param p the Position to check
     * @return true if p.row in [0, rows) and p.col in [0, cols)
     */
    public boolean inBounds(Position p) {
        int r = p.getRow();
        int c = p.getCol();
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /**
     * Gets the number of rotations needed to solve a tile.
     *
     * @param row row index
     * @param col column index
     * @return number of 90-degree rotations needed
     */
    public int getRequiredClicks(int row, int col) {
        if (solutionRotations == null) return 0;
        int target = solutionRotations[row][col];
        int current = tiles[row][col].getRotation();
        int diff = (target - current + 360) % 360;
        return diff / 90;
    }


    /**
     * Checks if timed mode is enabled for this game.
     *
     * @return true if timed mode is enabled
     */
    public boolean isTimedModeEnabled() {
        return timedModeEnabled;
    }

    /**
     * Sets whether timed mode is enabled.
     *
     * @param timedModeEnabled true to enable timed mode
     */
    public void setTimedModeEnabled(boolean timedModeEnabled) {
        this.timedModeEnabled = timedModeEnabled;
    }

    /**
     * Gets the time limit in seconds.
     *
     * @return time limit in seconds
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the time limit in seconds.
     *
     * @param timeLimit time limit in seconds
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
        this.remainingTime = timeLimit;
    }

    /**
     * Gets the remaining time in seconds.
     *
     * @return remaining time in seconds
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Sets the remaining time in seconds.
     *
     * @param remainingTime remaining time in seconds
     */
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

}