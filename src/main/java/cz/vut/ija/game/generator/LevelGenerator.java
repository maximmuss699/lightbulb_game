package cz.vut.ija.game.generator;

import cz.vut.ija.game.model.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * LevelGenerator: builds a valid wiring solution and then scrambles tile rotations
 * to create a playable puzzle.
 */
public class LevelGenerator {
    private final int rows, cols, bulbCount;
    private final Random rnd = new Random();

    public LevelGenerator(int rows, int cols, int bulbCount) {
        if (bulbCount < 1) throw new IllegalArgumentException("bulbCount>=1");
        this.rows = rows; this.cols = cols; this.bulbCount = bulbCount;
    }

    /**
     * Generates a new puzzle GameBoard by first constructing a solved wiring layout
     * and then randomizing each tile's rotation so that no bulb is lit initially.
     *
     * @return a GameBoard instance containing the scrambled puzzle
     */
    public GameBoard generatePuzzle() {
        // Step 1: Generate the solved wiring solution as a 2D Tile array
        Tile[][] solution = generateSolutionTiles();
        GameBoard board = new GameBoard(solution);

        // Step 2: Randomize each tile's rotation (different from its solution orientation)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = solution[r][c];
                int orig = t.getRotation();
                int newRot;
                do { newRot = rnd.nextInt(4)*90; }
                while (newRot == orig);
                t.setRotation(newRot);
                board.setTileRotation(r, c, newRot);
            }
        }
        return board;
    }

    /**
     * Constructs a 2D array of Tile objects in the correct orientation
     * forming a single power source connected to all bulbs via wires.
     *
     * @return a matrix of Tiles representing the solved layout
     */
    private Tile[][] generateSolutionTiles() {
        // Choose a random cell to be the power source
        Position source = new Position(rnd.nextInt(rows), rnd.nextInt(cols));
        // Randomly select bulbCount distinct positions (excluding the source)
        Set<Position> bulbs = new HashSet<>();
        while (bulbs.size() < bulbCount) {
            Position p = new Position(rnd.nextInt(rows), rnd.nextInt(cols));
            if (!p.equals(source)) bulbs.add(p);
        }

        // Build a minimum spanning tree connecting the source to each bulb
        Map<Position, Set<Side>> connMap = new HashMap<>();
        BiConsumer<Position, Position> connect = (a,b) -> {
            Side s = sideBetween(a,b);
            connMap.computeIfAbsent(a, k->new HashSet<>()).add(s);
            connMap.computeIfAbsent(b, k->new HashSet<>()).add(s.opposite());
        };

        // For each bulb: find a BFS path and connect pairs sequentially
        for (Position bulb : bulbs) {
            Map<Position,Position> parent = new HashMap<>();
            Queue<Position> q = new ArrayDeque<>();
            parent.put(source, null);
            q.add(source);
            Position end = null;
            // Perform BFS to find a path from the source to this bulb
            // Block other bulbs from being used as intermediate nodes
            while (!q.isEmpty()) {
                Position cur = q.poll();
                if (cur.equals(bulb)) { end = cur; break; }
                for (Side s : Side.values()) {
                    Position nxt = cur.step(s);
                    if (inBounds(nxt)
                        && !parent.containsKey(nxt)
                        && (!bulbs.contains(nxt) || nxt.equals(bulb))) {
                        parent.put(nxt, cur);
                        q.add(nxt);
                    }
                }
            }
            // restore path and connect pairs
            for (Position cur = end; parent.get(cur) != null; cur = parent.get(cur)) {
                connect.accept(parent.get(cur), cur);
            }
        }

        // Instantiate each tile based on how many connections it needs:
        // - SourceTile for the source position
        // - BulbTile for bulb positions with one connection
        // - WireTile, LTile, TTile, or XTile for other positions
        Tile[][] tiles = new Tile[rows][cols];
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                Position p = new Position(r, c);
                Set<Side> needs = connMap.getOrDefault(p, Collections.emptySet());
                Tile t;
                if (p.equals(source)) {
                    // Source tile must always be placed at the source position
                    t = new SourceTile();
                } else {
                    // Other tiles based on required connections
                    switch (needs.size()) {
                        case 0:
                            t = new WireTile();
                            break;
                        case 1:
                            if (bulbs.contains(p)) {
                                t = new BulbTile();
                            } else {
                                t = new WireTile();
                            }
                            break;
                        case 2:
                            Iterator<Side> it = needs.iterator();
                            Side a = it.next(), b = it.next();
                            if (a.opposite() == b) {
                                t = new WireTile();
                            } else {
                                t = new LTile();
                            }
                            break;
                        case 3:
                            t = new TTile();
                            break;
                        case 4:
                            t = new XTile();
                            break;
                        default:
                            t = new WireTile();
                    }
                }
                // Set the correct orientation
                int rot = computeRotation(needs, t);
                t.setRotation(rot);
                tiles[r][c] = t;
            }
        }
        return tiles;
    }

    /**
     * Computes the correct rotation angle for a tile so that its base
     * connection sides match the required 'needs' set.
     *
     * @param needs the set of sides this tile must connect to
     * @param t the tile whose base connection pattern is used
     * @return rotation in degrees (0, 90, 180, 270)
     */
    private int computeRotation(Set<Side> needs, Tile t) {
        Set<Side> base;
        switch (t.getType()) {
            case "I": base = EnumSet.of(Side.NORTH, Side.SOUTH); break;
            case "L": base = EnumSet.of(Side.NORTH, Side.EAST); break;
            case "T": base = EnumSet.of(Side.NORTH, Side.EAST, Side.SOUTH); break;
            case "X": base = EnumSet.allOf(Side.class); break;
            case "S": base = EnumSet.of(Side.SOUTH); break;
            case "B": base = EnumSet.of(Side.NORTH); break;
            default: base = EnumSet.noneOf(Side.class);
        }
        for (int k=0; k<4; k++) {
            Set<Side> rotated = EnumSet.noneOf(Side.class);
            for (Side s : base) {
                Side cur = s;
                for (int i=0; i<k; i++) {
                    cur = cur==Side.NORTH?Side.EAST
                            : cur==Side.EAST?Side.SOUTH
                            : cur==Side.SOUTH?Side.WEST
                            : Side.NORTH;
                }
                rotated.add(cur);
            }
            if (rotated.equals(needs)) return k*90;
        }
        return 0;
    }

    /** Returns true if the given position is within the board boundaries. */
    private boolean inBounds(Position p) {
        return p.getRow()>=0 && p.getRow()<rows
                && p.getCol()>=0 && p.getCol()<cols;
    }

    /**
     * Determines which Side (NORTH/SOUTH/EAST/WEST) separates two adjacent positions.
     * @throws IllegalArgumentException if the positions are not adjacent.
     */
    private Side sideBetween(Position a, Position b) {
        if (b.getRow()==a.getRow()+1 && b.getCol()==a.getCol()) return Side.SOUTH;
        if (b.getRow()==a.getRow()-1 && b.getCol()==a.getCol()) return Side.NORTH;
        if (b.getCol()==a.getCol()+1 && b.getRow()==a.getRow()) return Side.EAST;
        if (b.getCol()==a.getCol()-1 && b.getRow()==a.getRow()) return Side.WEST;
        throw new IllegalArgumentException(a + " not adjacent to " + b);
    }
}