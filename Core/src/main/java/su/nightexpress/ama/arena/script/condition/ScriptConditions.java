package su.nightexpress.ama.arena.script.condition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TriFunction;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.event.*;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.region.RegionManager;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.api.type.PlayerType;

import java.util.*;
import java.util.function.Function;

public class ScriptConditions {

    private static final Map<String, ScriptCondition<?, ?>> REGISTRY = new HashMap<>();

    public static final ScriptCondition<Number, Number>   CHANCE            = numeric("chance", event -> Rnd.get(true));
    public static final ScriptCondition<Number, Number>   WAVE_NUMBER       = numeric("wave_number", event -> event.getArena().getRoundNumber());
    public static final ScriptCondition<Number[], Number> ROUND_NUMBER      = numerics("round_number", event -> event.getArena().getRoundNumber());
    public static final ScriptCondition<Number, Number>   PLAYERS_AMOUNT    = numeric("players_amount", event -> event.getArena().getPlayers().select(GameState.INGAME, PlayerType.REAL).size());
    public static final ScriptCondition<Number, Number>   ENEMY_MOBS_AMOUNT = numeric("enemy_mobs_amount", event -> event.getArena().getMobs().getEnemies().size());
    public static final ScriptCondition<Number, Number>   ALLY_MOBS_AMOUNT  = numeric("ally_mobs_amount", event -> event.getArena().getMobs().getAllies().size());
    public static final ScriptCondition<Number, Number>   GAME_SCORE        = numeric("game_score", event -> event.getArena().getGameScore());

    public static final ScriptCondition<String, RegionManager> REGION_UNLOCKED = register("region_unlocked",
        str -> str,
        event -> event.getArena().getConfig().getRegionManager(),
        ScriptConditionPrefabs.forRegion(LockState.UNLOCKED));

    public static final ScriptCondition<String, RegionManager> REGION_LOCKED = register("region_locked",
        str -> str,
        event -> event.getArena().getConfig().getRegionManager(),
        ScriptConditionPrefabs.forRegion(LockState.LOCKED));

    public static final ScriptCondition<String, ShopManager> SHOP_UNLOCKED = register("shop_unlocked",
        str -> str,
        event -> event.getArena().getConfig().getShopManager(),
        (shopManager, str, operator) -> shopManager.isUnlocked());

    public static final ScriptCondition<String, ShopManager> SHOP_LOCKED = register("shop_locked",
        str -> str,
        event -> event.getArena().getConfig().getShopManager(),
        (shopManager, str, operator) -> shopManager.isLocked());

    public static final ScriptCondition<String, ShopManager> SHOP_CATEGORY_UNLOCKED = register("shop_category_unlocked",
        str -> str,
        event -> event.getArena().getConfig().getShopManager(),
        ScriptConditionPrefabs.forShopCategory(LockState.UNLOCKED));

    public static final ScriptCondition<String, ShopManager> SHOP_CATEGORY_LOCKED = register("shop_category_locked",
        str -> str,
        event -> event.getArena().getConfig().getShopManager(),
        ScriptConditionPrefabs.forShopCategory(LockState.LOCKED));

    public static final ScriptCondition<String, ShopManager> SHOP_PRODUCT_UNLOCKED = register("shop_product_unlocked",
        str -> str,
        event -> event.getArena().getConfig().getShopManager(),
        ScriptConditionPrefabs.forShopProduct(LockState.UNLOCKED));

    public static final ScriptCondition<String, ShopManager> SHOP_PRODUCT_LOCKED = register("shop_product_locked",
        str -> str,
        event -> event.getArena().getConfig().getShopManager(),
        ScriptConditionPrefabs.forShopProduct(LockState.LOCKED));

    public static final ScriptCondition<String, RegionManager> REGION_IS_EMPTY = register("region_is_empty",
        str -> str,
        event -> event.getArena().getConfig().getRegionManager(),
        (regionManager, str, operator) -> {
            Region region = regionManager.getRegion(str);
            return region == null || region.getPlayers().isEmpty();
        });

    public static final ScriptCondition<String, RegionManager> REGION_NOT_EMPTY = register("region_not_empty",
        str -> str,
        event -> event.getArena().getConfig().getRegionManager(),
        (regionManager, str, operator) -> {
            Region region = regionManager.getRegion(str);
            return region == null || !region.getPlayers().isEmpty();
        });

    public static final ScriptCondition<String, String> REGION_ID = string("region_id",
        event -> ((ArenaRegionEvent) event).getArenaRegion().getId());

