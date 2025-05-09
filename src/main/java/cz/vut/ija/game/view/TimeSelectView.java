/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A view used for selecting an amount of time in timed mode.
 */
package cz.vut.ija.game.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Region;

/**
 * Screen for selecting the amount of time in timed mode.
 */
public class TimeSelectView extends VBox {
    /**
     * Dropdown for selecting time limit.
     */
    private final ComboBox<Integer> timeLimit;
    /**
     * Button to continue with current settings.
     */
    private final Button continueButton;
    /**
     * Button to go back to previous screen.
     */
    private final Button backButton;

    /**
     * Listener for settings changes.
     */
    private SettingsChangeListener changeListener;

    /**
     * Creates a new time selection view.
     */
    public TimeSelectView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(40));

        // Title
        Label titleLabel = new Label("SELECT TIME");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 30));
        titleLabel.getStyleClass().add("cyberpunk-title");

        // Time limit selector
        Label timeLabel = new Label("Time limit (seconds):");
        timeLabel.getStyleClass().add("cyberpunk-label");
        timeLimit = new ComboBox<>(
                FXCollections.observableArrayList(30, 60, 90, 120, 180)
        );
        timeLimit.getStyleClass().add("cyberpunk-combo");
        timeLimit.setValue(60); // default
        timeLimit.setOnAction(e -> {
            if (changeListener != null) {
                changeListener.onTimeLimitChanged(timeLimit.getValue());
            }
        });

        // continue to screen with game difficulty
        continueButton = new Button("Continue");
        continueButton.getStyleClass().add("menu-button");
        continueButton.setPrefWidth(200);

        // Back button
        backButton = new Button("Back");
        backButton.getStyleClass().add("menu-button");
        backButton.setPrefWidth(200);

        Region spacer = new Region();
        spacer.setPrefHeight(25);

        HBox buttonBox = new HBox(15, backButton, continueButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add components to the layout
        getChildren().addAll(
                titleLabel,
                timeLabel,
                spacer,
                timeLimit,
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
     * Gets the time limit selector component.
     *
     * @return the time limit selector component
     */
    public ComboBox<Integer> getTimeLimit() {
        return timeLimit;
    }

    /**
     * Gets the continue button.
     *
     * @return the continue button component
     */
    public Button getContinueButton() {
        return continueButton;
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

