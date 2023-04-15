package su.nightexpress.ama.arena.script.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaUpcomingWave;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.supply.ArenaSupplyChest;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;

import java.util.*;
import java.util.function.BiConsumer;

public class ScriptActions {

    private static final Map<String, ScriptAction> REGISTRY = new HashMap<>();

    public static final ScriptAction UNLOCK_REGION = register("unlock_region", (event, result) -> {
        String regId = result.get(Parameters.REGION, "");
        ArenaRegion region = event.getArena().getConfig().getRegionManager().getRegion(regId);
        if (region != null) region.unlock();
    }, Parameters.REGION);
    public static final ScriptAction LOCK_REGION   = register("lock_region", (event, result) -> {
        String regId = result.get(Parameters.REGION, "");
        ArenaRegion region = event.getArena().getConfig().getRegionManager().getRegion(regId);
        if (region != null) region.lock();
    }, Parameters.REGION);

    public static final ScriptAction UNLOCK_SHOP = register("unlock_shop", (event, result) -> {
        event.getArena().getConfig().getShopManager().unlock();
    });
    public static final ScriptAction LOCK_SHOP   = register("lock_shop", (event, result) -> {
        event.getArena().getConfig().getShopManager().lock();
    });

    public static final ScriptAction UNLOCK_SHOP_CATEGORY = register("unlock_shop_category", (event, result) -> {
        String catId = result.get(Parameters.SHOP_CATEGORY, "");
        ShopCategory category = event.getArena().getConfig().getShopManager().getCategory(catId);
        if (category != null) category.unlock();
    }, Parameters.SHOP_CATEGORY);
    public static final ScriptAction LOCK_SHOP_CATEGORY   = register("lock_shop_category", (event, result) -> {
        String catId = result.get(Parameters.SHOP_CATEGORY, "");
        ShopCategory category = event.getArena().getConfig().getShopManager().getCategory(catId);
        if (category != null) category.lock();
    }, Parameters.SHOP_CATEGORY);

    public static final ScriptAction UNLOCK_SHOP_PRODUCT = register("unlock_shop_product", (event, result) -> {
        String catId = result.get(Parameters.SHOP_CATEGORY, "");
        String prodId = result.get(Parameters.SHOP_PRODUCT, "");

        ShopCategory category = event.getArena().getConfig().getShopManager().getCategory(catId);
        if (category == null) return;

        ShopProduct product = category.getProduct(prodId);
        if (product != null) product.unlock();
    }, Parameters.SHOP_CATEGORY, Parameters.SHOP_PRODUCT);
    public static final ScriptAction LOCK_SHOP_PRODUCT   = register("lock_shop_product", (event, result) -> {
        String catId = result.get(Parameters.SHOP_CATEGORY, "");
        String prodId = result.get(Parameters.SHOP_PRODUCT, "");

        ShopCategory category = event.getArena().getConfig().getShopManager().getCategory(catId);
        if (category == null) return;

        ShopProduct product = category.getProduct(prodId);
        if (product != null) product.lock();
    }, Parameters.SHOP_CATEGORY, Parameters.SHOP_PRODUCT);

    public static final ScriptAction GIVE_REWARD = register("give_reward", (event, result) -> {
        ArenaTargetType targetType = result.get(Parameters.TARGET, ArenaTargetType.GLOBAL);
        String rewardId = result.get(Parameters.REWARD, "");
        event.getArena().getConfig().getRewardManager().getReward(rewardId).ifPresent(reward -> {
            reward.give(event.getArena(), targetType);
        });
    }, Parameters.REWARD, Parameters.TARGET);

    public static final ScriptAction RUN_COMMAND = register("run_command", (event, result) -> {
        ArenaTargetType targetType = result.get(Parameters.TARGET, ArenaTargetType.GLOBAL);
        String command = result.get(Parameters.NAME, "");
        event.getArena().runCommand(command, targetType);
    }, Parameters.NAME, Parameters.TARGET);

