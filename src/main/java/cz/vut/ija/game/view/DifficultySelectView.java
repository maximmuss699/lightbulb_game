/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A view for selecting difficulty.
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
 * Screen for selecting game difficulty level.
 */
public class DifficultySelectView extends VBox {
    /**
     * Button for easy difficulty.
     */
    private final Button easyButton;
    /**
     * Button for medium difficulty.
     */
    private final Button mediumButton;
    /**
     * Button for hard difficulty.
     */
    private final Button hardButton;
    /**
     * Button to go back to previous screen.
     */
    private final Button backButton;

    /**
     * Creates a new difficulty select view.
     */
    public DifficultySelectView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(40));
        getStyleClass().add("settings-title");

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
        backButton = new Button("Back");
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

    /**
     * Gets the easy difficulty button.
     *
     * @return the easy button component
     */
    public Button getEasyButton() {
        return easyButton;
    }

    /**
     * Gets the medium difficulty button.
     *
     * @return the medium button component
     */
    public Button getMediumButton() {
        return mediumButton;
    }

    /**
     * Gets the hard difficulty button.
     *
     * @return the hard button component
     */
    public Button getHardButton() {
        return hardButton;
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