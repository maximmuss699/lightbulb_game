/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * Implementation of the powernode tile
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/** The single power source tile (type "S"). */
public class SourceTile extends Tile {
    @Override
    public String getType() {
        return "S";
    }

    /**
     * Base connection side for the SourceTile.
     * By convention, the source outputs downward (SOUTH) at rotation=0.
     */
    @Override
    public Set<Side> getBaseSides() {
        return EnumSet.of(Side.SOUTH);
    }
}