    public static final ScriptCondition<String, String> SHOP_CATEGORY_ID = string("shop_category_id",
        event -> event instanceof ArenaShopProductEvent productEvent ? productEvent.getShopProduct().getCategory().getId() : ((ArenaShopCategoryEvent) event).getShopCategory().getId());

    public static final ScriptCondition<String, String> SHOP_PRODUCT_ID = string("shop_product_id",
        event -> ((ArenaShopProductEvent) event).getShopProduct().getId());

    public static final ScriptCondition<String, String> SPOT_ID = string("spot_id",
        event -> ((ArenaSpotStateChangeEvent) event).getSpot().getId());

    public static final ScriptCondition<String, String> SPOT_STATE_ID = string("spot_state_id",
        event -> ((ArenaSpotStateChangeEvent) event).getNewState().getId());

    @NotNull
    public static <C, E> ScriptCondition<C, E> register(@NotNull String name,
                                                        @NotNull Function<String, C> parser,
                                                        @NotNull Function<ArenaGameGenericEvent, E> extractor,
                                                        @NotNull TriFunction<E, C, ScriptCondition.Operator, Boolean> tester) {
        ScriptCondition<C, E> condition = new ScriptCondition<>(name, parser, extractor, tester);
        return register(condition);
    }

    @NotNull
    public static ScriptCondition<Number, Number> numeric(@NotNull String name,
                                                          @NotNull Function<ArenaGameGenericEvent, Number> extractor) {

        Function<String, Number> parser = str -> StringUtil.getDouble(str, 0D);
        TriFunction<Number, Number, ScriptCondition.Operator, Boolean> tester = (eventValue, condValue, operator) -> {
            return switch (operator) {
                case EQUAL -> eventValue.doubleValue() == condValue.doubleValue();
                case NOT_EQUAL -> eventValue.doubleValue() != condValue.doubleValue();
                case GREATER -> eventValue.doubleValue() > condValue.doubleValue();
                case SMALLER -> eventValue.doubleValue() < condValue.doubleValue();
                case EACH -> eventValue.intValue() % condValue.intValue() == 0;
                case EACH_NOT -> eventValue.intValue() % condValue.intValue() != 0;
            };
        };

        return register(name, parser, extractor, tester);
    }

    @NotNull
    public static ScriptCondition<Number[], Number> numerics(@NotNull String name,
                                                          @NotNull Function<ArenaGameGenericEvent, Number> extractor) {

        Function<String, Number[]> parser = str -> {
            String[] split = StringUtil.noSpace(str).split(",");
            Integer[] array = new Integer[split.length];

            for(int index = 0; index < split.length; ++index) {
                try {
                    array[index] = Integer.parseInt(split[index]);
                } catch (NumberFormatException var5) {
                    array[index] = 0;
                }
            }

            return array;
        };
        TriFunction<Number, Number[], ScriptCondition.Operator, Boolean> tester = (eventValue, condValue, operator) -> {
            return Arrays.stream(condValue).anyMatch(number -> {
                return switch (operator) {
                    case EQUAL -> eventValue.doubleValue() == number.doubleValue();
                    case NOT_EQUAL -> eventValue.doubleValue() != number.doubleValue();
                    case GREATER -> eventValue.doubleValue() > number.doubleValue();
                    case SMALLER -> eventValue.doubleValue() < number.doubleValue();
                    case EACH -> eventValue.intValue() % number.intValue() == 0;
                    case EACH_NOT -> eventValue.intValue() % number.intValue() != 0;
                };
            });
        };

        return register(name, parser, extractor, tester);
    }

    @NotNull
    public static ScriptCondition<String, String> string(@NotNull String name,
                                                         @NotNull Function<ArenaGameGenericEvent, String> extractor) {

        Function<String, String> parser = Colorizer::strip;
        TriFunction<String, String, ScriptCondition.Operator, Boolean> tester = (eventValue, condValue, operator) -> {
            if (operator == ScriptCondition.Operator.NOT_EQUAL) {
                return !eventValue.equalsIgnoreCase(condValue);
            }
            return eventValue.equalsIgnoreCase(condValue) || condValue.equalsIgnoreCase(Placeholders.WILDCARD);
        };

        return register(name, parser, extractor, tester);
    }

    @NotNull
    public static <C, E> ScriptCondition<C, E> register(@NotNull ScriptCondition<C, E> condition) {
        REGISTRY.put(condition.getName(), condition);
        return condition;
    }

    @Nullable
    public static ScriptCondition<?, ?> getByName(@NotNull String name) {
        return REGISTRY.get(name.toLowerCase());
    }

    @NotNull
    public static Set<ScriptCondition<?, ?>> getConditions() {
        return new HashSet<>(REGISTRY.values());
    }
}
