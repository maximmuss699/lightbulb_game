/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A view for selecting game mode - classic or timed mode.
 */
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
    /**
     * Button to select classic (untimed) mode.
     */
    private final Button classicModeButton;
    /**
     * Button to select timed mode.
     */
    private final Button timedModeButton;
    /**
     * Button to go back to previous screen.
     */
    private final Button backButton;

    /**
     * Listener for settings changes.
     */
    private SettingsChangeListener changeListener;

    /**
     * Creates a new game mode selection view.
     */
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
     * Sets the settings change listener.
     *
     * @param listener the listener that will handle settings changes
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

    /**
     * Gets the classic mode button.
     *
     * @return the classic mode button component
     */
    public Button getClassicModeButton() {
        return classicModeButton;
    }

    /**
     * Gets the timed mode button.
     *
     * @return the timed mode button component
     */
    public Button getTimedModeButton() {
        return timedModeButton;
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
