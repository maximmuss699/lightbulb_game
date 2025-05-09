/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * An inteface that is used for listening for changes in settings - passing a shared state.
 */
package cz.vut.ija.game.view;

/**
 * Interface for classes that need to be notified of settings changes.
 * Provides callback methods for different types of settings updates.
 */
public interface SettingsChangeListener {
    /**
     * Called when board size is changed.
     *
     * @param newSize new board size string (e.g. "5×5")
     */
    void onSizeChanged(String newSize);

    /**
     * Called when bulb count is changed.
     *
     * @param newCount new bulb count
     */
    void onBulbCountChanged(int newCount);

    /**
     * Called when the timed mode setting is changed.
     *
     * @param enabled true if timed mode is enabled
     */
    void onTimedModeChanged(boolean enabled);

    /**
     * Called when the time limit is changed.
     *
     * @param newTimeLimit new time limit in seconds
     */
    void onTimeLimitChanged(int newTimeLimit);

    /**
     * Called when classic (untimed) mode is selected.
     */
    void onClassicModeSelected();

    /**
     * Called when the timed mode is selected.
     */
    void onTimedModeSelected();
}