/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Implementation of the lightbulb tile.
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/**
 * Represents a light bulb tile on the game board.
 * Light bulbs can be powered and light up when connected.
 */
public class BulbTile extends Tile {
    @Override
    public String getType() {
        return "B";
    }

    /**
     * Base connection side for the BulbTile.
     * By convention, at rotation=0 it connects downwards (SOUTH) - so the logic is the same as for the source tile.
     */
    @Override
    public Set<Side> getBaseSides() {
        return EnumSet.of(Side.SOUTH);
    }
}