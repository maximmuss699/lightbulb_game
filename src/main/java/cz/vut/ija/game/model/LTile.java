/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Implementation of the L wire tile.
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/**
 * Creates a new L-shaped wire tile.
 * This tile connects two adjacent sides at a 90-degree angle.
 */
public class LTile extends Tile {
    @Override
    public String getType() {
        return "L";
    }

    /**
     * Base connection sides for L-shaped tile (an elbow: north + east at rotation=0).
     */
    @Override
    public Set<Side> getBaseSides() {
        return EnumSet.of(Side.NORTH, Side.EAST);
    }
}
