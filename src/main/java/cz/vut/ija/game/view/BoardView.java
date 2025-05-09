/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A view of the actual game board. The logic of showing the game is here.
 */
package cz.vut.ija.game.view;

import cz.vut.ija.game.controller.GameController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import cz.vut.ija.game.model.BoardObserver;
import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.Tile;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import cz.vut.ija.game.logic.GameSimulator;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

/**
 * Represents the game board in the UI.
 */

public class BoardView extends GridPane implements BoardObserver {
    /**
     * The game board model.
     */
    private final GameBoard model;
    /**
     * Game simulator for checking connections.
     */
    private final GameSimulator simulator;
    /**
     * Controller that handles game logic.
     */
    private GameController controller;

    /**
     * Grid of panes for placing tiles.
     */
    private final StackPane[][] tilePanes;
    /**
     * Grid of image views for displaying tiles.
     */
    private final ImageView[][] tileImages;

    /**
     * Button for auto-solving the puzzle.
     */
    private Button solveButton;
    /**
     * Hint window for showing hints to the player.
     */
    private HintView hintWindow;

    /**
     * Tracks if victory message has been shown.
     */
    private boolean victoryShown = false;

    /**
     * True if this is used for replay mode (prevents showing solve and hint button)
     */
    private boolean isReplayMode = false;

    /**
     * Size of each tile in pixels.
     */
    private static final int TILE_SIZE = 75;

    /**
     * A counter for player moves
     */
    private final Label moveCounterLabel = new Label("Moves: 0");

    /**
     * A Label for total hint-clicks from the hint window
     */
    private final Label totalHintClicksLabel = new Label("Optimal moves: 0");

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
     * Creates a board view.
     *
     * @param model        the game board model
     * @param isReplayMode true if this is for replay mode
     */
    public BoardView(GameBoard model, boolean isReplayMode) {

        // Call the superclass constructor
        super();
        this.getStyleClass().add("board-view"); // Add a CSS class to the grid pane

        this.setAlignment(Pos.CENTER);

        // Spaces between tiles
        this.setHgap(5);
        this.setVgap(5);

        this.model = model;
        model.addObserver(this); // Register this view as an observer of the model

        this.isReplayMode = isReplayMode;

        // Initialize move counter label
        moveCounterLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        totalHintClicksLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Allocate the tile panes matching the board dimensions
        tilePanes = new StackPane[model.getRows()][model.getCols()];
        tileImages = new ImageView[model.getRows()][model.getCols()];

        // Initialize the power simulator
        simulator = new GameSimulator(model);

        // Build the grid of buttons
        buildGrid();

        // Determine initial powered state and style tiles
        simulator.propagate();
        applyPowerStyles();

        if (model.getSolutionRotations() != null && !isReplayMode) {
            initializeHintAndControls();
            // Initialize hint counts immediately
            hintWindow.refreshHints();
            totalHintClicksLabel.setText("Optimal moves: " + hintWindow.getTotalHintClicks());
        }
    }

    /**
     * Initializes the hint window and control buttons.
     * This inlitializes only when we start the game from play screen
     */
    private void initializeHintAndControls() {
        // Initialize hint window
        this.hintWindow = new HintView(model);

        // add control buttons
        solveButton = new Button("Solve");
        solveButton.setOnAction(e -> autoSolve());

        Button hintButton = new Button("Show Hints");
        hintButton.setOnAction(e -> hintWindow.show());

        // apply a style to the buttons
        solveButton.getStyleClass().add("game-button");
        hintButton.getStyleClass().add("game-button");

        // Arrange buttons horizontally with spacing and include move counter label and hint clicks label
        HBox buttonBox = new HBox(10, solveButton, hintButton, moveCounterLabel, totalHintClicksLabel);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Place buttonBox below the grid
        this.add(buttonBox, 0, model.getRows(), model.getCols(), 1);
    }

