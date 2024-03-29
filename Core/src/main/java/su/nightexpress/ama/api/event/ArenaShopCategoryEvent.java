package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;

public class ArenaShopCategoryEvent extends ArenaGameGenericEvent {

    private final ShopCategory shopCategory;

    public ArenaShopCategoryEvent(@NotNull Arena arena, @NotNull ShopCategory shopCategory, @NotNull GameEventType eventType) {
        super(arena, eventType);
        this.shopCategory = shopCategory;
    }

    @NotNull
    public ShopCategory getShopCategory() {
        return shopCategory;
    }
}
