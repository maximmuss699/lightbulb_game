/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * Interface for the Command pattern
 */
package cz.vut.ija.game.command;

/**
 * A reversible game action following the Command pattern.
 * Implementations must support execute() and undo().
 */
public interface Command {
    /**
     * Executes the command, applying its effect to the model.
     */
    void execute();

    /**
     * Undoes the command, restoring the model to its previous state.
     */
    void undo();
}