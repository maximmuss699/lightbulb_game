/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 *
 * An inteface that is used for listening for changes in settings - passing a shared state.
 */
package cz.vut.ija.game.view;

public interface SettingsChangeListener {
    void onSizeChanged(String newSize);

    void onBulbCountChanged(int newCount);

    void onTimedModeChanged(boolean enabled);

    void onTimeLimitChanged(int newTimeLimit);

    void onClassicModeSelected();

    void onTimedModeSelected();
}