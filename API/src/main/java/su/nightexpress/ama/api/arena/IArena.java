package su.nightexpress.ama.api.arena;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.info.MobList;
import su.nightexpress.ama.api.arena.info.PlayerList;

public interface IArena {

    @NotNull PlayerList<? extends IArenaPlayer> getPlayers();

    @NotNull MobList getMobs();
}
