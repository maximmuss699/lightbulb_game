package cz.vut.ija.game.model;

/** MVC “Model → View” callback interface. */
public interface BoardObserver {
    void tileChanged(int row, int col);
}