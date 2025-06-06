/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A service class used for creating game save containing board information, bulb count etc.
 */
package cz.vut.ija.game.service;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.GameSave;
import cz.vut.ija.game.model.Tile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for creating, saving and loading game saves.
 */
public class GameSaveService {
    /**
     * Directory for storing save files.
     */
    private static final String SAVE_DIRECTORY = "saves";

    /**
     * Creates a new game save service. Creates save directory if it does not exist
     */
    public GameSaveService() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Creates a new save from the current board state.
     *
     * @param board     the game board
     * @param boardSize size of the board as string
     * @param bulbCount number of light bulbs
     * @return new game save
     */
    public GameSave createSaveFromBoard(GameBoard board, String boardSize, int bulbCount) {
        GameSave save = new GameSave();
        save.setBoardSize(boardSize);
        save.setBulbCount(bulbCount);

        // saves the board state
        int rows = board.getRows();
        int cols = board.getCols();

        String[][] types = new String[rows][cols];
        int[][] rotations = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = board.getTile(r, c);
                types[r][c] = tile.getType();
                rotations[r][c] = tile.getRotation();
            }
        }

        if (board.getSolutionRotations() != null) {
            save.setSolutionRotations(board.getSolutionRotations());
        }

        save.setInitialBoardTypes(types);
        save.setInitialBoardRotations(rotations);

        return save;
    }

    /**
     * Save the game to a file
     *
     * @param gameSave  game save to save
     * @param completed was the game finished?
     */
    public void saveGame(GameSave gameSave, boolean completed) {
        gameSave.setCompleted(completed);

        String filename = generateFilename(gameSave);
        File file = new File(SAVE_DIRECTORY, filename);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(gameSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a game from a file.
     *
     * @param filename name of the save file
     * @return loaded game save or null if failed
     */
    public GameSave loadGame(String filename) {
        File file = new File(SAVE_DIRECTORY, filename);

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (GameSave) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a list of all save filenames.
     *
     * @return list of save filenames
     */
    public List<String> getAllSaveFilenames() {
        List<String> filenames = new ArrayList<>();
        File dir = new File(SAVE_DIRECTORY);

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".save"));
            if (files != null) {
                for (File file : files) {
                    filenames.add(file.getName());
                }
            }
        }

        return filenames;
    }

    /**
     * Gets a list of all saved games.
     *
     * @return list of game saves
     */
    public List<GameSave> getAllSaves() {
        List<GameSave> saves = new ArrayList<>();
        List<String> filenames = getAllSaveFilenames();

        for (String filename : filenames) {
            GameSave save = loadGame(filename);
            if (save != null) {
                saves.add(save);
            }
        }

        return saves;
    }

    /**
     * Recreates a GameBoard instance based on a previously saved game state
     *
     * @param save      GameSave object containing the saved game data
     * @param moveIndex the index of the last move to apply from the saved move history (0 is first move)
     * @return a GameBoard instance with all specified moves up to the given move index
     */
    public GameBoard createBoardFromSave(GameSave save, int moveIndex) {
        // Parse the board size
        String[] dimensions = save.getBoardSize().split("×");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        // create a new board
        GameBoard board = new GameBoard(rows, cols);

        // set the initial board state
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board.setTileType(r, c, save.getInitialBoardTypes()[r][c]);
                board.setTileRotation(r, c, save.getInitialBoardRotations()[r][c]);
            }
        }

        // Apply the moves till the given index
        List<GameSave.GameMove> moves = save.getMoves();
        for (int i = 0; i <= moveIndex && i < moves.size(); i++) {
            GameSave.GameMove move = moves.get(i);
            board.setTileRotation(move.getRow(), move.getCol(), move.getNewRotation());
        }

        if (save.getSolutionRotations() != null) {
            board.setSolutionRotations(save.getSolutionRotations());
        }

        return board;
    }

    /**
     * Generates a filename for a save.
     *
     * @param save the game save
     * @return generated filename
     */
    private String generateFilename(GameSave save) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = sdf.format(save.getSaveDate());
        String status = save.isCompleted() ? "completed" : "ongoing";

        return String.format("game_%s_%s_%s.save", save.getBoardSize(), status, timestamp);
    }

}