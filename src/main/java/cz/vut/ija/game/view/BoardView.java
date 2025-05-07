package cz.vut.ija.game.view;

import cz.vut.ija.game.model.SourceTile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import cz.vut.ija.game.model.BoardObserver;
import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.Tile;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import cz.vut.ija.game.logic.GameSimulator;
import cz.vut.ija.game.model.Position;

public class BoardView extends GridPane implements BoardObserver {
    private final GameBoard model;
    private final GameSimulator simulator;
    private final StackPane[][] tilePanes;
    private final ImageView[][] tileImages;
    private final Button solveButton;

    private static final int TILE_SIZE = 80;

    // Images for wires
    private final Image empty_tile = new Image(getClass().getResourceAsStream("/emptytile/empty_tile.png"));
    private final Image wireI_lit = new Image(getClass().getResourceAsStream("/wires/I_lit.png"));
    private final Image wireI_unlit = new Image(getClass().getResourceAsStream("/wires/I_unlit.png"));
    private final Image wireL_lit = new Image(getClass().getResourceAsStream("/wires/L_lit.png"));
    private final Image wireL_unlit = new Image(getClass().getResourceAsStream("/wires/L_unlit.png"));
    private final Image wireT_lit = new Image(getClass().getResourceAsStream("/wires/T_lit.png"));
    private final Image wireT_unlit = new Image(getClass().getResourceAsStream("/wires/T_unlit.png"));
    private final Image wireX_lit = new Image(getClass().getResourceAsStream("/wires/X_lit.png"));
    private final Image wireX_unlit = new Image(getClass().getResourceAsStream("/wires/X_unlit.png"));
    private final Image lightbulb_lit = new Image(getClass().getResourceAsStream("/lightbulb/lightbulb_lit.png"));
    private final Image lightbulb_unlit = new Image(getClass().getResourceAsStream("/lightbulb/lightbulb_unlit.png"));
    private final Image power_node = new Image(getClass().getResourceAsStream("/powernode/power_node.png"));
    //private final Button[][] buttons;

    public BoardView(GameBoard model) {

        // Call the superclass constructor
        super();
        this.getStyleClass().add("board-view"); // Add a CSS class to the grid pane

        this.setAlignment(Pos.CENTER);

        // Spaces between tiles
        this.setHgap(5);
        this.setVgap(5);

        this.model = model;
        model.addObserver(this); // Register this view as an observer of the model

        // Allocate the tile panes matching the board dimensions
        tilePanes = new StackPane[model.getRows()][model.getCols()];
        tileImages = new ImageView[model.getRows()][model.getCols()];

        // Allocate the button grid matching the board dimensions
        //buttons = new Button[model.getRows()][model.getCols()];

        // Initialize the power simulator
        simulator = new GameSimulator(model);

        // Build the grid of buttons
        buildGrid();

        // Determine initial powered state and style tiles
        simulator.propagate();
        applyPowerStyles();

        // add Solve button
        solveButton = new Button("Solve");
        solveButton.setOnAction(e -> autoSolve());
        // place below the grid: span across all columns
        this.add(solveButton, 0, model.getRows(), model.getCols(), 1);
    }

    private void buildGrid() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                StackPane tilePane = createTilePane(r, c);
                tilePanes[r][c] = tilePane;
                add(tilePane, c, r);
            }
        }
    }

    private StackPane createTilePane(int r, int c) {
        Tile tile = model.getTile(r, c);

        StackPane tilePane = new StackPane();
        tilePane.setPrefSize(TILE_SIZE, TILE_SIZE);
        tilePane.setMinSize(TILE_SIZE, TILE_SIZE);
        tilePane.setMaxSize(TILE_SIZE, TILE_SIZE);

        ImageView tileImage = createTileImage(tile);
        tileImages[r][c] = tileImage;

        tilePane.getChildren().add(tileImage);

        // fire a MVC event
        tilePane.setOnMouseClicked(e ->
                fireEvent(new TileClickEvent(r, c))
        );

        return tilePane;
    }

    private ImageView createTileImage(Tile tile) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setPreserveRatio(true);

        // Based on whether the tile is powered or not, set the image
        updateTileImage(imageView, tile, false); // false = unpowered

        return imageView;
    }

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


    private Button createButton(int r, int c) {
        Tile tile = model.getTile(r, c);
        Button btn = new Button(tile.getType());
        btn.setPrefSize(50, 50);

        // apply the stone-like style
        btn.getStyleClass().add("tile-button");

        applyRotation(btn, tile);

        // wrap click into MVC event
        btn.setOnAction(e ->
                fireEvent(new TileClickEvent(r, c))
        );
        return btn;
    }

    @Override
    public void tileChanged(int row, int col) {
        Tile tile = model.getTile(row, col);
        tileImages[row][col].setRotate(tile.getRotation());

        // recalculate the powered state
        simulator.propagate();
        applyPowerStyles();
    }

    private void applyRotation(Button btn, Tile tile) {
        btn.getTransforms().setAll(
                new Rotate(tile.getRotation(),
                        btn.getPrefWidth() / 2,
                        btn.getPrefHeight() / 2)
        );
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
                model.setTileRotation(r, c, sol[r][c]);
            }
        }
        simulator.propagate();
        applyPowerStyles();
    }
}