package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.shop.ShopManager;

public class ArenaShopEvent extends ArenaGameGenericEvent {

    public ArenaShopEvent(@NotNull Arena arena, @NotNull GameEventType eventType) {
        super(arena, eventType);
    }

    @NotNull
    public ShopManager getShopManager() {
        return this.getArena().getConfig().getShopManager();
    }
}
