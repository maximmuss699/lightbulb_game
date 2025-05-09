package cz.vut.ija.game.view;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.GameSave;
import cz.vut.ija.game.service.GameSaveService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import java.util.function.Consumer;

/**
 * Screen for replaying a saved game.
 * Allows to select a move and continue the game from that move.
 */
public class ReplayView extends BorderPane {
    private final GameSave save;
    private final GameSaveService saveService;
    private BoardView boardView;
    private Slider moveSlider;
    private Label moveLabel;
    private Button playGameButton;
    private Button backButton;
    private int currentMoveIndex = -1;

    private Consumer<GameSave> onPlayGame;
    private Consumer<Integer> onPlayGameAtMove;
    private Runnable onBackToMenu;

    public ReplayView(GameSave save) {
        this.save = save;
        this.saveService = new GameSaveService();
        setupView();
    }

    private void setupView() {
        // Set up initial board
        GameBoard board = saveService.createBoardFromSave(save, -1);
        boardView = new BoardView(board, true);
        setCenter(boardView);

        moveLabel = new Label("Move: 0 / " + save.getMoves().size());
        moveLabel.getStyleClass().add("cyberpunk-label");

        moveSlider = new Slider(0, save.getMoves().size(), 0);
        moveSlider.setShowTickMarks(true);
        moveSlider.setShowTickLabels(true);
        moveSlider.setMajorTickUnit(1);
        moveSlider.setMinorTickCount(0);
        moveSlider.setBlockIncrement(1);
        moveSlider.setSnapToTicks(true);

        int gridSize = board.getRows();
        double cellSize = 75;
        moveSlider.setPrefWidth(gridSize * cellSize);

        moveSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int moveIndex = newVal.intValue() - 1;
            if (moveIndex != currentMoveIndex) {
                currentMoveIndex = moveIndex;
                updateBoardToMove(moveIndex);
                moveLabel.setText("Move: " + (moveIndex + 1) + " / " + save.getMoves().size());

                boolean isLastMove = moveIndex == save.getMoves().size() - 1;
                boolean isCompleted = save.isCompleted();
                playGameButton.setDisable(isLastMove && isCompleted);
            }
        });

        // Buttons
        playGameButton = new Button("Continue from this move");
        playGameButton.setDisable(save.isCompleted());
        playGameButton.getStyleClass().add("game-button");

        backButton = new Button("Back to Menu");
        backButton.getStyleClass().add("game-button");

        playGameButton.setOnAction(e -> {
            if (onPlayGameAtMove != null) {
                onPlayGameAtMove.accept(currentMoveIndex);
            }
        });

        backButton.setOnAction(e -> {
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });

        VBox controlBox = new VBox(5, moveLabel, moveSlider);
        controlBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(10, backButton, playGameButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(20, controlBox, buttonBox);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));

        setBottom(bottomBox);
    }

    private void updateBoardToMove(int moveIndex) {
        GameBoard newBoard = saveService.createBoardFromSave(save, moveIndex);
        boardView = new BoardView(newBoard, true);
        setCenter(boardView);
    }

    public void setOnPlayGame(Consumer<GameSave> callback) {
        this.onPlayGame = callback;
    }

    public void setOnPlayGameAtMove(Consumer<Integer> callback) {
        this.onPlayGameAtMove = callback;
    }

    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }
}
