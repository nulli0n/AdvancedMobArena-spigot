package su.nightexpress.ama.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IArena {

    @NotNull Set<Player> getAliveGamePlayers();

    @NotNull MobList getMobs();
}
