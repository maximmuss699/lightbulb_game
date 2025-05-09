/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A view used for selecting a replay of a game.
 */
package cz.vut.ija.game.view;

import cz.vut.ija.game.model.GameSave;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Screen for selecting saved game to load.
 */
public class LoadGameView extends BorderPane {
    /**
     * List view displaying available saves.
     */
    private ListView<GameSave> saveListView;
    /**
     * Button to load a selected game.
     */
    private Button loadButton;
    /**
     * Button to go back to the previous screen.
     */
    private Button backButton;
    /**
     * Callback for when a save is selected.
     */
    private Consumer<GameSave> onSaveSelected;

    /**
     * Creates a new load game view.
     *
     * @param saves list of available game saves
     */
    public LoadGameView(List<GameSave> saves) {
        setPadding(new Insets(20));

        Label titleLabel = new Label("Select Saved Game");
        titleLabel.getStyleClass().add("cyberpunk-title");

        setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        // A list of saved games
        saveListView = new ListView<>();
        saveListView.setCellFactory(param -> new javafx.scene.control.ListCell<GameSave>() {
            @Override
            protected void updateItem(GameSave save, boolean empty) {
                super.updateItem(save, empty);
                if (empty || save == null) {
                    setText(null);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = sdf.format(save.getSaveDate());
                    String boardSize = String.valueOf(save.getBoardSize());
                    String status = save.isCompleted() ? "Completed" : "In Progress";
                    String moves = save.getMoves().size() + " moves";

                    // separate text elements for each part of the save info and set different colors
                    Text tDate = new Text(date + " - ");
                    tDate.setStyle("-fx-fill: white;");

                    Text tBoard = new Text(boardSize + " - ");
                    tBoard.setStyle("-fx-fill: #00eaff;");

                    Text tStatus = new Text(status + " - ");
                    tStatus.setStyle("-fx-fill: #ff4ecd;");

                    Text tMoves = new Text(moves);
                    tMoves.setStyle("-fx-fill: #ffffff;");

                    TextFlow flow = new TextFlow(tDate, tBoard, tStatus, tMoves);
                    setGraphic(flow);
                    setText(null);
                }
            }
        });
        saveListView.getItems().addAll(saves);
        saveListView.getStyleClass().add("save-list");
        setCenter(saveListView);

        // Load and Back buttons
        loadButton = new Button("Load");
        loadButton.getStyleClass().add("menu-button");
        loadButton.setDisable(true);

        backButton = new Button("Back");
        backButton.getStyleClass().add("menu-button");

        HBox buttonBox = new HBox(10, backButton, loadButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        setBottom(buttonBox);

        // set up listeners for buttons
        saveListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadButton.setDisable(newVal == null);
        });

        loadButton.setOnAction(e -> {
            if (onSaveSelected != null) {
                GameSave selected = saveListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    onSaveSelected.accept(selected);
                }
            }
        });
    }

    /**
     * Sets the callback for when a save is selected.
     *
     * @param callback function to call with selected save
     */
    public void setOnSaveSelected(Consumer<GameSave> callback) {
        this.onSaveSelected = callback;
    }

    /**
     * Gets the back button.
     *
     * @return the back button
     */
    public Button getBackButton() {
        return backButton;
    }
}