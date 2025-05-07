package cz.vut.ija.game.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The main menu screen. Used for entering settings, starting a new game or exitting the app.
 */
public class MainMenuView extends VBox {

    private final Button startGameButton;
    private final Button customButton;
    private final Button exitButton;

    public MainMenuView() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(50));
        getStyleClass().add("main-menu");

        // game title
        Label titleLabel = new Label("LightBulb");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.BOLD, 36));
        titleLabel.getStyleClass().add("menu-title");

        // start game
        startGameButton = new Button("Play");
        startGameButton.getStyleClass().add("menu-button");
        startGameButton.setPrefWidth(200);

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
                customButton,
                exitButton
        );
    }

    public Button getStartGameButton() {
        return startGameButton;
    }

    public Button getCustomButton() {
        return customButton;
    }

    public Button getExitButton() {
        return exitButton;
    }
}