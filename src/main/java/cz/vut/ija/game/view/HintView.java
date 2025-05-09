/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A view that opens up a new window with a hint for each tile.
 */
package cz.vut.ija.game.view;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.Tile;
import cz.vut.ija.game.logic.GameSimulator;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Informational preview window. Similar to BoardView but non-interactive,
 * displays the number of rotations needed for each tile to reach its correct orientation.
 */
public class HintView extends GridPane {
    /**
     * The game board model.
     */
    private final GameBoard model;
    /**
     * Game simulator for checking connections.
     */
    private final GameSimulator simulator;
    /**
     * Grid of panes for placing tiles.
     */
    private final StackPane[][] tilePanes;
    /**
     * Grid of image views for tile images.
     */
    private final ImageView[][] tileImages;
    /**
     * Grid of text labels for hints.
     */
    private final Text[][] tileHints;
    /**
     * Button to close the hint window.
     */
    private final Button closeButton;
    /**
     * Window for displaying hints.
     */
    private Stage stage;

    /** Sum of all hint rotations needed across the board */
    private int totalHintClicks = 0;
    /** Stores the initial total of hint clicks when first calculated */
    private Integer initialTotalHintClicks = null;

    /**
     * Size of each tile in pixels.
     */
    private static final int TILE_SIZE = 80;

    /**
     * Image for empty tile.
     */
    private final Image empty_tile = new Image(getClass().getResourceAsStream("/emptytile/empty_tile.png"));
    /**
     * Image for I wire when lit.
     */
    private final Image wireI_lit = new Image(getClass().getResourceAsStream("/wires/I_lit.png"));
    /**
     * Image for I wire when unlit.
     */
    private final Image wireI_unlit = new Image(getClass().getResourceAsStream("/wires/I_unlit.png"));
    /**
     * Image for L wire when lit.
     */
    private final Image wireL_lit = new Image(getClass().getResourceAsStream("/wires/L_lit.png"));
    /**
     * Image for L wire when unlit.
     */
    private final Image wireL_unlit = new Image(getClass().getResourceAsStream("/wires/L_unlit.png"));
    /**
     * Image for T wire when lit.
     */
    private final Image wireT_lit = new Image(getClass().getResourceAsStream("/wires/T_lit.png"));
    /**
     * Image for T wire when unlit.
     */
    private final Image wireT_unlit = new Image(getClass().getResourceAsStream("/wires/T_unlit.png"));
    /**
     * Image for X wire when lit.
     */
    private final Image wireX_lit = new Image(getClass().getResourceAsStream("/wires/X_lit.png"));
    /**
     * Image for X wire when unlit.
     */
    private final Image wireX_unlit = new Image(getClass().getResourceAsStream("/wires/X_unlit.png"));
    /**
     * Image for light bulb when lit.
     */
    private final Image lightbulb_lit = new Image(getClass().getResourceAsStream("/lightbulb/lightbulb_lit.png"));
    /**
     * Image for light bulb when unlit.
     */
    private final Image lightbulb_unlit = new Image(getClass().getResourceAsStream("/lightbulb/lightbulb_unlit.png"));
    /**
     * Image for power node.
     */
    private final Image power_node = new Image(getClass().getResourceAsStream("/powernode/power_node.png"));

    /**
     * Creates a new hint view for the provided game board.
     *
     * @param model the game board to show hints for
     */
    public HintView(GameBoard model) {
        this.model = model;
        this.simulator = new GameSimulator(model);
        int rows = model.getRows(), cols = model.getCols();
        tilePanes = new StackPane[rows][cols];
        tileImages = new ImageView[rows][cols];
        tileHints = new Text[rows][cols];

        getStyleClass().add("board-view");
        setAlignment(Pos.CENTER);
        setHgap(5);
        setVgap(5);

        // Black background for hint preview
        this.setStyle("-fx-background-color: black;");

        buildGrid();

        simulator.propagate();
        applyPowerStyles();
        refreshHints();

        closeButton = new Button("Close Preview");
        closeButton.setStyle(
                "-fx-background-color: #444444; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5px;"
        );
        closeButton.setOnAction(e -> stage.close());
        add(closeButton, 0, rows, cols, 1);
    }

    /**
     * Checks if current and target rotations are effectively the same for symmetric tiles.
     *
     * @param tile    the tile to check
     * @param current current rotation
     * @param target  target rotation
     * @return true if rotations are functionally equivalent
     */
    private boolean isSameOrientation(Tile tile, int current, int target) {
        String type = tile.getType();
        switch (type) {
            case "I":
                return (current % 180) == (target % 180);
            case "X":
                return true;
            default:
                return current == target;
        }
    }

