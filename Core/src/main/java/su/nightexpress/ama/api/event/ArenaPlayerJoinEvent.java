package su.nightexpress.ama.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaPlayerJoinEvent extends ArenaGameGenericEvent implements Cancellable {

    private final Player  player;
    private       boolean isCancelled;

    public ArenaPlayerJoinEvent(@NotNull Arena arena, @NotNull Player player) {
        super(arena, GameEventType.PLAYER_JOIN);
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
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
