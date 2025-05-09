/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * Enum for storing the side which the tile is facing
 */
package cz.vut.ija.game.model;

/** Four cardinal directions. */
public enum Side {
    NORTH, EAST, SOUTH, WEST;

    /** Returns the opposite side. */
    public Side opposite() {
        switch(this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST:  return WEST;
            case WEST:  return EAST;
        }
        throw new IllegalStateException();
    }
}