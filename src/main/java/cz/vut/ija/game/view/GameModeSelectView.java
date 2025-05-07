package cz.vut.ija.game.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


public class GameModeSelectView extends VBox {

    /**
     * Screen for selecting game mode - either timed or not timed.
     */
    private final Button classicModeButton;
    private final Button timedModeButton;
    private final Button backButton;

    private SettingsChangeListener changeListener;

    public GameModeSelectView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(40));
        getStyleClass().add("difficulty-select");

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

        // Mezera místo separátoru
        Region spacer = new Region();
        spacer.setPrefHeight(25); // Nastavení výšky mezery

        // Back button
        backButton = new Button("BACK");
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
