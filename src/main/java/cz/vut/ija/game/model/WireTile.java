package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;
import cz.vut.ija.game.model.Side;

/** A straight‐wire tile (“I” shape). */
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