package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.shop.ArenaShopProduct;

public class ArenaShopProductEvent extends ArenaGameGenericEvent {

    private final ArenaShopProduct shopProduct;

    public ArenaShopProductEvent(@NotNull AbstractArena arena, @NotNull ArenaGameEventType eventType, @NotNull ArenaShopProduct shopProduct) {
        super(arena, eventType);
        this.shopProduct = shopProduct;
    }

    @NotNull
    public ArenaShopProduct getShopProduct() {
        return shopProduct;
    }
}
