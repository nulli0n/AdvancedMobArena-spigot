package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.shop.ArenaShopCategory;

public class ArenaShopCategoryEvent extends ArenaGameGenericEvent {

    private final ArenaShopCategory shopCategory;

    public ArenaShopCategoryEvent(@NotNull AbstractArena arena, @NotNull ArenaShopCategory shopCategory, @NotNull ArenaGameEventType eventType) {
        super(arena, eventType);
        this.shopCategory = shopCategory;
    }

    @NotNull
    public ArenaShopCategory getShopCategory() {
        return shopCategory;
    }
}
