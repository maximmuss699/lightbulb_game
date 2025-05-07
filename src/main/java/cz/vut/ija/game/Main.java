package cz.vut.ija.game;

import cz.vut.ija.game.controller.GameController;
import cz.vut.ija.game.view.DifficultySelectView;
import cz.vut.ija.game.view.CustomGameView;
import cz.vut.ija.game.view.MainMenuView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
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
public class Main extends Application implements CustomGameView.SettingsChangeListener {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 1000;

    private Scene scene;
    private BorderPane root;

    /**
     * Holds the active GameController instance responsible for coordinating between the model and the view.
     * Component MainMenuView serves as an entry point, and the settings view is used for setting up the game.
     */
    private MainMenuView mainMenu;
    private DifficultySelectView difficultyView;
    private CustomGameView settingsView;
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
        difficultyView = new DifficultySelectView();
        settingsView = new CustomGameView();
        settingsView.setSettingsChangeListener(this);
    }

    /**
     * Set the buttons to handle events.
     */
    private void setupEventHandlers() {
        // Main menu
        mainMenu.getStartGameButton().setOnAction(e -> showDifficultySelect());
        mainMenu.getCustomButton().setOnAction(e -> showSettings());
        mainMenu.getExitButton().setOnAction(e -> Platform.exit());

        // Difficulty select screen
        difficultyView.getEasyButton().setOnAction(e -> startGameWithDifficulty("easy"));
        difficultyView.getMediumButton().setOnAction(e -> startGameWithDifficulty("medium"));
        difficultyView.getHardButton().setOnAction(e -> startGameWithDifficulty("hard"));
        difficultyView.getBackButton().setOnAction(e -> showMainMenu());

        // Custom settings
        settingsView.getStartGameButton().setOnAction(e -> startNewGame());
        settingsView.getBackButton().setOnAction(e -> showMainMenu());
    }

    private void showMainMenu() {
        root.setCenter(mainMenu);
    }

    /**
     * Show the difficulty selection screen
     */
    private void showDifficultySelect() {
        root.setCenter(difficultyView);
    }

    private void showSettings() {
        settingsView.updateUI(boardSize, bulbCount, timedModeEnabled, timeLimit);
        root.setCenter(settingsView);
    }

    /**
     * Start a new game with predefined settings based on difficulty level
     */
    private void startGameWithDifficulty(String difficulty) {
        switch(difficulty) {
            case "easy":
                boardSize = "5×5";
                bulbCount = 2;
                timedModeEnabled = false;
                break;
            case "medium":
                boardSize = "8×8";
                bulbCount = 4;
                timedModeEnabled = false;
                break;
            case "hard":
                boardSize = "10×10";
                bulbCount = 5;
                timedModeEnabled = false;
                break;
        }

        startNewGame();
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
        System.out.println("========== STARTING NEW GAME ==========");
        System.out.println("Board size: " + boardSize);
        System.out.println("Lightbulb count: " + bulbCount);

        if (timedModeEnabled) {
            System.out.println("Timed mode: ENABLED, time limit: " + timeLimit + " seconds");
        } else {
            System.out.println("Timed mode: DISABLED");
        }
        System.out.println("=======================================");

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
