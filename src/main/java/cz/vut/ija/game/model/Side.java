/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Enum for storing the side which the tile is facing
 */
package cz.vut.ija.game.model;

/**
 * Four cardinal directions.
 */
public enum Side {
    /**
     * Direction pointing up.
     */
    NORTH,
    /**
     * Direction pointing right.
     */
    EAST,
    /**
     * Direction pointing down.
     */
    SOUTH,
    /**
     * Direction pointing left.
     */
    WEST;

    /**
     * Returns the opposite side of this side.
     *
     * @return the opposite direction (NORTH to SOUTH, EAST to WEST)
     */

    public Side opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
        }
        throw new IllegalStateException();
    }
}