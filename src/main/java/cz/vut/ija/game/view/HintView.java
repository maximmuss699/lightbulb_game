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
    private final GameBoard model;
    private final GameSimulator simulator;
    private final StackPane[][] tilePanes;
    private final ImageView[][] tileImages;
    private final Text[][] tileHints;
    private final Button closeButton;
    private Stage stage;

    private static final int TILE_SIZE = 80;

    // Unlit images
    private final Image emptyTile = new Image(getClass().getResourceAsStream("/emptytile/empty_tile.png"));
    private final Image wireI = new Image(getClass().getResourceAsStream("/wires/I_unlit.png"));
    private final Image wireL = new Image(getClass().getResourceAsStream("/wires/L_unlit.png"));
    private final Image wireT = new Image(getClass().getResourceAsStream("/wires/T_unlit.png"));
    private final Image wireX = new Image(getClass().getResourceAsStream("/wires/X_unlit.png"));
    private final Image bulb = new Image(getClass().getResourceAsStream("/lightbulb/lightbulb_unlit.png"));
    private final Image source = new Image(getClass().getResourceAsStream("/powernode/power_node.png"));

    // Lit images
    private final Image wireI_lit = new Image(getClass().getResourceAsStream("/wires/I_lit.png"));
    private final Image wireL_lit = new Image(getClass().getResourceAsStream("/wires/L_lit.png"));
    private final Image wireT_lit = new Image(getClass().getResourceAsStream("/wires/T_lit.png"));
    private final Image wireX_lit = new Image(getClass().getResourceAsStream("/wires/X_lit.png"));
    private final Image bulb_lit  = new Image(getClass().getResourceAsStream("/lightbulb/lightbulb_lit.png"));

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

    /** Returns true if current and target rotations are effectively the same for symmetric tiles. */
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
                hint.setStyle("-fx-fill: red;");
                StackPane.setAlignment(hint, Pos.TOP_RIGHT);
                tileHints[r][c] = hint;
                pane.getChildren().add(hint);

                tilePanes[r][c] = pane;
                add(pane, c, r);
            }
        }
    }

    private void applyPowerStyles() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Tile tile = model.getTile(r, c);
                boolean powered = simulator.isPowered(r, c);
                updateTileImage(tileImages[r][c], tile, powered);
            }
        }
    }

    private void updateTileImage(ImageView imageView, Tile tile, boolean powered) {
        String type = tile.getType();
        switch (type) {
            case "I": imageView.setImage(powered ? wireI_lit : wireI); break;
            case "L": imageView.setImage(powered ? wireL_lit : wireL); break;
            case "T": imageView.setImage(powered ? wireT_lit : wireT); break;
            case "X": imageView.setImage(powered ? wireX_lit : wireX); break;
            case "S": imageView.setImage(source); break;
            case "B": imageView.setImage(powered ? bulb_lit : bulb); break;
            default:  imageView.setImage(emptyTile);
        }
        imageView.setRotate(tile.getRotation());
    }

    /** Refreshes click counts and updates tile states. */
    public void refreshHints() {
        simulator.propagate();
        applyPowerStyles();
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                Tile tile = model.getTile(r, c);
                int current = tile.getRotation();
                int target = model.getSolutionRotations()[r][c];
                int diff = (target - current + 360) % 360;
                int clicks = diff / 90;
                if (isSameOrientation(tile, current, target)) {
                    clicks = 0;
                }
                tileHints[r][c].setText("↻" + clicks);
            }
        }
    }

    /** Shows or re-shows the hint preview window, refreshing its contents. */
    public void show() {
        // Refresh tile states and hint counts before showing
        refreshHints();

        // If stage doesn't exist or was closed, create a new one
        if (stage == null || !stage.isShowing()) {
            stage = new Stage();
            stage.setTitle("Hint Preview");
            stage.setScene(new Scene(this));
        }
        // Show and bring to front
        stage.show();
        stage.toFront();
    }
    public void close() {
        if (stage != null) {
            stage.close();
        }
    }
}
