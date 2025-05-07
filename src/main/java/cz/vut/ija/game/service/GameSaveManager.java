package cz.vut.ija.game.service;

import cz.vut.ija.game.model.GameBoard;
import cz.vut.ija.game.model.GameSave;

/**
 * Správce pro ukládání herního stavu a zaznamenávání tahů.
 * Odděluje logiku ukládání od herního kontroleru.
 */
public class GameSaveManager {
    private final GameBoard board;
    private final GameSaveService saveService;
    private final String boardSize;
    private final int bulbCount;
    private GameSave currentSave;
    private boolean newMovesMade = false;
    
    /**
     * Vytvoří nového správce ukládání pro danou herní desku.
     * 
     * @param board herní deska
     * @param boardSize řetězec s velikostí desky (např. "5×5")
     * @param bulbCount počet žárovek
     */
    public GameSaveManager(GameBoard board, String boardSize, int bulbCount) {
        this.board = board;
        this.boardSize = boardSize;
        this.bulbCount = bulbCount;
        this.saveService = new GameSaveService();
        this.currentSave = saveService.createSaveFromBoard(board, boardSize, bulbCount);
    }

    /**
     * Zaznamená tah pro pozdější uložení.
     * 
     * @param row řádek
     * @param col sloupec
     * @param oldRotation původní rotace
     * @param newRotation nová rotace
     */
    public void recordMove(int row, int col, int oldRotation, int newRotation) {
        currentSave.addMove(row, col, oldRotation, newRotation);
        newMovesMade = true;
    }
    
    /**
     * Uloží aktuální stav hry do souboru.
     * 
     * @param completed zda je hra dokončena
     */
    public void saveGame(boolean completed) {
        if (newMovesMade) {
            saveService.saveGame(currentSave, completed);
            newMovesMade = false;
        }
    }
    
    /**
     * Vrátí aktuální uloženou hru.
     * 
     * @return objekt s aktuální uloženou hrou
     */
    public GameSave getCurrentSave() {
        return currentSave;
    }
}