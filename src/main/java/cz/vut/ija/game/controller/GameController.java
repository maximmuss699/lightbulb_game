package cz.vut.ija.game.controller;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.view.BoardView;
import cz.vut.ija.game.view.TileClickEvent;

/**
 * Controller that glues the GameBoard (model) and BoardView (view) together.
 */
public class GameController {
    private final GameBoard model;
    private final BoardView view;

    /**
     * Constructs a controller for a board of the given size.
     *
     * @param rows number of rows
     * @param cols number of columns
     */
    public GameController(int rows, int cols) {
        // Initialize model and view
        model = new GameBoard(rows, cols);
        view  = new BoardView(model);

        // Listen for click‐events in the view and rotate the model tile
        view.addEventHandler(
                TileClickEvent.TILE_CLICK,
                // Lambda instead of anonymous class:
                evt -> model.rotateTile(evt.getRow(), evt.getCol())
        );
    }

    /**
     * @return the view managed by this controller
     */
    public BoardView getView() {
        return view;
    }
}