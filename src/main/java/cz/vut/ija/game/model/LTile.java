/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * Implementation of the L wire tile.
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/** L-shaped connector (3 o’clock + 6 o’clock + 9 o’clock). */
public class LTile extends Tile {
    @Override public String getType() { return "L"; }

    /**
     * Base connection sides for L-shaped tile (an elbow: north + east at rotation=0).
     */
    @Override
    public Set<Side> getBaseSides() {
        return EnumSet.of(Side.NORTH, Side.EAST);
    }
}
