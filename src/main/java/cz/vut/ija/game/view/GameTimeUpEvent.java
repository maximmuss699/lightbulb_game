package cz.vut.ija.game.view;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event fired when time runs out in timed mode.
 */
public class GameTimeUpEvent extends Event {
    public static final EventType<GameTimeUpEvent> GAME_TIME_UP = new EventType<>(Event.ANY, "GAME_TIME_UP");

    public GameTimeUpEvent() {
        super(GAME_TIME_UP);
    }
}