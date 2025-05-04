package cz.vut.ija.game.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model: 2D grid of Tile objects. Notifies observers on change.
 */
public class GameBoard {
    private final int rows, cols;
    private final Tile[][] tiles;
    private final List<BoardObserver> observers = new ArrayList<>();

    public GameBoard(int rows, int cols) {
        this.rows = rows; this.cols = cols;
        tiles = new Tile[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                tiles[r][c] = new WireTile();
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Tile getTile(int r, int c) { return tiles[r][c]; }

    public void rotateTile(int r, int c) {
        tiles[r][c].rotate();
        notifyObservers(r, c);
    }

    public void addObserver(BoardObserver o) {
        observers.add(o);
    }

    private void notifyObservers(int row, int col) {
        for (BoardObserver o : observers) o.tileChanged(row, col);
    }
}