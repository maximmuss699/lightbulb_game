/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A view for creating a custom game with custom parameters.
 */
package cz.vut.ija.game.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Settings screen. Used for customizing the game behavior.
 */
public class CustomGameView extends VBox {
    /**
     * Board size selector dropdown.
     */
    private final ComboBox<String> boardSizeSelector;
    /**
     * Light bulb count selector dropdown.
     */
    private final ComboBox<Integer> bulbSelector;
    /**
     * Time limit selector dropdown.
     */
    private final ComboBox<Integer> timeSelector;
    /**
     * Checkbox to enable timed mode.
     */
    private final CheckBox timedModeCheckbox;
    /**
     * Button to start a game with selected settings.
     */
    private final Button startGameButton;
    /**
     * Button to go back to the main menu.
     */
    private final Button backButton;

    /**
     * Listener for settings changes.
     */
    private SettingsChangeListener changeListener;

    /**
     * Creates a new custom game view.
     */
    public CustomGameView() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(50));
        getStyleClass().add("settings-menu");

        Label titleLabel = new Label("Game settings");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 30));
        titleLabel.getStyleClass().add("cyberpunk-title");

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(15);
        settingsGrid.setAlignment(Pos.CENTER);

        // Board size settings
        settingsGrid.add(new Label("Board size:"), 0, 0);
        boardSizeSelector = new ComboBox<>(
                FXCollections.observableArrayList(
                        "5×5", "6×6", "8×8", "10×10"
                )
        );
        boardSizeSelector.setValue("5×5"); // default
        boardSizeSelector.setOnAction(e -> {
            if (changeListener != null) {
                changeListener.onSizeChanged(boardSizeSelector.getValue());
            }
        });

        settingsGrid.add(boardSizeSelector, 1, 0);

        // Number of lightbulbs
        settingsGrid.add(new Label("Number of lightbulbs:"), 0, 1);
        bulbSelector = new ComboBox<>(
                FXCollections.observableArrayList(1, 2, 3, 4, 5)
        );
        bulbSelector.setValue(3); // default
        bulbSelector.setOnAction(e -> {
            if (changeListener != null) {
                changeListener.onBulbCountChanged(bulbSelector.getValue());
            }
        });

        settingsGrid.add(bulbSelector, 1, 1);

        // time limit checkbox
        settingsGrid.add(new Label("Timed mode:"), 0, 2);
        timedModeCheckbox = new CheckBox();
        timedModeCheckbox.setSelected(false);
        settingsGrid.add(timedModeCheckbox, 1, 2);

        // time limit value
        settingsGrid.add(new Label("Time limit [s]:"), 0, 3);
        timeSelector = new ComboBox<>(
                FXCollections.observableArrayList(30, 60, 90, 120, 180)
        );
        timeSelector.setValue(60); // default
        timeSelector.setDisable(true);
        settingsGrid.add(timeSelector, 1, 3);

        timedModeCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            timeSelector.setDisable(!newVal);
            if (changeListener != null) {
                changeListener.onTimedModeChanged(newVal);
            }
        });

        timeSelector.setOnAction(e -> {
            if (timedModeCheckbox.isSelected() && changeListener != null) {
                changeListener.onTimeLimitChanged(timeSelector.getValue());
            }
        });

        // Buttons
        startGameButton = new Button("Start");
        startGameButton.getStyleClass().add("menu-button");
        startGameButton.setPrefWidth(120);

        backButton = new Button("Back");
        backButton.getStyleClass().add("menu-button");
        backButton.setPrefWidth(120);

        HBox buttonBox = new HBox(15, backButton, startGameButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add everything to the layout
        getChildren().addAll(
                titleLabel,
                settingsGrid,
                buttonBox
        );
    }

    /**
     * Sets the settings change listener.
     *
     * @param listener the listener that will handle settings changes
     */
    public void setSettingsChangeListener(SettingsChangeListener listener) {
        this.changeListener = listener;
    }

    /**
     * Updates the UI based on the provided settings.
     *
     * @param size      board size string (e.g. "5×5")
     * @param bulbCount number of light bulbs
     * @param timedMode whether timed mode is enabled
     * @param timeLimit time limit in seconds
     */
    public void updateUI(String size, int bulbCount, boolean timedMode, int timeLimit) {
        boardSizeSelector.setValue(size);
        bulbSelector.setValue(bulbCount);
        timedModeCheckbox.setSelected(timedMode);
        timeSelector.setValue(timeLimit);
        timeSelector.setDisable(!timedMode);
    }

    /**
     * Gets the board size selector dropdown.
     *
     * @return the board size selector component
     */
    public ComboBox<String> getBoardSizeSelector() {
        return boardSizeSelector;
    }

    /**
     * Gets the light bulb count selector dropdown.
     *
     * @return the light bulb selector component
     */
    public ComboBox<Integer> getBulbSelector() {
        return bulbSelector;
    }

    /**
     * Gets the time selector dropdown.
     *
     * @return the time selector component
     */
    public ComboBox<Integer> getTimeSelector() {
        return timeSelector;
    }

    /**
     * Gets the timed mode checkbox.
     *
     * @return the timed mode checkbox component
     */
    public CheckBox getTimedModeCheckbox() {
        return timedModeCheckbox;
    }

    /**
     * Gets the start game button.
     *
     * @return the start game button component
     */
    public Button getStartGameButton() {
        return startGameButton;
    }

    /**
     * Gets the back button.
     *
     * @return the back button component
     */
    public Button getBackButton() {
        return backButton;
    }
}