/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * Implementation of the T shaped wire tile
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/** T-shaped connector (all but one side). */
public class TTile extends Tile {
    @Override
    public String getType() {
        return "T";
    }

    /**
     * Base connection sides for T-shaped tile (three sides: east, south, west).
     * At rotation = 0, connects East, South, and West (hole on North).
     */
    @Override
    public Set<Side> getBaseSides() {
        return EnumSet.of(Side.EAST, Side.SOUTH, Side.WEST);
    }
}
