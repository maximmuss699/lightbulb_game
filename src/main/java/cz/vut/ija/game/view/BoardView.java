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
import javafx.scene.transform.Rotate;
import cz.vut.ija.game.logic.GameSimulator;
import cz.vut.ija.game.model.Position;

public class BoardView extends GridPane implements BoardObserver {
    private final GameBoard model;
    private final GameSimulator simulator;
    private final Button[][] buttons;

    public BoardView(GameBoard model) {

        // Call the superclass constructor
        super();
        this.getStyleClass().add("board-view"); // Add a CSS class to the grid pane

        this.model = model;
        model.addObserver(this); // Register this view as an observer of the model

        // Allocate the button grid matching the board dimensions
        buttons = new Button[model.getRows()][model.getCols()];

        // Initialize the power simulator
        simulator = new GameSimulator(model);

        // Build the grid of buttons
        buildGrid();

        // Determine initial powered state and style tiles
        simulator.propagate();
        applyPowerStyles();
    }

    private void buildGrid() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Button btn = createButton(r, c);
                buttons[r][c] = btn;
                add(btn, c, r);
            }
        }
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
        applyRotation(buttons[row][col], tile);
        simulator.propagate();
        applyPowerStyles();
    }

    private void applyRotation(Button btn, Tile tile) {
        btn.getTransforms().setAll(
                new Rotate(tile.getRotation(),
                        btn.getPrefWidth()/2,
                        btn.getPrefHeight()/2)
        );
    }

    /**
     * Updates each button's style class based on whether it's powered.
     */
    private void applyPowerStyles() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Button btn = buttons[r][c];
                // Remove old power classes
                btn.getStyleClass().removeAll("tile-powered", "tile-unpowered");
                // Add the appropriate class
                String cls = simulator.isPowered(r, c) ? "tile-powered" : "tile-unpowered";
                btn.getStyleClass().add(cls);
            }
        }
    }
}