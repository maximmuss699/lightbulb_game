package cz.vut.ija.game.view;

public interface SettingsChangeListener {
    void onSizeChanged(String newSize);

    void onBulbCountChanged(int newCount);

    void onTimedModeChanged(boolean enabled);

    void onTimeLimitChanged(int newTimeLimit);

    void onClassicModeSelected();

    void onTimedModeSelected();
}