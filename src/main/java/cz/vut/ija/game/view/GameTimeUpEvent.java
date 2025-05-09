/**
 * Authors:
 * Filip Hladík (xhladi26)
 * Maksim Samusevich (xsamus00)
 * <p>
 * A custom event that fires when time is up.
 */
package cz.vut.ija.game.view;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event fired when time runs out in timed mode.
 */
public class GameTimeUpEvent extends Event {
    /**
     * Event type for time up events.
     */
    public static final EventType<GameTimeUpEvent> GAME_TIME_UP = new EventType<>(Event.ANY, "GAME_TIME_UP");

    /**
     * Creates a new game time up event.
     */
    public GameTimeUpEvent() {
        super(GAME_TIME_UP);
    }
}