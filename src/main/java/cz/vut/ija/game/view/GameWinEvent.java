/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A custom event that fires when player won the game.
 */
package cz.vut.ija.game.view;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event fired when the game is won.
 */
public class GameWinEvent extends Event {
    /**
     * Event type for win events.
     */
    public static final EventType<GameWinEvent> GAME_WIN =
            new EventType<>(Event.ANY, "GAME_WIN");

    /**
     * Creates a new game win event.
     */
    public GameWinEvent() {
        super(GAME_WIN);
    }
}