package cz.vut.ija.game.view;

import javafx.event.Event;
import javafx.event.EventType;

public class GameWinEvent extends Event {
    public static final EventType<GameWinEvent> GAME_WIN =
            new EventType<>(Event.ANY, "GAME_WIN");

    public GameWinEvent() {
        super(GAME_WIN);
    }
}