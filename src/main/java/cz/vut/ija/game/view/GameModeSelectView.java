package cz.vut.ija.game.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Screen for selecting game mode - either timed or not timed.
 */
public class GameModeSelectView extends VBox {

    private final Button classicModeButton;
    private final Button timedModeButton;
    private final Button backButton;

    private SettingsChangeListener changeListener;

    public GameModeSelectView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(40));
        getStyleClass().add("settings-title");

        // Title
        Label titleLabel = new Label("SELECT GAME MODE");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 30));
        titleLabel.getStyleClass().add("cyberpunk-title");

        // Classic mode
        classicModeButton = new Button("Classic");
        classicModeButton.getStyleClass().add("menu-button");
        classicModeButton.setPrefWidth(200);

        // Timed mode
        timedModeButton = new Button("Timed mode");
        timedModeButton.getStyleClass().add("menu-button");
        timedModeButton.setPrefWidth(200);

        // Add a space between the buttons
        Region spacer = new Region();
        spacer.setPrefHeight(25);

        // Back button
        backButton = new Button("Back");
        backButton.getStyleClass().add("menu-button");
        backButton.setPrefWidth(200);

        // Add components to the layout
        getChildren().addAll(
                titleLabel,
                classicModeButton,
                timedModeButton,
                spacer,
                backButton
        );
    }

    /**
     * Sets the listener for the settings changes.
     */
    public void setSettingsChangeListener(SettingsChangeListener listener) {
        this.changeListener = listener;

        // set up listeners for buttons
        classicModeButton.setOnAction(e -> {
            if (changeListener != null) {
                changeListener.onClassicModeSelected();
            }
        });

        timedModeButton.setOnAction(e -> {
            if (changeListener != null) {
                changeListener.onTimedModeSelected();
            }
        });
    }

    // Getters
    public Button getClassicModeButton() {
        return classicModeButton;
    }

    public Button getTimedModeButton() {
        return timedModeButton;
    }

    public Button getBackButton() {
        return backButton;
    }
}
