package cz.vut.ija.game.model;
import cz.vut.ija.game.model.WireTile;
import java.util.ArrayList;
import java.util.List;
import cz.vut.ija.game.model.Position;
import cz.vut.ija.game.model.SourceTile;

/**
 * Model: a 2D grid of Tile objects.
 * Notifies observers when tiles change.
 */
public class GameBoard {
    private final int rows, cols;
    private final Tile[][] tiles;
    private final List<BoardObserver> observers = new ArrayList<>();

    /** Public constructor: initialize every cell to a default WireTile */
    public GameBoard(int rows, int cols) {
        this.rows = rows; this.cols = cols;
        tiles = new Tile[rows][cols];
        // make sure no cell is null!
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = new WireTile();
            }
        }
    }

    /** Construct directly from a prebuilt Tile matrix */
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

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    /** Rotate and notify observers (used by controller) */
    public void rotateTile(int row, int col) {
        tiles[row][col].rotate();
        notifyObservers(row, col);
    }

    /** Set rotation arbitrarily and notify (used by generator & undo) */
    public void setTileRotation(int row, int col, int rotation) {
        tiles[row][col].setRotation(rotation);
        notifyObservers(row, col);
    }

    public void addObserver(BoardObserver o) {
        observers.add(o);
    }

    private void notifyObservers(int row, int col) {
        for (BoardObserver o : observers) {
            o.tileChanged(row, col);
        }
    }

    /**
     * Finds the position of the single SourceTile on the board.
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
     * @param p the Position to check
     * @return true if p.row in [0, rows) and p.col in [0, cols)
     */
    public boolean inBounds(Position p) {
        int r = p.getRow();
        int c = p.getCol();
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}