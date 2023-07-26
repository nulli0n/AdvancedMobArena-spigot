package su.nightexpress.ama.arena.util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.EntityUtil;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.external.EssentialsHook;
import su.nightexpress.ama.hook.external.SunLightHook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSnapshot {

    private final Location                 location;
    private final int                      foodLevel;
    private final float                    saturation;
    private final double                   health;
    private final ItemStack[]              inventory;
    private final ItemStack[]              armor;
    private final Collection<PotionEffect> effects;
    private final GameMode                 gameMode;

    private static final Map<UUID, PlayerSnapshot> SNAPSHOTS = new HashMap<>();

    PlayerSnapshot(@NotNull Player player) {
        this.location = player.getLocation();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.health = player.getHealth();
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.effects = player.getActivePotionEffects();
        this.gameMode = player.getGameMode();
    }

    public static void doSnapshot(@NotNull Player player) {
        SNAPSHOTS.put(player.getUniqueId(), new PlayerSnapshot(player));
    }

    public static void clear(@NotNull Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGliding(false);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(20F);
        player.setHealth(EntityUtil.getAttribute(player, Attribute.GENERIC_MAX_HEALTH));
        player.setFireTicks(0);
        player.leaveVehicle();
        if (Config.ARENA_CLEAR_POTION_EFFECTS.get()) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
        if (EngineUtils.hasPlugin(HookId.ESSENTIALS)) {
            EssentialsHook.disableGod(player);
        }
        if (EngineUtils.hasPlugin(HookId.SUNLIGHT)) {
            SunLightHook.disableGod(player);
        }
    }

    public static void restore(@NotNull ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        PlayerSnapshot snapshot = SNAPSHOTS.remove(player.getUniqueId());
        if (snapshot == null) return;

        Arena arena = arenaPlayer.getArena();

        Location exit = arena.getConfig().getLocation(ArenaLocationType.LEAVE);
        player.teleport(exit != null ? exit : snapshot.getLocation());

        player.setFoodLevel(snapshot.getFoodLevel());
        player.setSaturation(snapshot.getSaturation());
        player.setHealth(Math.min(EntityUtil.getAttribute(player, Attribute.GENERIC_MAX_HEALTH), snapshot.getHealth()));
        player.setGameMode(snapshot.getGameMode());

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        if (Config.ARENA_CLEAR_POTION_EFFECTS.get()) {
            player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
            player.addPotionEffects(snapshot.getPotionEffects());
        }

        // Return player inventory before the game
        if (arena.getConfig().getGameplayManager().isKitsEnabled()) {
            player.getInventory().setContents(snapshot.getInventory());
            player.getInventory().setArmorContents(snapshot.getArmor());
        }
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public double getHealth() {
        return health;
    }

    @NotNull
    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    @NotNull
    public Collection<PotionEffect> getPotionEffects() {
        return this.effects;
    }

    @NotNull
    public GameMode getGameMode() {
        return this.gameMode;
    }
}
