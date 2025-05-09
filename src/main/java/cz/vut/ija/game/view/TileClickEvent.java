/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * An event that fires when the user clicks on a tile.
 */
package cz.vut.ija.game.view;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Custom event: user clicked tile at (row,col).
 */
public class TileClickEvent extends Event {
    public static final EventType<TileClickEvent> TILE_CLICK =
            new EventType<>(Event.ANY, "TILE_CLICK");

    private final int row, col;

    public TileClickEvent(int row, int col) {
        super(TILE_CLICK);
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}