package su.nightexpress.ama.api.arena;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.config.ArenaConfig;

public interface IArenaObject {

    @NotNull ArenaConfig getArenaConfig();

    @NotNull
    default AMA plugin() {
        return this.getArenaConfig().plugin();
    }

    @NotNull
    default AbstractArena getArena() {
        return this.getArenaConfig().getArena();
    }
}
