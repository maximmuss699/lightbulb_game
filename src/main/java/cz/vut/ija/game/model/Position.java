package cz.vut.ija.game.model;

/** Immutable (row, col) coordinate on the board. */
public final class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public int getRow() { return row; }
    public int getCol() { return col; }

    /** Step one cell in the given direction. */
    public Position step(Side dir) {
        switch(dir) {
            case NORTH: return new Position(row-1, col);
            case SOUTH: return new Position(row+1, col);
            case EAST:  return new Position(row, col+1);
            case WEST:  return new Position(row, col-1);
        }
        throw new IllegalStateException();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position)o;
        return row == p.row && col == p.col;
    }
    @Override public int hashCode() {
        return 31*row + col;
    }
    @Override public String toString() {
        return "(" + row + "," + col + ")";
    }
}