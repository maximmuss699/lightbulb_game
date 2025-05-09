/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Replay view used for replaying a saved game. The user can check the moves he made.
 */
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
    /**
     * The game save being replayed.
     */
    private final GameSave save;
    /**
     * Service for loading saved games.
     */
    private final GameSaveService saveService;
    /**
     * View displaying the board state.
     */
    private BoardView boardView;
    /**
     * Slider for controlling which move to display.
     */
    private Slider moveSlider;
    /**
     * Label showing current move information.
     */
    private Label moveLabel;
    /**
     * Button to start playing from current move.
     */
    private Button playGameButton;
    /**
     * Button to go back to main menu.
     */
    private Button backButton;
    /**
     * Index of the current move being displayed.
     */
    private int currentMoveIndex = -1;

    /**
     * Callback for when user wants to play from beginning.
     */
    private Consumer<GameSave> onPlayGame;
    /**
     * Callback for when user wants to play from current move.
     */
    private Consumer<Integer> onPlayGameAtMove;
    /**
     * Callback for when user wants to return to menu.
     */
    private Runnable onBackToMenu;

    /**
     * Creates a new replay view for the given save.
     *
     * @param save the game save to replay
     */
    public ReplayView(GameSave save) {
        this.save = save;
        this.saveService = new GameSaveService();
        setupView();
    }

    /**
     * Sets up the replay view UI components.
     */
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

    /**
     * Updates the board to show the state at the given move.
     *
     * @param moveIndex index of the move to display
     */
    private void updateBoardToMove(int moveIndex) {
        GameBoard newBoard = saveService.createBoardFromSave(save, moveIndex);
        boardView = new BoardView(newBoard, true);
        setCenter(boardView);
    }

    /**
     * Sets the callback for playing game from beginning.
     *
     * @param callback function to call with save
     */
    public void setOnPlayGame(Consumer<GameSave> callback) {
        this.onPlayGame = callback;
    }

    /**
     * Sets the callback for playing game from current move.
     *
     * @param callback function to call with move index
     */
    public void setOnPlayGameAtMove(Consumer<Integer> callback) {
        this.onPlayGameAtMove = callback;
    }

    /**
     * Sets the callback for returning to main menu.
     *
     * @param callback function to call
     */
    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }
}
