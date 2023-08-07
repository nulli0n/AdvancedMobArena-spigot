package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;

public class ArenaShopProductEvent extends ArenaGameGenericEvent {

    private final ShopProduct shopProduct;

    public ArenaShopProductEvent(@NotNull Arena arena, @NotNull GameEventType eventType, @NotNull ShopProduct shopProduct) {
        super(arena, eventType);
        this.shopProduct = shopProduct;
    }

    @NotNull
    public ShopProduct getShopProduct() {
        return shopProduct;
    }
}
