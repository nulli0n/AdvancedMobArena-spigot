package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.shop.ArenaShopManager;

public class ArenaShopEvent extends ArenaGameGenericEvent {

    public ArenaShopEvent(@NotNull AbstractArena arena, @NotNull ArenaGameEventType eventType) {
        super(arena, eventType);
    }

    @NotNull
    public ArenaShopManager getShopManager() {
        return this.getArena().getConfig().getShopManager();
    }
}
