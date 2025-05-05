package cz.vut.ija.game;

import cz.vut.ija.game.controller.GameController;
import cz.vut.ija.game.view.GameSettingsView;
import cz.vut.ija.game.view.MainMenuView;
import javafx.application.Application;
import javafx.application.Platform;
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
 * Sets up the JavaFX stage, scene, and core UI elements
 * and delegates game logic and view management to GameController.
 */
public class Main extends Application implements GameSettingsView.SettingsChangeListener {

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 650;

    private Scene scene;
    private BorderPane root;

    /**
     * Holds the active GameController instance responsible for coordinating between the model and the view.
     * Component MainMenuView serves as an entry point, and the settings view is used for setting up the game.
     */
    private MainMenuView mainMenu;
    private GameSettingsView settingsView;
    private GameController gameController;
    private Label moveLabel;

    // Default settings
    private String boardSize = "5×5";
    private int bulbCount = 3;
    private boolean timedModeEnabled = false;
    private int timeLimit = 60; // default value in seconds


    /**
     * JavaFX entry point method, called when the application is launched.
     *
     * @param stage the primary application window
     */
    @Override
    public void start(Stage stage) {
        // Create the root layout container: a BorderPane
        root = new BorderPane();
        root.getStyleClass().add("root"); // Apply CSS class for styling
        //root.setPadding(new Insets(10));   // Add uniform padding around the layout

        // Create the JavaFX scene with specified dimensions
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        // Attach external CSS stylesheet for consistent styling
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/styles.css"),
                        "styles.css not found"
                ).toExternalForm()
        );

        initializeComponents();
        setupEventHandlers();

        // LAUNCH THE MAIN MENU SCREEN
        showMainMenu();

        // Configure and display the primary stage
        stage.setScene(scene);
        stage.setTitle("LightBulb");
        stage.show();
    }

    /**
     * Initialize all the components.
     */
    private void initializeComponents() {
        mainMenu = new MainMenuView();
        settingsView = new GameSettingsView();
        settingsView.setSettingsChangeListener(this);
    }

    /**
     * Set the buttons to handle events.
     */
    private void setupEventHandlers() {
        // Main menu
        mainMenu.getStartGameButton().setOnAction(e -> startNewGame());
        mainMenu.getGameSettingsButton().setOnAction(e -> showSettings());
        mainMenu.getExitButton().setOnAction(e -> Platform.exit());

        // Settings
        settingsView.getBackButton().setOnAction(e -> showMainMenu());
    }

    private void showMainMenu() {
        root.setCenter(mainMenu);
    }

    private void showSettings() {
        settingsView.updateUI(boardSize, bulbCount, timedModeEnabled, timeLimit);
        root.setCenter(settingsView);
    }

    /**
     * Implementation of the interface SettingsChangeListener (listens for changes made in values)
     */
    @Override
    public void onSizeChanged(String newSize) {
        boardSize = newSize;
        System.out.println("Board size changed to: " + newSize);
    }

    @Override
    public void onBulbCountChanged(int newCount) {
        bulbCount = newCount;
        System.out.println("Lightbulb count changed to: " + newCount);
    }

    @Override
    public void onTimedModeChanged(boolean enabled, int timeLimit) {
        this.timedModeEnabled = enabled;
        this.timeLimit = timeLimit;
        System.out.println("Timed mode " + (enabled ? "enabled" : "disabled"));
    }

    @Override
    public void onTimeLimitChanged(int newTimeLimit) {
        this.timeLimit = newTimeLimit;
        System.out.println("Time limit changed to: " + newTimeLimit + "s");
    }

    // Starts a new game
    private void startNewGame() {
        // Parsování velikosti desky
        String[] dimensions = boardSize.split("×");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        // Generate puzzle
        LevelGenerator gen = new LevelGenerator(rows, cols, bulbCount);
        GameBoard puzzle = gen.generatePuzzle();

        // Initialize controller
        gameController = new GameController(puzzle);

        // Set up a new pane
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(gameController.getView());

        Button backToMenuButton = new Button("Back to main menu");
        backToMenuButton.setOnAction(e -> showMainMenu());

        HBox bottomBar = new HBox(backToMenuButton);
        bottomBar.setAlignment(javafx.geometry.Pos.CENTER);
        bottomBar.setPadding(new javafx.geometry.Insets(10));

        gamePane.setBottom(bottomBar);
        root.setCenter(gamePane);
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
