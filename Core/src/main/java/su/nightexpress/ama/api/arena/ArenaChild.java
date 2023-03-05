package su.nightexpress.ama.api.arena;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;

public interface ArenaChild {

    @NotNull ArenaConfig getArenaConfig();

    @NotNull
    default AMA plugin() {
        return this.getArenaConfig().plugin();
    }

    @NotNull
    default Arena getArena() {
        return this.getArenaConfig().getArena();
    }
}
