package cz.vut.ija.game.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
public class GameSettingsView extends VBox {
    
    private final ComboBox<String> boardSizeSelector;
    private final ComboBox<Integer> bulbSelector;
    private final Button backButton;

    // Interface for checking changed options in settings
    public interface SettingsChangeListener {
        void onSizeChanged(String newSize);
        void onBulbCountChanged(int newCount);
    }

    private SettingsChangeListener changeListener;

    public GameSettingsView() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(50));
        getStyleClass().add("settings-menu");

        Label titleLabel = new Label("Game settings");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 30));
        titleLabel.getStyleClass().add("settings-title");

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
        
        // Buttons
        backButton = new Button("Back");
        backButton.getStyleClass().add("menu-button");

        HBox buttonBox = new HBox(15, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        // Add everything to the layout
        getChildren().addAll(
            titleLabel,
            settingsGrid,
            buttonBox
        );
    }

    /**
     * Game settings listener setter.
     */
    public void setSettingsChangeListener(SettingsChangeListener listener) {
        this.changeListener = listener;
    }

    /**
     * Updates the UI based on the passed values.
     */
    public void updateUI(String size, int bulbCount) {
        boardSizeSelector.setValue(size);
        bulbSelector.setValue(bulbCount);
    }

    public ComboBox<String> getBoardSizeSelector() {
        return boardSizeSelector;
    }
    
    public ComboBox<Integer> getBulbSelector() {
        return bulbSelector;
    }
    
    public Button getBackButton() {
        return backButton;
    }
}