package cz.vut.ija.game;

import cz.vut.ija.game.controller.GameController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import cz.vut.ija.game.generator.LevelGenerator;
import cz.vut.ija.game.model.GameBoard;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * Main application class for the LightBulb Puzzle Game.
 * Sets up the JavaFX stage, scene, and core UI elements,
 * and delegates game logic and view management to GameController.
 */
public class Main extends Application {

    /**
     * Holds the active GameController instance responsible
     * for coordinating between the model and the view.
     */
    private GameController controller;
    private Label moveLabel;

    /**
     * JavaFX entry point method, called when the application is launched.
     *
     * @param stage the primary application window
     */
    @Override
    public void start(Stage stage) {
        // Create the root layout container: a BorderPane
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root"); // Apply CSS class for styling
        root.setPadding(new Insets(10));   // Add uniform padding around the layout

        // Initialize a ComboBox for board size selection with preset options
        ComboBox<String> sizeSelector = new ComboBox<>(
                FXCollections.observableArrayList(
                        "5×5",    // Small board
                        "6×6",    // Medium board
                        "8×8",    // Large board
                        "10×10"   // Extra-large board
                )
        );
        sizeSelector.setValue("5×5"); // Default to 5×5 on startup

        // Register an event handler to rebuild the game board
        // whenever the user selects a different size
        sizeSelector.setOnAction(event -> {
            String[] dimensions = sizeSelector.getValue().split("×");
            int rows = Integer.parseInt(dimensions[0]); // Parse row count
            int cols = Integer.parseInt(dimensions[1]); // Parse column count

            // Create and initialize the GameController with new dimensions
            controller = new GameController(rows, cols);
            // Place the controller's view into the center of the layout
            root.setCenter(controller.getView());
        });

        // Bulb-count selector
        ComboBox<Integer> bulbBox = new ComboBox<>(FXCollections.observableArrayList(1,2,3,4,5));
        bulbBox.setValue(3);

        // Move counter label
        moveLabel = new Label("Moves: 0");

        // New Game button
        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(e -> {
            String[] dims = sizeSelector.getValue().split("×");
            int rows = Integer.parseInt(dims[0]);
            int cols = Integer.parseInt(dims[1]);
            int bulbs = bulbBox.getValue();

            // Generate puzzle and initialize controller
            LevelGenerator gen = new LevelGenerator(rows, cols, bulbs);
            GameBoard puzzle = gen.generatePuzzle();
            controller = new GameController(puzzle);
            controller.setOnMoveUpdated(count -> moveLabel.setText("Moves: " + count));

            // Display the new puzzle
            root.setCenter(controller.getView());
        });

        // Top controls: size, bulb count, new game
        HBox topBar = new HBox(10,
            new Label("Size:"), sizeSelector,
            new Label("Bulbs:"), bulbBox,
            newGameBtn
        );
        topBar.setAlignment(Pos.CENTER);
        root.setTop(topBar);

        // Launch initial game
        newGameBtn.fire();

        // Create the JavaFX scene with specified dimensions
        Scene scene = new Scene(root, 600, 650);
        // Attach external CSS stylesheet for consistent styling
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles.css"),
                        "styles.css not found"
                ).toExternalForm()
        );

        // Configure and display the primary stage
        stage.setScene(scene);
        stage.setTitle("LightBulb Game (MVC)");
        stage.show();
    }

    /**
     * Standard Java main method that launches the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args); // Bootstraps the JavaFX runtime
    }
}
