package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/** A light bulb tile: connects in one direction to receive power. */
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