    public static final ScriptAction BROADCAST = register("broadcast", (event, result) -> {
        ArenaTargetType targetType = result.get(Parameters.TARGET, ArenaTargetType.GLOBAL);
        String message = Colorizer.apply(result.get(Parameters.MESSAGE, ""));
        event.getArena().broadcast(targetType, message);
    }, Parameters.MESSAGE, Parameters.TARGET);

    public static final ScriptAction CHANGE_SPOT = register("change_spot", (event, result) -> {
        String spotId = result.get(Parameters.SPOT, "");
        String stateId = result.get(Parameters.STATE, "");
        ArenaSpot spot = event.getArena().getConfig().getSpotManager().getSpot(spotId);
        if (spot != null) spot.setState(event.getArena(), stateId);
    }, Parameters.SPOT, Parameters.STATE);

    public static final ScriptAction REFILL_SUPPLY_CHEST = register("refill_supply_chest", (event, result) -> {
        String chestId = result.get(Parameters.NAME, "");
        ArenaSupplyChest chest = event.getArena().getConfig().getSupplyManager().getChest(chestId);
        if (chest != null) chest.refill();
    }, Parameters.NAME);

    public static final ScriptAction ADJUST_MOB_AMOUNT = register("adjust_mob_amount", (event, result) -> {
        String waveId = result.get(Parameters.WAVE, "");
        int amount = result.get(Parameters.AMOUNT, 0);

        event.getArena().addWaveAmplificatorAmount(waveId, amount);
    }, Parameters.WAVE, Parameters.AMOUNT);

    public static final ScriptAction ADJUST_MOB_LEVEL = register("adjust_mob_level", (event, result) -> {
        String waveId = result.get(Parameters.WAVE, "");
        int amount = result.get(Parameters.AMOUNT, 0);

        event.getArena().addWaveAmplificatorLevel(waveId, amount);
    }, Parameters.WAVE, Parameters.AMOUNT);

    public static final ScriptAction INJECT_WAVE = register("inject_wave", (event, result) -> {
        String regionId = result.get(Parameters.REGION, "");
        String waveId = result.get(Parameters.WAVE, "");
        String spawnerIds = result.get(Parameters.SPAWNERS, "");

        Arena arena = event.getArena();
        ArenaConfig config = arena.getConfig();
        ArenaRegion region = config.getRegionManager().getRegion(regionId);
        if (region == null) return;

        ArenaWave wave = config.getWaveManager().getWave(waveId);
        if (wave == null) return;

        List<ArenaWaveMob> mobs = new ArrayList<>(wave.getMobsByChance().stream().map(ArenaWaveMob::new).toList());
        mobs.forEach(mob -> mob.setAmount((int) (mob.getAmount() + arena.getWaveAmplificatorAmount(wave.getId()))));
        mobs.forEach(mob -> mob.setLevel((int) (mob.getLevel() + arena.getWaveAmplificatorLevel(wave.getId()))));
        mobs.removeIf(mob -> mob.getAmount() <= 0);
        if (mobs.isEmpty()) return;

        List<String> spawners = new ArrayList<>(Arrays.asList(spawnerIds.split(",")));
        spawners.removeIf(String::isEmpty);

        arena.injectWave(new ArenaUpcomingWave(region, mobs, spawners));
    }, Parameters.WAVE, Parameters.REGION, Parameters.SPAWNERS);

    @NotNull
    public static ScriptAction register(@NotNull String name,
                                        @NotNull BiConsumer<ArenaGameGenericEvent, ParameterResult> executor,
                                        Parameter<?>...                       parameters) {
        return register(new ScriptAction(name, executor, parameters));
    }

    @NotNull
    public static ScriptAction register(@NotNull ScriptAction action) {
        REGISTRY.put(action.getName(), action);
        return action;
    }

    @Nullable
    public static ScriptAction getByName(@NotNull String name) {
        return REGISTRY.get(name.toLowerCase());
    }

    @NotNull
    public static Set<ScriptAction> getActions() {
        return new HashSet<>(REGISTRY.values());
    }
}
