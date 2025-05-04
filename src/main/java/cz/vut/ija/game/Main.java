package cz.vut.ija.game;

import cz.vut.ija.game.controller.GameController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main class for the LightBulb Game.
 * Launches JavaFX, builds UI controls, and delegates to GameController.
 */
public class Main extends Application {

    /** Holds the current GameController which manages model & view. */
    private GameController controller;

    /**
     * JavaFX entry point.
     * Called after Application.launch().
     *
     * @param stage the primary window (Stage) for this application
     */
    @Override
    public void start(Stage stage) {
        // Root layout: a BorderPane to place controls at top, center, etc.
        BorderPane root = new BorderPane();
        // Add padding around the edges of the root pane
        root.setPadding(new Insets(10));

        // Create a dropdown (ComboBox) for selecting board size
        ComboBox<String> sizeBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        "5×5",   // small board
                        "6×6",   // medium board
                        "8×8",   // larger board
                        "10×10"  // extra-large board
                )
        );
        // Set default selected value
        sizeBox.setValue("5×5");

        // When the user selects a new size:
        sizeBox.setOnAction(e -> {
            // Split the "NxM" string into ["N","M"]
            String[] parts = sizeBox.getValue().split("×");
            int rows = Integer.parseInt(parts[0]); // number of rows
            int cols = Integer.parseInt(parts[1]); // number of columns

            // Create a new GameController with the selected dimensions
            controller = new GameController(rows, cols);
            // Ask the controller for its view and place it in the center of the BorderPane
            root.setCenter(controller.getView());
        });

        // Place the size selector at the top of the window
        root.setTop(sizeBox);

        // Trigger the action once to initialize the board on startup
        sizeBox.getOnAction().handle(null);

        // Create the main scene, giving it the root layout and initial dimensions
        Scene scene = new Scene(root, 500, 550);

        // Attach the scene to the stage (window)
        stage.setScene(scene);
        // Set the window title
        stage.setTitle("LightBulb Game (MVC)");
        // Show the window
        stage.show();
    }

    /**
     * Standard main method to launch the JavaFX application.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // Launches the JavaFX runtime, which eventually calls start(Stage)
        launch(args);
    }
}