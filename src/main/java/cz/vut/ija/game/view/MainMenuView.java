/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Main menu view. The app opens this view first.
 */
package cz.vut.ija.game.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The main menu screen. Used for entering settings, starting a new game, loading a game (replay) or exitting the app.
 */
public class MainMenuView extends VBox {
    /**
     * Button for starting a new game.
     */
    private final Button startGameButton;
    /**
     * Button for replaying saved games.
     */
    private final Button replayButton;
    /**
     * Button for starting a custom game.
     */
    private final Button customButton;
    /**
     * Button for exiting the application.
     */
    private final Button exitButton;

    /**
     * Creates a new main menu view.
     */
    public MainMenuView() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(50));
        getStyleClass().add("main-menu");

        // game title
        Label titleLabel = new Label("LightBulb");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 36));
        titleLabel.getStyleClass().add("cyberpunk-title");

        // start game
        startGameButton = new Button("Play");
        startGameButton.getStyleClass().add("menu-button");
        startGameButton.setPrefWidth(200);

        // replay button
        replayButton = new Button("Replay");
        replayButton.getStyleClass().add("menu-button");
        replayButton.setPrefWidth(200);

        // Custom settings button
        customButton = new Button("Custom game");
        customButton.getStyleClass().add("menu-button");
        customButton.setPrefWidth(200);

        // exit
        exitButton = new Button("Exit");
        exitButton.getStyleClass().add("menu-button");
        exitButton.setPrefWidth(200);

        // add everything to the layout
        getChildren().addAll(
                titleLabel,
                startGameButton,
                replayButton,
                customButton,
                exitButton
        );
    }

    /**
     * Gets the start game button.
     *
     * @return the start game button
     */
    public Button getStartGameButton() {
        return startGameButton;
    }

    /**
     * Gets the replay button.
     *
     * @return the replay button
     */
    public Button getReplayButton() {
        return replayButton;
    }

    /**
     * Gets the custom game button.
     *
     * @return the custom game button
     */
    public Button getCustomButton() {
        return customButton;
    }

    /**
     * Gets the exit button.
     *
     * @return the exit button
     */
    public Button getExitButton() {
        return exitButton;
    }
}