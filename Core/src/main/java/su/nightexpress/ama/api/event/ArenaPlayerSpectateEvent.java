package su.nightexpress.ama.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaPlayerSpectateEvent extends ArenaEvent implements Cancellable {

    private final Player  player;
    private       boolean isCancelled;

    public ArenaPlayerSpectateEvent(@NotNull Arena arena, @NotNull Player player) {
        super(arena);
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