    /**
     * Constructs the grid structure for the board view.
     */
    private void buildGrid() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                StackPane tilePane = createTilePane(r, c);
                tilePanes[r][c] = tilePane;
                add(tilePane, c, r);
            }
        }
    }

    /**
     * Creates a visual representation of a tile as a StackPane with a specific size.
     * Links a mouse click action to the tile, firing a custom TileClickEvent.
     *
     * @param r the row index of the tile
     * @param c the column index of the tile
     * @return a StackPane representing the tile at the specified position
     */
    private StackPane createTilePane(int r, int c) {
        Tile tile = model.getTile(r, c);

        StackPane tilePane = new StackPane();
        tilePane.setPrefSize(TILE_SIZE, TILE_SIZE);
        tilePane.setMinSize(TILE_SIZE, TILE_SIZE);
        tilePane.setMaxSize(TILE_SIZE, TILE_SIZE);

        // loads a specific image for the tile type
        ImageView tileImage = createTileImage(tile);
        tileImages[r][c] = tileImage;

        tilePane.getChildren().add(tileImage);

        // fire a MVC event
        tilePane.setOnMouseClicked(e ->
                fireEvent(new TileClickEvent(r, c))
        );

        return tilePane;
    }

    /**
     * Creates a visual representation of a tile as an ImageView, sets its size,
     * and initializes its image to the unpowered state.
     *
     * @param tile the Tile object for which the ImageView is to be created
     * @return an ImageView representing the specified tile
     */
    private ImageView createTileImage(Tile tile) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setPreserveRatio(true);

        // Based on whether the tile is powered or not, set the image
        updateTileImage(imageView, tile, false); // false = unpowered

        return imageView;
    }

    /**
     * Updates the appearance of a tile's image based on its type, whether it is powered,
     * and its current rotation.
     *
     * @param imageView the ImageView representing the tile's image
     * @param tile      the tile whose image is to be updated
     * @param powered   a boolean indicating whether the tile is powered
     */
    private void updateTileImage(ImageView imageView, Tile tile, boolean powered) {
        String tileType = tile.getType();

        switch (tileType) {
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

        // rotate accordingly
        imageView.setRotate(tile.getRotation());
    }

    /**
     * Updates the visual state of a tile when it is changed.
     *
     * @param row the row index of the tile
     * @param col the column index of the tile
     */
    @Override
    public void tileChanged(int row, int col) {
        Tile tile = model.getTile(row, col);
        tileImages[row][col].setRotate(tile.getRotation());

        // recalculate the powered state
        simulator.propagate();
        applyPowerStyles();

        if (hintWindow != null) {
            hintWindow.refreshHints();
            totalHintClicksLabel.setText("Optimal moves: " + hintWindow.getInitialTotalHintClicks());
        }

        checkVictory();
    }

    /**
     * Updates each tile's image based on whether it's powered.
     */
    private void applyPowerStyles() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Tile tile = model.getTile(r, c);

                // Remove old power classes
                boolean powered = simulator.isPowered(r, c);
                updateTileImage(tileImages[r][c], tile, powered);
            }
        }
    }

    /**
     * Rotate all tiles to their solution rotations and refresh the view.
     */
    private void autoSolve() {
        int[][] sol = model.getSolutionRotations();
        if (sol == null) return;
        for (int r = 0; r < sol.length; r++) {
            for (int c = 0; c < sol[r].length; c++) {
                if (sol[r][c] == -1) continue;  // 🔴 Skip tiles not in solution
                model.setTileRotation(r, c, sol[r][c]);
            }
        }
        simulator.propagate();
        applyPowerStyles();

        if (hintWindow != null) {
            hintWindow.refreshHints();
        }
    }


    /**
     * Checks if all lightbulb tiles are powered. If so, fire up a new Win event
     */
    private void checkVictory() {
        if (victoryShown) return;
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Tile tile = model.getTile(r, c);
                if ("B".equals(tile.getType()) && !simulator.isPowered(r, c)) {
                    return; // at least one lightbulb is not powered
                }
            }
        }

        if (controller != null) {
            controller.stopTimer();
        }

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Victory!");
        alert.setHeaderText("All bulbs are lit. Puzzle solved!");
        alert.setContentText("Click OK to return to the main menu.");
        alert.showAndWait();

        victoryShown = true;

        // Trigger return to the main menu
        fireEvent(new GameWinEvent());
        if (hintWindow != null) {
            hintWindow.close();
        }
    }

    /**
     * Sets the controller for this view.
     *
     * @param controller the game controller
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Update the displayed move count.
     * @param count the new move count
     */
    public void updateMoveCount(int count) {
        moveCounterLabel.setText("Moves: " + count);
    }
}