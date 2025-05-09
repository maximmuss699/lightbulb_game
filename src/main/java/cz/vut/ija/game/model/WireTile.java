/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Implementation of the I shaped wire tile
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/**
 * A straight‐wire tile (“I” shape).
 */
public class WireTile extends Tile {
    @Override
    public String getType() {
        return "I";
    }

    /**
     * Base connection sides for I-shaped tile (straight line: north + south at rotation=0).
     */
    @Override
    public Set<Side> getBaseSides() {
        return EnumSet.of(Side.NORTH, Side.SOUTH);
    }
}