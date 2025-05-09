/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * Model -> View callback interface.
 */
package cz.vut.ija.game.model;

/** MVC “Model → View” callback interface. */
public interface BoardObserver {
    void tileChanged(int row, int col);
}