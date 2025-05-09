/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Implementation of the X shaped wire tile
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/**
 * X-shaped connector (all four sides).
 */
public class XTile extends Tile {
    @Override
    public String getType() {
        return "X";
    }

    /**
     * Base connection sides for X-shaped tile (connects all four directions).
     */
    @Override
    public Set<Side> getBaseSides() {
        return EnumSet.allOf(Side.class);
    }
}
