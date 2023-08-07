package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.api.type.GameResult;

public class ArenaGameEndEvent extends ArenaGameGenericEvent {

    private final GameResult result;

    public ArenaGameEndEvent(@NotNull Arena arena, @NotNull GameResult result) {
        super(arena, result == GameResult.VICTORY ? GameEventType.GAME_END_WIN : GameEventType.GAME_END_LOSE);
        this.result = result;
    }

    @NotNull
    public GameResult getResult() {
        return result;
    }
}
