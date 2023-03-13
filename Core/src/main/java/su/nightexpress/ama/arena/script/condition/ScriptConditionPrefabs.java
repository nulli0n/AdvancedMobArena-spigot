package su.nightexpress.ama.arena.script.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.arena.shop.ArenaShopManager;
import su.nightexpress.ama.arena.shop.impl.ArenaShopCategory;
import su.nightexpress.ama.arena.shop.impl.ArenaShopProduct;
import su.nightexpress.ama.utils.TriFunction;

public class ScriptConditionPrefabs {

    @NotNull
    public static TriFunction<ArenaRegionManager, String, ScriptCondition.Operator, Boolean> forRegion(@NotNull LockState state) {
        return (regionManager, regId, opeartor) -> {
            ArenaRegion region = regionManager.getRegion(regId);
            return region == null || region.getState() == state;
        };
    }

    @NotNull
    public static TriFunction<ArenaShopManager, String, ScriptCondition.Operator, Boolean> forShopCategory(@NotNull LockState state) {
        return (shopManager, catId, opeartor) -> {
            ArenaShopCategory category = shopManager.getCategory(catId);
            return category == null || category.getState() == state;
        };
    }

    @NotNull
    public static TriFunction<ArenaShopManager, String, ScriptCondition.Operator, Boolean> forShopProduct(@NotNull LockState state) {
        return (shopManager, catId, opeartor) -> {
            String[] split = catId.split(":");
            if (split.length < 2) return false;

            ArenaShopCategory category = shopManager.getCategory(split[0]);
            if (category == null) return true;

            ArenaShopProduct product = category.getProduct(split[1]);
            return product == null || product.getState() == state;
        };
    }
}
