package cz.vut.ija.game.logic;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.Position;
import cz.vut.ija.game.model.Side;
import cz.vut.ija.game.model.Tile;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Simulator to determine which tiles are powered by the source.
 * Performs a flood-fill from the source tile, following matching connections.
 */
public class GameSimulator {
    private final GameBoard board;
    private final Set<Position> powered = new HashSet<>();

    /**
     * Constructs the simulator for the given game board.
     * @param board the game board model
     */
    public GameSimulator(GameBoard board) {
        this.board = board;
    }

    /**
     * Runs a BFS from the source tile, marking all reachable (powered) tiles.
     */
    public void propagate() {
        powered.clear();
        Position source = board.findSource();
        if (source == null) return;

        Deque<Position> queue = new ArrayDeque<>();
        queue.add(source);
        powered.add(source);

        while (!queue.isEmpty()) {
            Position cur = queue.poll();
            Tile tile = board.getTile(cur.getRow(), cur.getCol());
            for (Side s : Side.values()) {
                // Only follow if this tile has a connector on side s
                if (!tile.connects(s)) continue;
                Position next = cur.step(s);
                if (!board.inBounds(next) || powered.contains(next)) continue;
                Tile neighbor = board.getTile(next.getRow(), next.getCol());
                // Neighbor must have opposite connector
                if (neighbor.connects(s.opposite())) {
                    powered.add(next);
                    queue.add(next);
                }
            }
        }
    }

    /**
     * Returns true if the tile at (r,c) is powered after the last propagate().
     */
    public boolean isPowered(int r, int c) {
        return powered.contains(new Position(r, c));
    }
}
