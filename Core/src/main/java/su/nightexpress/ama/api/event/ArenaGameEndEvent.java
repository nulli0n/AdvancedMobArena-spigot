package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.type.GameResult;

public class ArenaGameEndEvent extends ArenaGameGenericEvent {

    private final GameResult result;

    public ArenaGameEndEvent(@NotNull Arena arena, @NotNull GameResult result) {
        super(arena, result == GameResult.VICTORY ? ArenaGameEventType.GAME_END_WIN : ArenaGameEventType.GAME_END_LOSE);
        this.result = result;
    }

    @NotNull
    public GameResult getResult() {
        return result;
    }
}
