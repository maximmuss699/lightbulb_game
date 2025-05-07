package cz.vut.ija.game.service;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.GameSave;
import cz.vut.ija.game.model.Tile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GameSaveService {
    private static final String SAVE_DIRECTORY = "saves";

    public GameSaveService() {
        // Vytvoření adresáře pro uložení, pokud neexistuje
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Vytvoří objekt GameSave z aktuální herní desky
     */
    public GameSave createSaveFromBoard(GameBoard board, String boardSize, int bulbCount) {
        GameSave save = new GameSave();
        save.setBoardSize(boardSize);
        save.setBulbCount(bulbCount);

        // Uložení rozložení desky
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

        save.setInitialBoardTypes(types);
        save.setInitialBoardRotations(rotations);

        return save;
    }

    /**
     * Uloží hru do souboru
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
     * Načte hru ze souboru
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
     * Vrátí seznam všech uložených her
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
     * Načte všechny uložené hry
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
     * Vytvoří herní desku z uloženého stavu po aplikování zadaného počtu tahů
     */
    public GameBoard createBoardFromSave(GameSave save, int moveIndex) {
        // Parsování velikosti
        String[] dimensions = save.getBoardSize().split("×");
        int rows = Integer.parseInt(dimensions[0]);
        int cols = Integer.parseInt(dimensions[1]);

        // Vytvoření prázdné desky
        GameBoard board = new GameBoard(rows, cols);

        // Nastavení počátečního rozložení
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board.setTileType(r, c, save.getInitialBoardTypes()[r][c]);
                board.setTileRotation(r, c, save.getInitialBoardRotations()[r][c]);
            }
        }

        // Aplikování tahů až do požadovaného indexu
        List<GameSave.GameMove> moves = save.getMoves();
        for (int i = 0; i <= moveIndex && i < moves.size(); i++) {
            GameSave.GameMove move = moves.get(i);
            board.setTileRotation(move.getRow(), move.getCol(), move.getNewRotation());
        }

        return board;
    }

    /**
     * Generuje název souboru pro uložení hry
     */
    private String generateFilename(GameSave save) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = sdf.format(save.getSaveDate());
        String status = save.isCompleted() ? "completed" : "ongoing";

        return String.format("game_%s_%s_%s.save", save.getBoardSize(), status, timestamp);
    }

}