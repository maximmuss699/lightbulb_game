package cz.vut.ija.game.view;

import cz.vut.ija.game.model.BoardObserver;
import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.Tile;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;

public class BoardView extends GridPane implements BoardObserver {
    private final GameBoard model;
    private final Button[][] buttons;

    public BoardView(GameBoard model) {
        this.model = model;
        model.addObserver(this);

        int rows = model.getRows(), cols = model.getCols();
        buttons = new Button[rows][cols];
        setHgap(2); setVgap(2); setAlignment(Pos.CENTER);
        buildGrid();
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
    }

    private void applyRotation(Button btn, Tile tile) {
        btn.getTransforms().setAll(
                new Rotate(tile.getRotation(),
                        btn.getPrefWidth()/2,
                        btn.getPrefHeight()/2)
        );
    }
}