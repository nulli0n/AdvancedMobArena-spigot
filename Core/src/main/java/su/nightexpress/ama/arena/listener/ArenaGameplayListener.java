package su.nightexpress.ama.arena.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.ArrayUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaMobDeathEvent;
import su.nightexpress.ama.api.event.ArenaPlayerDeathEvent;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.util.LobbyItem;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.config.MobConfig;
import su.nightexpress.ama.mob.config.MobsConfig;
import su.nightexpress.ama.mob.kill.MobKillReward;
import su.nightexpress.ama.mob.kill.MobKillStreak;
import su.nightexpress.ama.stats.object.StatType;

public class ArenaGameplayListener extends AbstractListener<AMA> {

    private final ArenaManager manager;

    public ArenaGameplayListener(@NotNull ArenaManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArenaGameEvent(ArenaGameGenericEvent event) {
        event.getArena().onArenaGameEvent(event);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGameplayItemDurability(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplaySettings().isItemDurabilityEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGameplayItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        // Prevent drop lobby or kit items in lobby.
        if (arena.getConfig().getGameplaySettings().isKitsEnabled() && arenaPlayer.getState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (!arena.getConfig().getGameplaySettings().isItemDropEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGamePlayerRegen(EntityRegainHealthEvent e) {
        if (e.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) return;
        if (!(e.getEntity() instanceof Player player)) return;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplaySettings().isRegenerationEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGamePlayerHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplaySettings().isHungerEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onGamePlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(Perms.BYPASS_ARENA_GAME_COMMANDS)) return;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        String command = StringUtil.extractCommandName(event.getMessage());
        if (ArrayUtil.contains(plugin.getLabels(), command)) return;

        Arena arena = arenaPlayer.getArena();
        if (arena.getConfig().getGameplaySettings().isWhitelistedCommand(command)) return;

        event.setCancelled(true);
        this.plugin.runTask(task -> player.closeInventory());
        this.plugin.getMessage(Lang.ARENA_GAME_RESTRICT_COMMANDS).send(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGamePlayerRegionMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        if (to == null) return;

        Location from = e.getFrom();

        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        if (arenaPlayer.isReal()) {
            if (to.getX() == from.getX() && to.getZ() == from.getZ()) return;
        }
        else if (arenaPlayer.isGhost()) {
            if (to.getX() == from.getX() && to.getZ() == from.getZ() && to.getY() == from.getY()) return;
        }

        Arena arena = arenaPlayer.getArena();
        if (!arena.getConfig().isProtected(to)) {
            e.setCancelled(true);
            return;
        }

        Region region = arena.getConfig().getRegionManager().getRegion(to);
        if (region == null || !region.isActive() || region.isUnlocked()) return;

        region.getCuboid().ifPresent(cuboid -> {
            if (cuboid.contains(to) && !cuboid.contains(from)) {
                e.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGamePlayerDeathRealSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        arenaPlayer.onDeath();
        event.setRespawnLocation(player.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGamePlayerDeathReal(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        ArenaPlayerDeathEvent deathEvent = new ArenaPlayerDeathEvent(arena, arenaPlayer);
        plugin.getPluginManager().callEvent(deathEvent);

        if (arena.getConfig().getGameplaySettings().isKeepInventory()) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }

        event.setDroppedExp(0);
        event.setKeepLevel(true);
        this.plugin.runTask(task -> player.spigot().respawn());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        Arena arena = this.plugin.getMobManager().getEntityArena(entity);
        if (arena == null) return;

        if (!arena.getConfig().getGameplaySettings().isMobDropLootEnabled()) {
            e.getDrops().clear();
        }
        if (!arena.getConfig().getGameplaySettings().isMobDropExpEnabled()) {
            e.setDroppedExp(0);
        }

        String mobId = MobManager.getMobId(entity);
        MobConfig customMob = this.plugin.getMobManager().getEntityMobConfig(entity);
        if (customMob != null && customMob.isBarEnabled()) {
            arena.removeBossBar(entity);
        }

        ArenaMobDeathEvent mobDeathEvent = new ArenaMobDeathEvent(arena, entity, mobId);

        Player killer = entity.getKiller();
        ArenaPlayer arenaPlayer = killer != null ? ArenaPlayer.getPlayer(killer) : null;
        if (killer != null && arenaPlayer != null) {
            mobDeathEvent.setKiller(arenaPlayer);

            int streak = arenaPlayer.getKillStreak() + 1;

            MobKillReward killReward = MobManager.getMobKillReward(entity);
            MobKillStreak killStreak = MobManager.getMobKillStreak(streak);

            if (killStreak != null) {
                killStreak.execute(killer);
            }
            arenaPlayer.setKillStreak(streak);
            if (streak > 0) {
                arenaPlayer.setKillStreakDecay(MobsConfig.KILL_STREAK_DECAY.get() * 1000L);
            }
            arenaPlayer.addStats(StatType.BEST_KILL_STREAK, 1);
            arenaPlayer.addStats(StatType.MOB_KILLS, 1);

            if (killReward != null) {
                killReward.reward(arenaPlayer);
                this.plugin.getMobManager().displayMobKillReward(entity, killReward);
            }
        }

        plugin.getPluginManager().callEvent(mobDeathEvent);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGamePlayerBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);

        if (arenaPlayer != null) {
            e.setCancelled(true);
            if (block.getType() == Material.TNT) {
                ItemStack item = e.getItemInHand();
                item.setAmount(item.getAmount() - 1);
                TNTPrimed tnt = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
                tnt.setSource(player);
            }
            return;
        }

        if (player.hasPermission(Perms.CREATOR)) return;
        Arena arena = this.manager.getArenaAtLocation(block.getLocation());
        if (arena != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameStatsTNT(EntityExplodeEvent e) {
        if (e.getEntity() instanceof TNTPrimed tnt && tnt.getSource() instanceof Player player) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return;

            arenaPlayer.addStats(StatType.TNT_EXPLODED, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGameStatsConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();

        if (meta instanceof PotionMeta) {
            arenaPlayer.addStats(StatType.POTIONS_DRUNK, 1);
        }
        else {
            arenaPlayer.addStats(StatType.FOOD_EATEN, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameStatsItemBreak(PlayerItemBreakEvent e) {
        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        arenaPlayer.addStats(StatType.EQUIPMENT_BROKEN, 1);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onGamePlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        arenaPlayer.leaveArena();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        if (arenaPlayer.getState() != GameState.INGAME) {
            // Prevent using lobby items on arena signs
            // and interactable blocks.
            Block block = event.getClickedBlock();
            if (block != null && block.getType().isInteractable()) return;

            LobbyItem lobbyItem = LobbyItem.get(item);
            if (lobbyItem != null) {
                lobbyItem.use(arenaPlayer);
                event.setUseItemInHand(Result.DENY);
                return;
            }
        }

        Arena arena = arenaPlayer.getArena();
        if (arena.getConfig().getGameplaySettings().getBannedItems().contains(item.getType())) {
            event.setUseItemInHand(Result.DENY);
            event.setUseInteractedBlock(Result.DENY);
            return;
        }

        // What a stupid spawner egg api.....
        String typeRaw = item.getType().name();
        Block block = event.getClickedBlock();
        if (block != null && !block.getType().isInteractable() && typeRaw.endsWith("_SPAWN_EGG")) {
            String typeName = typeRaw.replace("_SPAWN_EGG", "");
            EntityType entityType = StringUtil.getEnum(typeName, EntityType.class).orElse(null);
            if (entityType != null && MobsConfig.ALLY_FROM_EGGS.get().contains(entityType)) {
                arena.spawnAllyMob(entityType, block.getRelative(BlockFace.UP).getLocation());
                item.setAmount(item.getAmount() - 1);
            }
            event.setUseItemInHand(Result.DENY);
            event.setUseInteractedBlock(Result.DENY);
        }
    }
}
