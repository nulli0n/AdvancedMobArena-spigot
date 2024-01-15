package su.nightexpress.ama.arena.script.action;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.spot.Spot;

import java.util.*;
import java.util.function.Function;

public class Parameters {

    private static final Map<String, Parameter<?>> REGISTRY = new HashMap<>();

    public static final Parameter<Integer> DELAY = asInt("delay")
        .withIcon(Material.CLOCK);

    public static final Parameter<String> WAVE = asString("wave")
        .withIcon(Material.BLAZE_POWDER)
        .withSuggestions((arena, result) -> new ArrayList<>(arena.getConfig().getWaveManager().getWaveMap().keySet()));

    public static final Parameter<String> REGION = asString("region")
        .withIcon(Material.OAK_FENCE)
        .withSuggestions((arena, result) -> new ArrayList<>(arena.getConfig().getRegionManager().getRegionMap().keySet()));

    public static final Parameter<String> SPAWNERS = asString("spawners")
        .withIcon(Material.SPAWNER)
        .withSuggestions((arena, result) -> {
            String regionId = result.get(REGION, "");
            Region region = arena.getConfig().getRegionManager().getRegion(regionId);
            if (region == null) return Collections.emptyList();

            List<String> list = new ArrayList<>(region.getMobSpawners().keySet());
            list.add(0, Placeholders.WILDCARD);

            return list;
        });

    public static final Parameter<String> SHOP_CATEGORY = asString("shop_category")
        .withIcon(Material.EMERALD)
        .withSuggestions((arena, result) -> new ArrayList<>(arena.getConfig().getShopManager().getCategoryMap().keySet()));

    public static final Parameter<String> SHOP_PRODUCT = asString("shop_product")
        .withIcon(Material.GOLD_NUGGET)
        .withSuggestions((arena, result) -> {
            String catId = result.get(SHOP_CATEGORY, "");
            ShopCategory category = arena.getConfig().getShopManager().getCategory(catId);
            if (category == null) return Collections.emptyList();

            return new ArrayList<>(category.getProductsMap().keySet());
        });

    public static final Parameter<String> REWARD = asString("reward")
        .withIcon(Material.GOLD_INGOT)
        .withSuggestions((arena, result) -> new ArrayList<>(arena.getConfig().getRewardManager().getRewardMap().keySet()));

    public static final Parameter<String> SPOT = asString("spot")
        .withIcon(Material.ITEM_FRAME)
        .withSuggestions((arena, result) -> new ArrayList<>(arena.getConfig().getSpotManager().getSpotsMap().keySet()));

    public static final Parameter<String> STATE = asString("state")
        .withIcon(Material.PAINTING)
        .withSuggestions((arena, result) -> {
            String spotId = result.get(SPOT, "");
            Spot spot = arena.getConfig().getSpotManager().getSpot(spotId);
            if (spot == null) return Collections.emptyList();

            return new ArrayList<>(spot.getStates().keySet());
        });

    public static final Parameter<String> NAME = asString("name")
        .withIcon(Material.NAME_TAG);

    public static final Parameter<String> MESSAGE = asString("message")
        .withIcon(Material.PAPER);

    public static final Parameter<ArenaTargetType> TARGET = register("target",
        str -> StringUtil.getEnum(str, ArenaTargetType.class).orElse(ArenaTargetType.GLOBAL))
        .withIcon(Material.COMPASS)
        .withSuggestions((arena, result) -> CollectionsUtil.getEnumsList(ArenaTargetType.class));

    public static final Parameter<Integer> AMOUNT = asInt("amount")
        .withIcon(Material.HOPPER);

    public static final Parameter<Double> DOUBLE_VALUE = asDouble("value")
        .withIcon(Material.OAK_SIGN);

    @NotNull
    public static Optional<Parameter<?>> getByName(@NotNull String name) {
        return Optional.ofNullable(REGISTRY.get(name.toLowerCase()));
    }

    @NotNull
    public static Collection<Parameter<?>> getParameters() {
        return REGISTRY.values();
    }

    @NotNull
    public static <V> Parameter<V> register(@NotNull String name, @NotNull Function<String, V> parser) {
        Parameter<V> parameter = new Parameter<>(name, parser);
        REGISTRY.put(parameter.getName(), parameter);
        return parameter;
    }

    @NotNull
    public static Parameter<Integer> asInt(@NotNull String name) {
        Function<String, Integer> parser = str -> StringUtil.getInteger(str, 0, true);
        return register(name, parser);
    }

    @NotNull
    public static Parameter<Double> asDouble(@NotNull String name) {
        Function<String, Double> parser = str -> StringUtil.getDouble(str, 0, true);
        return register(name, parser);
    }

    @NotNull
    public static Parameter<String> asString(@NotNull String name) {
        Function<String, String> parser = Colorizer::strip;
        return register(name, parser);
    }
}
