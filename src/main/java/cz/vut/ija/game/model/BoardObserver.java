/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Model -> View callback interface.
 */
package cz.vut.ija.game.model;

/**
 * Interface for observing changes to the board.
 */

public interface BoardObserver {
    /**
     * Called when a tile changes.
     *
     * @param row row of changed tile
     * @param col column of changed tile
     */
    void tileChanged(int row, int col);
}