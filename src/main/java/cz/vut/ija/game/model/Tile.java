/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * An abstract class for each tile to inherit from
 */
package cz.vut.ija.game.model;

import java.util.Set;
import java.util.EnumSet;

/**
 * Base class for all tiles.
 */
public abstract class Tile {
    /**
     * Current rotation of the tile in degrees (0, 90, 180, or 270).
     */
    private int rotation = 0;

    /**
     * Rotate this tile 90° clockwise.
     */
    public void rotate() {
        rotation = (rotation + 90) % 360;
    }

    /**
     * Returns the current rotation of the tile.
     *
     * @return current rotation angle in degrees
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the tile.
     *
     * @param rotation rotation angle in degrees
     */
    public void setRotation(int rotation) {
        // Normalize into [0..359]
        this.rotation = ((rotation % 360) + 360) % 360;
    }

    /**
     * Gets the type of the tile, e.g. "I","L","T","X","S","B".
     *
     * @return string identifier of the tile type
     */
    public abstract String getType();

    /**
     * Returns the set of sides (NORTH, EAST, SOUTH, WEST) that this tile
     * connects to in its base orientation (rotation = 0).
     *
     * @return set of sides where connections are possible
     */
    public abstract Set<Side> getBaseSides();

    /**
     * Computes which sides this tile connects to, taking its current
     * rotation into account.
     *
     * @return a Set of Side enums for the rotated tile
     */
    public Set<Side> getRotatedSides() {
        int steps = (getRotation() / 90) % 4;
        Set<Side> rotated = EnumSet.noneOf(Side.class);
        for (Side base : getBaseSides()) {
            Side cur = base;
            for (int i = 0; i < steps; i++) {
                switch (cur) {
                    case NORTH:
                        cur = Side.EAST;
                        break;
                    case EAST:
                        cur = Side.SOUTH;
                        break;
                    case SOUTH:
                        cur = Side.WEST;
                        break;
                    case WEST:
                        cur = Side.NORTH;
                        break;
                }
            }
            rotated.add(cur);
        }
        return rotated;
    }

    /**
     * Returns true if this tile has a connector on the given side,
     * honoring its current rotation.
     *
     * @param side the Side to check
     * @return true if rotated connections include the side
     */
    public boolean connects(Side side) {
        return getRotatedSides().contains(side);
    }
}