    /**
     * Builds the grid of tile images and hint text.
     */
    private void buildGrid() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                StackPane pane = new StackPane();
                pane.setPrefSize(TILE_SIZE, TILE_SIZE);

                ImageView iv = new ImageView();
                iv.setFitWidth(TILE_SIZE);
                iv.setFitHeight(TILE_SIZE);
                iv.setPreserveRatio(true);
                tileImages[r][c] = iv;
                pane.getChildren().add(iv);

                Text hint = new Text();
                hint.getStyleClass().add("hint-label");
                hint.setStyle("-fx-fill: white; -fx-font-size: 36px; -fx-font-weight: bold; -fx-stroke: black; -fx-stroke-width: 1.5px;");
                StackPane.setAlignment(hint, Pos.CENTER);

                tileHints[r][c] = hint;
                pane.getChildren().add(hint);

                tilePanes[r][c] = pane;
                add(pane, c, r);
            }
        }
    }

    /**
     * Updates all tile images based on their power status.
     */
    private void applyPowerStyles() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Tile tile = model.getTile(r, c);
                boolean powered = simulator.isPowered(r, c);
                updateTileImage(tileImages[r][c], tile, powered);
            }
        }
    }

    /**
     * Updates a tile image based on its type and power status.
     *
     * @param imageView the image view to update
     * @param tile      the tile to display
     * @param powered   whether the tile is powered
     */
    private void updateTileImage(ImageView imageView, Tile tile, boolean powered) {
        String type = tile.getType();
        switch (type) {
            case "I":
                imageView.setImage(powered ? wireI_lit : wireI_unlit);
                break;
            case "L":
                imageView.setImage(powered ? wireL_lit : wireL_unlit);
                break;
            case "T":
                imageView.setImage(powered ? wireT_lit : wireT_unlit);
                break;
            case "X":
                imageView.setImage(powered ? wireX_lit : wireX_unlit);
                break;
            case "S":
                imageView.setImage(power_node);
                break;
            case "B":
                imageView.setImage(powered ? lightbulb_lit : lightbulb_unlit);
                break;
            default:
                imageView.setImage(empty_tile);
        }
        imageView.setRotate(tile.getRotation());
    }

    /**
     * Refreshes click counts and updates tile states.
     */
    public void refreshHints() {
        simulator.propagate();
        applyPowerStyles();
        totalHintClicks = 0;
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Tile tile = model.getTile(r, c);
                // Skip non-solution tiles: no base connections and not source or bulb
                if (tile.getBaseSides().isEmpty() && !"S".equals(tile.getType()) && !"B".equals(tile.getType())) {
                    tileHints[r][c].setText("");
                    continue;
                }
                int current = tile.getRotation();
                int target = model.getSolutionRotations()[r][c];
                if (target == -1) {
                    tileHints[r][c].setText(""); // skip non-solution tiles
                    continue;
                }
                int diff = (target - current + 360) % 360;
                int clicks = diff / 90;
                // For straight 'I' tiles, only parity of rotations matters
                if ("I".equals(tile.getType())) {
                    clicks = clicks % 2;
                }
                // Otherwise, apply symmetry check
                else if (isSameOrientation(tile, current, target)) {
                    clicks = 0;
                }
                totalHintClicks += clicks;
                if (clicks > 0) {
                    tileHints[r][c].setText("↻" + clicks);
                } else {
                    tileHints[r][c].setText("");
                }
            }
        }
        // Record the initial total only once
        if (initialTotalHintClicks == null) {
            initialTotalHintClicks = totalHintClicks;
        }
    }

    /**
     * Shows or re-shows the hint preview window, refreshing its contents.
     */
    public void show() {
        // Refresh tile states and hint counts before showing
        refreshHints();

        // If stage doesn't exist, create a new one
        if (stage == null) {
            stage = new Stage();
            stage.setTitle("Hint Preview");
            stage.setScene(new Scene(this));
        }
        // Un-hide or show
        stage.show();
        stage.toFront();
    }

    /**
     * Closes the hint window.
     */
    public void close() {
        if (stage != null) {
            stage.hide();
        }
    }

    /**
     * Gets the total hint clicks.
     *
     * @return the sum of all hint clicks across the board
     */
    public int getTotalHintClicks() {
        return totalHintClicks;
    }

    /**
     * Returns the initial sum of hint rotations needed across the board.
     *
     * @return initial sum of hint rotations
     */
    public int getInitialTotalHintClicks() {
        return initialTotalHintClicks != null ? initialTotalHintClicks : totalHintClicks;
    }
}
