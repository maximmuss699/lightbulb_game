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
 * Screen for selecting game difficulty level.
 */
public class DifficultySelectView extends VBox {

    private final Button easyButton;
    private final Button mediumButton;
    private final Button hardButton;
    private final Button backButton;

    public DifficultySelectView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(40));
        getStyleClass().add("difficulty-select");

        // Title
        Label titleLabel = new Label("SELECT DIFFICULTY");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 30));
        titleLabel.getStyleClass().add("cyberpunk-title");

        // Easy mode
        easyButton = new Button("Easy");
        easyButton.getStyleClass().add("menu-button");
        easyButton.setPrefWidth(200);

        // Medium mode
        mediumButton = new Button("Medium");
        mediumButton.getStyleClass().add("menu-button");
        mediumButton.setPrefWidth(200);

        // Hard mode
        hardButton = new Button("Hard");
        hardButton.getStyleClass().add("menu-button");
        hardButton.setPrefWidth(200);

        // Add a space between the buttons
        Region spacer = new Region();
        spacer.setPrefHeight(25);

        // Back button
        backButton = new Button("BACK");
        backButton.getStyleClass().add("menu-button");
        backButton.setPrefWidth(200);

        // Add components to the layout
        getChildren().addAll(
                titleLabel,
                easyButton,
                mediumButton,
                hardButton,
                spacer,
                backButton
        );
    }

    // Getters
    public Button getEasyButton() {
        return easyButton;
    }

    public Button getMediumButton() {
        return mediumButton;
    }

    public Button getHardButton() {
        return hardButton;
    }

    public Button getBackButton() {
        return backButton;
    }
}