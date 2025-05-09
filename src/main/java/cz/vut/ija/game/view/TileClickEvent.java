/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * An event that fires when the user clicks on a tile.
 */
package cz.vut.ija.game.view;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Custom event: user clicked tile at (row,col).
 */
public class TileClickEvent extends Event {
    /**
     * Event type for tile click events.
     */
    public static final EventType<TileClickEvent> TILE_CLICK =
            new EventType<>(Event.ANY, "TILE_CLICK");

    /**
     * Row and column of the clicked tile.
     */
    private final int row, col;

    /**
     * Creates a new tile click event for the given position.
     *
     * @param row row index of the clicked tile
     * @param col column index of the clicked tile
     */
    public TileClickEvent(int row, int col) {
        super(TILE_CLICK);
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row index of the clicked tile.
     *
     * @return row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index of the clicked tile.
     *
     * @return column index
     */
    public int getCol() {
        return col;
    }
}