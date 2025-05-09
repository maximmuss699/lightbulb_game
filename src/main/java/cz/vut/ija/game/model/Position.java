/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Record used to store position
 */
package cz.vut.ija.game.model;

/**
 * Immutable (row,col) coordinate on the board, implemented as Java 21 record.
 *
 * @param row row
 * @param col column
 */
public record Position(int row, int col) {

    /* ──────────────────────────────── */
    /*  ★ COMPATIBILITY ACCESSORS ★     */
    /* ──────────────────────────────── */

    /**
     * Gets the row coordinate.
     *
     * @return row index
     */
    public int getRow() {    // <-- add this
        return row;
    }

    /**
     * Gets the column coordinate.
     *
     * @return column index
     */
    public int getCol() {    // <-- and this
        return col;
    }

    /**
     * Creates a new position by moving one step in the given direction.
     *
     * @param dir direction to step in
     * @return new position after stepping
     */
    public Position step(Side dir) {
        return switch (dir) {
            case NORTH -> new Position(row - 1, col);
            case SOUTH -> new Position(row + 1, col);
            case EAST -> new Position(row, col + 1);
            case WEST -> new Position(row, col - 1);
        };
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}