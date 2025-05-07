package cz.vut.ija.game;

import cz.vut.ija.game.controller.GameController;
import cz.vut.ija.game.model.GameSave;
import cz.vut.ija.game.service.GameSaveManager;
import cz.vut.ija.game.service.GameSaveService;
import cz.vut.ija.game.view.*;
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

/**
 * Main application class for the LightBulb Puzzle Game.
 * Sets up the JavaFX stage, scene, and core UI elements
 * and delegates game logic and view management to GameController.
 */
public class Main extends Application implements SettingsChangeListener {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 1000;

    private Scene scene;
    private BorderPane root;

    /**
     * Views
     */
    private MainMenuView mainMenuView;
    private GameModeSelectView gameModeView;
    private DifficultySelectView difficultyView;
    private TimeSelectView timeSelectView;
    private CustomGameView customGameView;

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
        // Vytvořit/získat instanci GameSaveService
        GameSaveService saveService = new GameSaveService();

        // Získat seznam uložených her
        List<GameSave> saves = saveService.getAllSaves();

        if (saves.isEmpty()) {
            // Pokud nejsou žádné uložené hry, zobrazit zprávu
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("No Saved Games");
            alert.setHeaderText(null);
            alert.setContentText("There are no saved games to replay.");
            alert.showAndWait();
            return;
        }

        // Vytvořit a zobrazit view pro výběr uložené hry
        LoadGameView loadGameView = new LoadGameView(saves);
        loadGameView.setOnSaveSelected(save -> showReplay(save));
        loadGameView.getBackButton().setOnAction(e -> showMainMenu());

        root.setCenter(loadGameView);
    }

    private void showReplay(GameSave save) {
        ReplayView replayView = new ReplayView(save);

        // Nastavit callbacky
        replayView.setOnPlayGameAtMove(moveIndex -> continueGameFromMove(save, moveIndex));
        replayView.setOnBackToMenu(() -> showMainMenu());

        root.setCenter(replayView);
    }

    private void continueGameFromMove(GameSave save, int moveIndex) {
        // Vytvořit desku v požadovaném stavu
        GameSaveService saveService = new GameSaveService();
        GameBoard board = saveService.createBoardFromSave(save, moveIndex);

        // Vytvořit herní kontroler
        gameController = new GameController(board);

        // Vytvořit nový GameSaveManager s původními údaji
        saveManager = new GameSaveManager(board, save.getBoardSize(), save.getBulbCount());

        // Nastavit saveManager do kontroleru
        gameController.setSaveManager(saveManager);

        // Zaznamenat všechny tahy až do moveIndex, protože pokračujeme v existující hře
        List<GameSave.GameMove> movesToReplay = save.getMoves().subList(0, moveIndex + 1);
        for (GameSave.GameMove move : movesToReplay) {
            saveManager.recordMove(move.getRow(), move.getCol(), move.getOldRotation(), move.getNewRotation());
        }

        // Vytvořit a nastavit herní obrazovku
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(gameController.getView());

        // Přidat tlačítko pro návrat do menu, které uloží hru
        Button backToMenuButton = new Button("Back to Menu");
        backToMenuButton.setOnAction(e -> {
            if (saveManager != null) {
                saveManager.saveGame(false); // Uložit jako nedokončenou
            }
            showMainMenu();
        });

        HBox bottomBar = new HBox(10, backToMenuButton);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));

        gamePane.setBottom(bottomBar);

        // Přidat handler pro výhru, který označí hru jako dokončenou
        gameController.getView().addEventHandler(GameWinEvent.GAME_WIN, e -> {
            if (saveManager != null) {
                saveManager.saveGame(true); // Uložit jako dokončenou
            }
            // Váš existující kód pro zobrazení výhry
        });

        // Zobrazit herní obrazovku
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

        // Parsování velikosti desky
        String[] dimensions = boardSize.split("×");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        // Generate puzzle
        LevelGenerator gen = new LevelGenerator(rows, cols, bulbCount);
        GameBoard puzzle = gen.generatePuzzle();

        // Initialize controller
        gameController = new GameController(puzzle);

        saveManager = new GameSaveManager(puzzle, boardSize, bulbCount);
        gameController.setSaveManager(saveManager);

        // Set up a new pane
        BorderPane gamePane = new BorderPane();
        gamePane.setCenter(gameController.getView());
        // Handle game win event
        gameController.getView().addEventHandler(
                cz.vut.ija.game.view.GameWinEvent.GAME_WIN,
                e -> showMainMenu()
        );

        Button backToMenuButton = new Button("Back to main menu");
        backToMenuButton.setOnAction(event -> {
                System.out.println("Saving game...");
            if (saveManager != null) {
                saveManager.saveGame(false); // Uložit rozehranou hru
            }
            showMainMenu(); // Zobrazit hlavní menu
        });

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