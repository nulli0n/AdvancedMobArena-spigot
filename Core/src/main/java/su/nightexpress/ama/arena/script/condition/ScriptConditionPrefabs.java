package su.nightexpress.ama.arena.script.condition;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.TriFunction;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;

public class ScriptConditionPrefabs {

    @NotNull
    public static TriFunction<ArenaRegionManager, String, ScriptCondition.Operator, Boolean> forRegion(@NotNull LockState state) {
        return (regionManager, regId, opeartor) -> {
            ArenaRegion region = regionManager.getRegion(regId);
            return region == null || region.getLockState() == state;
        };
    }

    @NotNull
    public static TriFunction<ShopManager, String, ScriptCondition.Operator, Boolean> forShopCategory(@NotNull LockState state) {
        return (shopManager, catId, opeartor) -> {
            ShopCategory category = shopManager.getCategory(catId);
            return category == null || category.getLockState() == state;
        };
    }

    @NotNull
    public static TriFunction<ShopManager, String, ScriptCondition.Operator, Boolean> forShopProduct(@NotNull LockState state) {
        return (shopManager, catId, opeartor) -> {
            String[] split = catId.split(":");
            if (split.length < 2) return false;

            ShopCategory category = shopManager.getCategory(split[0]);
            if (category == null) return true;

            ShopProduct product = category.getProduct(split[1]);
            return product == null || product.getLockState() == state;
        };
    }
}
