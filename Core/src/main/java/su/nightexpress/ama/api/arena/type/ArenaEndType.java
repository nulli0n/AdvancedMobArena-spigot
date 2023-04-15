package su.nightexpress.ama.api.arena.type;

import org.jetbrains.annotations.NotNull;

@Deprecated
public enum ArenaEndType {

    TIMELEFT,
    FINISH,
    FORCE,
    NO_REGION,
    ;

    @NotNull
    @Deprecated
    public ArenaGameEventType getGameEventType() {
        if (this == TIMELEFT) return ArenaGameEventType.GAME_END_TIME;
        if (this == FINISH) return ArenaGameEventType.GAME_END_WIN;
        return ArenaGameEventType.GAME_END_LOSE;
    }
}
