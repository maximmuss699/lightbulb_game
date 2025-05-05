package cz.vut.ija.game.command;

import cz.vut.ija.game.model.GameBoard;

/**
 * Rotates a single tile on the game board and supports undoing the rotation.
 */
public class RotateCommand implements Command {
    private final GameBoard board;
    private final int row;
    private final int col;
    private final int beforeRotation;
    private int afterRotation;

    /**
     * Constructs a RotateCommand for the specified tile.
     *
     * @param board the game board model
     * @param row   the row index of the tile
     * @param col   the column index of the tile
     */
    public RotateCommand(GameBoard board, int row, int col) {
        this.board = board;
        this.row   = row;
        this.col   = col;
        // Capture the current rotation before execution
        this.beforeRotation = board.getTile(row, col).getRotation();
    }

    /**
     * Executes the rotation: rotates the tile 90° clockwise.
     * Captures the new rotation for potential redo.
     */
    @Override
    public void execute() {
        board.rotateTile(row, col);
        afterRotation = board.getTile(row, col).getRotation();
    }

    /**
     * Undoes the rotation by restoring the tile to its previous rotation.
     */
    @Override
    public void undo() {
        board.setTileRotation(row, col, beforeRotation);
    }
}