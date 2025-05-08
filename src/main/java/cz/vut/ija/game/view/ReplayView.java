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
        setPadding(new Insets(20));

        Label titleLabel = new Label("Replay Game");
        titleLabel.getStyleClass().add("title-label");

        // Information about the game
        Label gameInfoLabel = new Label(String.format(
                "Board: %s, Bulbs: %d, Moves: %d, Status: %s",
                save.getBoardSize(),
                save.getBulbCount(),
                save.getMoves().size(),
                save.isCompleted() ? "Completed" : "In Progress"
        ));

        VBox topBox = new VBox(10, titleLabel, gameInfoLabel);
        topBox.setAlignment(Pos.CENTER);
        setTop(topBox);
        BorderPane.setMargin(topBox, new Insets(0, 0, 20, 0));

        // Nastavení počáteční desky
        GameBoard board = saveService.createBoardFromSave(save, -1);
        boardView = new BoardView(board);
        setCenter(boardView);

        // Ovládací prvky
        moveLabel = new Label("Move: 0 / " + save.getMoves().size());

        moveSlider = new Slider(0, save.getMoves().size(), 0);
        moveSlider.setShowTickMarks(true);
        moveSlider.setShowTickLabels(true);
        moveSlider.setMajorTickUnit(Math.max(1, save.getMoves().size() / 10));
        moveSlider.setMinorTickCount(1);
        moveSlider.setSnapToTicks(true);

        // Sledování změn slideru
        moveSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int moveIndex = newVal.intValue() - 1; // -1 pro počáteční stav
            if (moveIndex != currentMoveIndex) {
                currentMoveIndex = moveIndex;
                updateBoardToMove(moveIndex);
                moveLabel.setText("Move: " + (moveIndex + 1) + " / " + save.getMoves().size());

                // Aktivovat tlačítko Play Game, pokud není na konci dokončené hry
                boolean isLastMove = moveIndex == save.getMoves().size() - 1;
                boolean isCompleted = save.isCompleted();
                playGameButton.setDisable(isLastMove && isCompleted);
            }
        });

        // Tlačítka
        playGameButton = new Button("Continue from this Move");
        playGameButton.setDisable(save.isCompleted()); // Zakázat pro dokončené hry
        backButton = new Button("Back to Menu");

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

        HBox controlBox = new HBox(10, moveLabel, moveSlider);
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
        boardView = new BoardView(newBoard);
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
