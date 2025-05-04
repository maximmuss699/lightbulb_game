package cz.vut.ija.game.model;

public abstract class Tile {
    private int rotation = 0;

    /** Rotate 90° clockwise */
    public void rotate() {
        rotation = (rotation + 90) % 360;
    }

    public int getRotation() {
        return rotation;
    }

    /** e.g. "I", "L", "T", "X" */
    public abstract String getType();
}