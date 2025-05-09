package cz.vut.ija.game.generator;

import cz.vut.ija.game.model.*;
import cz.vut.ija.game.model.BulbTile;
import cz.vut.ija.game.model.TTile;
import java.util.*;


/**
 * LevelGenerator: generates puzzle layouts similar to the LightBulb game:
 * 1) Builds a perfect maze spanning the entire board.
 * 2) Selects random leaf nodes as bulb positions (BulbTile).
 * 3) Renders remaining connections as wires (Wire/L/T/X).
 * 4) Converts some straight segments (I) to T-junctions with probability T_BIAS.
 * 5) Scrambles tile rotations so no bulb is initially lit.
 */
public class LevelGenerator {
    private Set<Position> checkReach = new HashSet<>();
    private static final double T_BIAS = 0.2;
    private final int rows, cols, bulbCount;
    private final Random rnd = new Random();

    public LevelGenerator(int rows, int cols, int bulbCount) {
        if (bulbCount < 1) throw new IllegalArgumentException("bulbCount>=1");
        this.rows = rows;
        this.cols = cols;
        this.bulbCount = bulbCount;
    }

    // Generate a puzzle with the specified number of bulbs
    public GameBoard generatePuzzle() {
        int desiredT = Math.max(0, bulbCount - 1);
        Tile[][] solution;
        int attempt = 0;
        do {
            attempt++;
            System.out.println("=== GENERATING PUZZLE, ATTEMPT #" + attempt + " ===");
            solution = generateSolutionTiles();
            // Count T-branches in solution
            int tCount = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    Tile t = solution[r][c];
                    if ("T".equals(t.getType())) tCount++;
                }
            }
            System.out.println("T-branches: " + tCount + ", desired: " + desiredT);
            if (tCount != desiredT) {
                System.out.println("Mismatch T-branches, regenerating...");
            }
        } while (attempt < 20 &&
                // retry if count mismatch
                Arrays.stream(solution)
                        .flatMap(Arrays::stream)
                        .filter(t -> t.getType().equals("T"))
                        .count() != desiredT);
        if (Arrays.stream(solution).flatMap(Arrays::stream).filter(t -> t.getType().equals("T")).count() != desiredT) {
            throw new IllegalStateException("Unable to generate solution with exact T-branches");
        }
        System.out.println("Tree generation complete. Building GameBoard and scrambling rotations.");
        GameBoard board = new GameBoard(solution);
        // Before scramble loop, record solution rotations
        int[][] solRots = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                solRots[r][c] = solution[r][c].getRotation();
            }
        }
        // Rotate every bulb tile's solution orientation by 180 degrees
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (solution[r][c] instanceof BulbTile) {
                    solRots[r][c] = (solRots[r][c] + 180) % 360;
                }
            }
        }
        // Rotate every T-tile's solution orientation by 270 degrees
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (solution[r][c] instanceof TTile) {
                    solRots[r][c] = (solRots[r][c] + 270) % 360;
                }
            }
        }
        // Only include tiles that are part of the power path from source to bulbs
        // Uses checkReach from generateSolutionTiles
        Set<Position> poweredPath = new HashSet<>(checkReach);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position p = new Position(r, c);
                if (!poweredPath.contains(p)) {
                    solRots[r][c] = -1; // mark as not part of the solution
                }
            }
        }
        board.setSolutionRotations(solRots);
        // Scramble tile rotations
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = solution[r][c];
                int orig = t.getRotation(), rot;
                do { rot = rnd.nextInt(4)*90; } while (rot == orig);
                t.setRotation(rot);
                board.setTileRotation(r, c, rot);
            }
        }
        System.out.println("Puzzle ready (scrambled).\n");
        return board;
    }

    private Tile[][] generateSolutionTiles() {
        Position start = new Position(rnd.nextInt(rows), rnd.nextInt(cols));
        System.out.println("Start cell: " + start);
        // 1) Generate a perfect maze using DFS
        Map<Position, Set<Side>> conn = new HashMap<>();
        Set<Position> visited = new HashSet<>();
        System.out.println("Carving perfect maze...");
        dfsCarve(start, visited, conn);
        System.out.println("Perfect maze carved. Total cells visited: " + visited.size());
        // Check full connectivity of the maze
        if (visited.size() == rows * cols) {
            System.out.println("Check: all " + visited.size() + " cells are connected in a single tree.");
        } else {
            System.out.println("Warning: only " + visited.size() + " out of " + (rows*cols) + " cells are connected!");
        }

        // 2) Find leaf nodes (degree 1), excluding the start
        List<Position> allLeaves   = new ArrayList<>();
        List<Position> closeLeaves = new ArrayList<>();   // Manhattan ≤1
        for (Position p : conn.keySet()) {
            if (p.equals(start)) continue;
            if (conn.get(p).size() == 1) {
                int dist = Math.abs(p.getRow() - start.getRow()) + Math.abs(p.getCol() - start.getCol());
                if (dist <= 1) closeLeaves.add(p);
                else           allLeaves.add(p);
            }
        }
        System.out.println("Far leaves   : " + allLeaves);
        System.out.println("Close leaves : " + closeLeaves);

        // 3) Select exactly bulbCount positions for bulbs
        List<Position> pool = new ArrayList<>(allLeaves);
        Collections.shuffle(pool, rnd);
        while (pool.size() < bulbCount && !closeLeaves.isEmpty()) {
            // Add from close leaves if not enough far leaves
            pool.add(closeLeaves.remove(rnd.nextInt(closeLeaves.size())));
        }
        if (pool.size() < bulbCount) {
            // In extreme case, add any non-source cell (not ideal, but ensures count)
            for (Position p : conn.keySet()) {
                if (p.equals(start) || pool.contains(p)) continue;
                pool.add(p);
                if (pool.size() == bulbCount) break;
            }
        }
        Collections.shuffle(pool, rnd);
        Set<Position> bulbs = new LinkedHashSet<>(pool.subList(0, bulbCount));
        System.out.println("Selected bulb positions: " + bulbs);
        // Verify all bulbs are reachable from the carved tree
        Set<Position> reach = new HashSet<>();
        Deque<Position> dq = new ArrayDeque<>();
        reach.add(start); dq.add(start);
        while (!dq.isEmpty()) {
            Position curPos = dq.poll();
            for (Side s : conn.getOrDefault(curPos, Collections.emptySet())) {
                Position next = curPos.step(s);
                if (!reach.contains(next)) {
                    reach.add(next);
                    dq.add(next);
                }
            }
        }
        if (reach.containsAll(bulbs)) {
            System.out.println("Check: DFS-carve connected source to all bulbs.");
        } else {
            Set<Position> unreachable = new HashSet<>(bulbs);
            unreachable.removeAll(reach);
            System.out.println("Check: some bulbs unreachable: " + unreachable);
        }
        // Verify bulbs are reachable from the source in the final tree
        Set<Position> reachable = new HashSet<>();
        Deque<Position> queue = new ArrayDeque<>();
        reachable.add(start); queue.add(start);
        while (!queue.isEmpty()) {
            Position cur = queue.poll();
            for (Side s : conn.getOrDefault(cur, Collections.emptySet())) {
                Position next = cur.step(s);
                if (!reachable.contains(next)) {
                    reachable.add(next);
                    queue.add(next);
                }
            }
        }
        boolean allReachable = true;
        for (Position b : bulbs) {
            if (!reachable.contains(b)) { allReachable = false; break; }
        }
        if (allReachable) {
            System.out.println("Check passed: all bulbs reachable from source.");
        } else {
            System.out.println("Check failed: some bulbs NOT reachable! Reachable set: " + reachable);
        }

        // 4) Apply T-bias to straight segments
        System.out.println("Applying T-bias (" + T_BIAS + ") to straight segments...");
        addTBias(conn, bulbs);
        System.out.println("T-bias applied.");

        // Prune dead-end branches not leading to any bulb
        System.out.println("Pruning dead-end branches not leading to bulbs...");
        boolean removed;
        do {
            removed = false;
            for (Position p : new ArrayList<>(conn.keySet())) {
                Set<Side> sides = conn.get(p);
                // dead end: not a bulb, not the source, degree == 1
                if (!bulbs.contains(p) && !p.equals(start) && sides.size() == 1) {
                    Side s = sides.iterator().next();
                    Position np = p.step(s);
                    sides.remove(s);
                    conn.getOrDefault(np, Collections.emptySet()).remove(s.opposite());
                    removed = true;
                }
            }
        } while (removed);
        System.out.println("Pruning complete.");

        // Optimize: remove unnecessary T-branches
        System.out.println("Optimizing to remove unnecessary T-branches...");
        for (Position p : new ArrayList<>(conn.keySet())) {
            Set<Side> sides = conn.get(p);
            if (sides.size() >= 3) {
                // attempt to remove each side if it's not needed
                for (Side s : new ArrayList<>(sides)) {
                    // Temporarily remove branch
                    sides.remove(s);
                    Position np = p.step(s);
                    conn.getOrDefault(np, Collections.emptySet()).remove(s.opposite());
                    // Check connectivity from source to all bulbs
                    Set<Position> reach2 = new HashSet<>();
                    Deque<Position> dq2 = new ArrayDeque<>();
                    reach2.add(start); dq2.add(start);
                    while (!dq2.isEmpty()) {
                        Position cur = dq2.poll();
                        for (Side ss : conn.getOrDefault(cur, Collections.emptySet())) {
                            Position nxt = cur.step(ss);
                            if (!reach2.contains(nxt)) {
                                reach2.add(nxt);
                                dq2.add(nxt);
                            }
                        }
                    }
                    if (!reach2.containsAll(bulbs)) {
                        // revert removal if any bulb becomes unreachable
                        sides.add(s);
                        conn.computeIfAbsent(np, k -> new HashSet<>()).add(s.opposite());
                    } else {
                        System.out.println("Removed unnecessary branch at " + p + " towards " + np);
                    }
                }
            }
        }
        System.out.println("Optimization complete.");

        // Final connectivity check
        checkReach.clear();
        Deque<Position> checkQueue = new ArrayDeque<>();
        checkReach.add(start);
        checkQueue.add(start);
        while (!checkQueue.isEmpty()) {
            Position cur = checkQueue.poll();
            for (Side s : conn.getOrDefault(cur, Collections.emptySet())) {
                Position nxt = cur.step(s);
                if (!checkReach.contains(nxt)) {
                    checkReach.add(nxt);
                    checkQueue.add(nxt);
                }
            }
        }
        if (checkReach.containsAll(bulbs)) {
            System.out.println("SUCCESS: ALL BULBS CONNECTED");
        } else {
            System.out.println("FAILURE: BULBS NOT ALL CONNECTED: " + bulbs.stream()
                    .filter(b -> !checkReach.contains(b))
                    .toList());
        }

        // 5) Build the tile matrix
        System.out.println("Building tile matrix...");
        Tile[][] tiles = new Tile[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Position p = new Position(r, c);
                Set<Side> needs = conn.getOrDefault(p, Collections.emptySet());
                Tile t;
                if (p.equals(start)) {
                    t = new SourceTile();
                } else if (bulbs.contains(p)) {
                    t = new BulbTile();            // leaf
                } else {
                    t = chooseWire(needs);
                }
                int rot = computeRotation(needs, t);
                t.setRotation(rot);
                tiles[r][c] = t;
            }
        }
        System.out.println("Tile matrix built.\n");
        return tiles;
    }

    // Recursive DFS carve to generate the maze
    private void dfsCarve(Position cur, Set<Position> visited, Map<Position,Set<Side>> conn) {
        visited.add(cur);
        List<Side> dirs = new ArrayList<>(Arrays.asList(Side.values()));
        Collections.shuffle(dirs, rnd);
        for (Side s : dirs) {
            Position nxt = cur.step(s);
            if (!inBounds(nxt) || visited.contains(nxt)) continue;
            // vyříznout spojení (carve)
            conn.computeIfAbsent(cur, k->new HashSet<>()).add(s);
            conn.computeIfAbsent(nxt, k->new HashSet<>()).add(s.opposite());
            if (visited.size() % 10 == 0) {
                System.out.println("  DFS visited " + visited.size() + " cells...");
            }
            dfsCarve(nxt, visited, conn);
        }
    }

    // Add T-junctions to some straight segments based on bias
    private void addTBias(Map<Position,Set<Side>> conn, Set<Position> bulbs) {
        // Collect candidates for T-bias: straight segments not adjacent to bulbs
        List<Position> candidates = new ArrayList<>();
        for (Position p : new ArrayList<>(conn.keySet())) {
            Set<Side> sides = conn.get(p);
            // If node is directly adjacent to a bulb, do not add a branch – prevents leakage
            boolean nextToBulb = false;
            for (Side sNeighbour : sides) {
                Position neighbour = p.step(sNeighbour);
                if (bulbs.contains(neighbour)) { nextToBulb = true; break; }
            }
            if (nextToBulb) continue;
            if (sides.size() == 2) {
                Iterator<Side> it = sides.iterator(); Side a = it.next(), b = it.next();
                if (a.opposite() == b) {
                    candidates.add(p);
                }
            }
        }
        int desired = Math.max(0, bulbCount - 1);
        Collections.shuffle(candidates, rnd);
        System.out.println("Adding exactly " + desired + " T-branches for " + bulbCount + " bulbs");
        for (int i = 0; i < Math.min(desired, candidates.size()); i++) {
            Position p = candidates.get(i);
            Set<Side> sides = conn.get(p);
            // find any free direction to branch
            for (Side s : Side.values()) {
                if (!sides.contains(s) && inBounds(p.step(s))) {
                    Position np = p.step(s);
                    conn.get(p).add(s);
                    conn.computeIfAbsent(np, k->new HashSet<>()).add(s.opposite());
                    System.out.println("  T-branch: added at " + p + " towards " + np);
                    break;
                }
            }
        }
    }

    // Choose appropriate wire tile type based on required connections
    private Tile chooseWire(Set<Side> need) {
        switch (need.size()) {
            case 0: case 1: return new WireTile();
            case 2: {
                Iterator<Side> it = need.iterator(); Side a = it.next(), b = it.next();
                return (a.opposite() == b) ? new WireTile() : new LTile();
            }
            case 3: return new TTile();
            case 4: return new XTile();
            default: return new WireTile();
        }
    }

    // Compute the rotation needed to match desired connection sides
    private int computeRotation(Set<Side> needs, Tile t) {
        Set<Side> base;
        switch (t.getType()) {
            case "I": base = EnumSet.of(Side.NORTH,Side.SOUTH); break;
            case "L": base = EnumSet.of(Side.NORTH,Side.EAST); break;
            case "T": base = EnumSet.of(Side.NORTH,Side.EAST,Side.SOUTH); break;
            case "X": base = EnumSet.allOf(Side.class); break;
            case "S": base = EnumSet.of(Side.SOUTH); break;
            case "B": base = EnumSet.of(Side.NORTH); break;
            default:  base = EnumSet.noneOf(Side.class);
        }
        for (int k = 0; k < 4; k++) {
            Set<Side> rotSides = EnumSet.noneOf(Side.class);
            for (Side s : base) {
                Side cur = s;
                for (int i = 0; i < k; i++) cur = rotate90(cur);
                rotSides.add(cur);
            }
            if (rotSides.equals(needs)) return k * 90;
        }
        return 0;
    }

    // Rotate a side clockwise by 90 degrees
    private Side rotate90(Side s) {
        switch (s) {
            case NORTH: return Side.EAST;
            case EAST:  return Side.SOUTH;
            case SOUTH: return Side.WEST;
            default:    return Side.NORTH;
        }
    }

    // Check if a position is within board bounds
    private boolean inBounds(Position p) {
        return p.getRow() >= 0 && p.getRow() < rows
                && p.getCol() >= 0 && p.getCol() < cols;
    }

}