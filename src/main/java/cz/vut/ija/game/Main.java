package cz.vut.ija.game;

import cz.vut.ija.game.controller.GameController;
import cz.vut.ija.game.model.GameSave;
import cz.vut.ija.game.service.GameSaveManager;
import cz.vut.ija.game.service.GameSaveService;
import cz.vut.ija.game.view.*;
import cz.vut.ija.game.view.HintView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import cz.vut.ija.game.generator.LevelGenerator;
import cz.vut.ija.game.model.GameBoard;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

import static cz.vut.ija.game.view.GameWinEvent.GAME_WIN;

/**
 * Main application class for the LightBulb Puzzle Game.
 * Sets up the JavaFX stage, scene, and core UI elements
 * and delegates game logic and view management to GameController.
 */
public class Main extends Application implements SettingsChangeListener {

    // Resolution of the window
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 1000;

    private Scene scene;
    private BorderPane root;

    // All the views
    private MainMenuView mainMenuView;
    private GameModeSelectView gameModeView;
    private DifficultySelectView difficultyView;
    private TimeSelectView timeSelectView;
    private CustomGameView customGameView;
    private HintView hintView;

    /**
     * Holds the active GameController instance responsible for coordinating between the model and the view.
     */
    private GameController gameController;
    private GameSaveManager saveManager;
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
        mainMenuView = new MainMenuView();
        gameModeView = new GameModeSelectView();
        gameModeView.setSettingsChangeListener(this);
        difficultyView = new DifficultySelectView();
        timeSelectView = new TimeSelectView();
        timeSelectView.setSettingsChangeListener(this);
        customGameView = new CustomGameView();
        customGameView.setSettingsChangeListener(this);
    }

    /**
     * Set the buttons to handle events.
     */
    private void setupEventHandlers() {
        // Main menu
        mainMenuView.getStartGameButton().setOnAction(e -> showGameModeSelect());
        mainMenuView.getReplayButton().setOnAction(e -> showSavedGames());
        mainMenuView.getCustomButton().setOnAction(e -> showSettings());
        mainMenuView.getExitButton().setOnAction(e -> Platform.exit());

        gameModeView.getBackButton().setOnAction(e -> showMainMenu());

        // Time select screen
        timeSelectView.getContinueButton().setOnAction(e -> showDifficultySelect());
        timeSelectView.getBackButton().setOnAction(e -> showGameModeSelect());

        // Difficulty select screen
        difficultyView.getEasyButton().setOnAction(e -> startGameWithDifficulty("easy"));
        difficultyView.getMediumButton().setOnAction(e -> startGameWithDifficulty("medium"));
        difficultyView.getHardButton().setOnAction(e -> startGameWithDifficulty("hard"));
        difficultyView.getBackButton().setOnAction(e -> showGameModeSelect());

        // Custom settings
        customGameView.getStartGameButton().setOnAction(e -> startNewGame());
        customGameView.getBackButton().setOnAction(e -> showMainMenu());
    }

    private void showMainMenu() {
        if (gameController != null) {
            gameController.stopTimer();
        }
        root.setCenter(mainMenuView);
    }

    private void showGameModeSelect() {
        root.setCenter(gameModeView);
    }

    private void showDifficultySelect() {
        root.setCenter(difficultyView);
    }

    private void showTimeSelect() {
        root.setCenter(timeSelectView);
    }

    private void showSettings() {
        customGameView.updateUI(boardSize, bulbCount, timedModeEnabled, timeLimit);
        root.setCenter(customGameView);
    }

    private void showSavedGames() {
        GameSaveService saveService = new GameSaveService();

        // Create a list of all saved games
        List<GameSave> saves = saveService.getAllSaves();

        // If no saved games exist, show an alert and return
        if (saves.isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("No Saved Games");
            alert.setHeaderText(null);
            alert.setContentText("There are no saved games to replay.");
            alert.showAndWait();
            return;
        }

        saves.sort((save1, save2) -> {
            return save2.getSaveDate().compareTo(save1.getSaveDate());
        });


        // Create a view for selecting a saved game to replay
        LoadGameView loadGameView = new LoadGameView(saves);
        loadGameView.setOnSaveSelected(save -> showReplay(save));
        loadGameView.getBackButton().setOnAction(e -> showMainMenu());

        root.setCenter(loadGameView);
    }

    /**
     * Show a replay of a saved game.
     *
     * @param save
     */
    private void showReplay(GameSave save) {
        ReplayView replayView = new ReplayView(save);

        // Set up event handlers for replay buttons
        replayView.setOnPlayGameAtMove(moveIndex -> continueGameFromMove(save, moveIndex));
        replayView.setOnBackToMenu(() -> showMainMenu());

        root.setCenter(replayView);
    }

    private void continueGameFromMove(GameSave save, int moveIndex) {
        // Creates a new GameBoard from the save and replays the game from the given move index
        GameSaveService saveService = new GameSaveService();
        GameBoard board = saveService.createBoardFromSave(save, moveIndex);

        gameController = new GameController(board, false, 0);
        saveManager = new GameSaveManager(board, save.getBoardSize(), save.getBulbCount());
        gameController.setSaveManager(saveManager);

        // Record all the moves up to the given index
        List<GameSave.GameMove> movesToReplay = save.getMoves().subList(0, moveIndex + 1);
        for (GameSave.GameMove move : movesToReplay) {
            saveManager.recordMove(move.getRow(), move.getCol(), move.getOldRotation(), move.getNewRotation());
        }

        // Create a new pane for the game controller
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(gameController.getView());

        // Create a button that will save the game and mark it as unfinished
        Button backToMenuButton = new Button("Back to Menu");
        backToMenuButton.setOnAction(e -> {
            if (saveManager != null) {
                saveManager.saveGame(false);
            }
            showMainMenu();
        });
        backToMenuButton.getStyleClass().add("game-button");

        HBox bottomBar = new HBox(10, backToMenuButton);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));

        gamePane.setBottom(bottomBar);

        // An event handler that will save the game and mark it as won (finished)
        gameController.getView().addEventHandler(GAME_WIN, e -> {
            if (saveManager != null) {
                saveManager.saveGame(true);
            }
            showMainMenu();
        });

        root.setCenter(gamePane);
    }


    /**
     * Start a new game with predefined settings based on difficulty level
     */
    private void startGameWithDifficulty(String difficulty) {
        switch (difficulty) {
            case "easy":
                boardSize = "5×5";
                bulbCount = 2;
                break;
            case "medium":
                boardSize = "8×8";
                bulbCount = 4;
                break;
            case "hard":
                boardSize = "10×10";
                bulbCount = 5;
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
    public void onTimedModeChanged(boolean enabled) {
        this.timedModeEnabled = enabled;
        System.out.println("Timed mode " + (enabled ? "enabled" : "disabled"));
    }

    @Override
    public void onTimeLimitChanged(int newTimeLimit) {
        this.timeLimit = newTimeLimit;
        System.out.println("Time limit changed to: " + newTimeLimit + "s");
    }

    @Override
    public void onClassicModeSelected() {
        this.timedModeEnabled = false;
        showDifficultySelect();
    }

    @Override
    public void onTimedModeSelected() {
        this.timedModeEnabled = true;
        showTimeSelect();
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

        // Parse the board size
        String[] dimensions = boardSize.split("×");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        // Generate puzzle
        LevelGenerator gen = new LevelGenerator(rows, cols, bulbCount);
        GameBoard puzzle = gen.generatePuzzle();

        // Initialize controller
        gameController = new GameController(puzzle, timedModeEnabled, timeLimit);

        saveManager = new GameSaveManager(puzzle, boardSize, bulbCount);
        gameController.setSaveManager(saveManager);

        // Set up a new pane
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(gameController.getView());

        // save the game when going back to the main menu
        Button backToMenuButton = new Button("Back to main menu");
        backToMenuButton.setOnAction(event -> {
            gameController.stopTimer();
            if (saveManager != null) {
                saveManager.saveGame(false); // Mark as unfinished
            }
            showMainMenu();
        });
        backToMenuButton.getStyleClass().add("game-button");

        gameController.getView().addEventHandler(GameTimeUpEvent.GAME_TIME_UP, e -> {
            gameController.stopTimer();
            showMainMenu();
        });


        // create a new element to showcase time
        if (timedModeEnabled) {
            Label timeLabel = new Label("TIME: " + formatTime(timeLimit));
            timeLabel.getStyleClass().add("time-display");

            gameController.setTimeUpdateListener(remainingTime ->
                    timeLabel.setText("TIME: " + formatTime(remainingTime))
            );

            HBox topBar = new HBox(timeLabel);
            topBar.setAlignment(Pos.CENTER);
            topBar.setPadding(new Insets(10, 0, 10, 0));

            gamePane.setTop(topBar);
        }


        HBox bottomBar = new HBox(backToMenuButton);
        bottomBar.setAlignment(javafx.geometry.Pos.CENTER);
        bottomBar.setPadding(new javafx.geometry.Insets(10));

        gamePane.setBottom(bottomBar);

        // Handle game win event
        gameController.getView().addEventHandler(GAME_WIN, e -> {
            gameController.stopTimer();
            if (saveManager != null) {
                saveManager.saveGame(true); // Mark the game as finished
            }
            showMainMenu();
        });

        root.setCenter(gamePane);
    }

    /**
     * Helper method that shows time in minutes and seconds
     * @param seconds
     * @return
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
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