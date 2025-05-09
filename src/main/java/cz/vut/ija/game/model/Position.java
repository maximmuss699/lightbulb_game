/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * Record used to store position
 */
package cz.vut.ija.game.model;

/** Immutable (row,col) coordinate on the board, implemented as Java 21 record. */
public record Position(int row, int col) {

    /* ──────────────────────────────── */
    /*  ★ COMPATIBILITY ACCESSORS ★     */
    /* ──────────────────────────────── */
    public int getRow() {    // <-- add this
        return row;
    }
    public int getCol() {    // <-- and this
        return col;
    }

    /** Step one cell in the given direction. */
    public Position step(Side dir) {
        return switch (dir) {
            case NORTH -> new Position(row - 1, col);
            case SOUTH -> new Position(row + 1, col);
            case EAST  -> new Position(row,     col + 1);
            case WEST  -> new Position(row,     col - 1);
        };
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}