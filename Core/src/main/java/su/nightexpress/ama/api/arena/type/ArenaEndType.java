package su.nightexpress.ama.api.arena.type;

import org.jetbrains.annotations.NotNull;

public enum ArenaEndType {

    TIMELEFT(LeaveReason.TIMELEFT),
    FINISH(LeaveReason.FINISH),
    FORCE(LeaveReason.FORCE),
    NO_REGION(LeaveReason.NO_REGION),
    ;

    private final LeaveReason reason;

    ArenaEndType(@NotNull LeaveReason reason) {
        this.reason = reason;
    }

    @NotNull
    public LeaveReason getReason() {
        return this.reason;
    }

    @NotNull
    public ArenaGameEventType getGameEventType() {
        if (this == TIMELEFT) return ArenaGameEventType.GAME_END_TIME;
        if (this == FINISH) return ArenaGameEventType.GAME_END_WIN;
        return ArenaGameEventType.GAME_END_LOSE;
    }
}
