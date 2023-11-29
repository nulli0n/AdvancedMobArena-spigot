package su.nightexpress.ama.arena.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.config.MobsConfig;

public class ArenaGenericListener extends AbstractListener<AMA> {

    private final ArenaManager manager;

    public ArenaGenericListener(@NotNull ArenaManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaPlayerBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (ArenaPlayer.isPlaying(player)) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        Arena arena = this.manager.getArenaAtLocation(block.getLocation());
        if (arena != null && !player.hasPermission(Perms.CREATOR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaDamageGeneric(EntityDamageEvent e) {
        // Avoid damage in lobby
        if (e.getEntity() instanceof Player player) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return;


            if (arenaPlayer.getState() != GameState.INGAME) {
                e.setCancelled(true);
            }
            return;
        }

        if (e.getEntity() instanceof LivingEntity entity) {
            this.plugin.getMobManager().updateMobBar(entity);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaDamageFriendly(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity victim)) return;

        Entity eDamager = e.getDamager();
        if (eDamager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity living) {
            eDamager = living;
        }

        Arena arenaDamager = this.plugin.getMobManager().getEntityArena(eDamager);
        Arena arenaVictim = this.plugin.getMobManager().getEntityArena(victim);
        if (arenaDamager == arenaVictim && arenaDamager != null) {
            if (arenaDamager.getMobs().getFaction((LivingEntity) eDamager) == arenaDamager.getMobs().getFaction(victim)) {
                e.setCancelled(true);
            }
        }
        else if (eDamager instanceof Player pDamager && victim instanceof Player pVictim) {
            if (pDamager == pVictim) return;
            if (ArenaPlayer.isPlaying(pDamager) || ArenaPlayer.isPlaying(pVictim)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaItemMove(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.CRAFTING) return;

        Player player = (Player) e.getWhoClicked();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplaySettings().isKitsEnabled()) return;
        if (arena.getState() == GameState.INGAME) return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArenaItemSpawn(ItemSpawnEvent e) {
        Item item = e.getEntity();
        Arena arena = this.manager.getArenaAtLocation(item.getLocation());
        if (arena == null) return;

        arena.addGroundItem(item);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArenaMobSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getType() == EntityType.ARMOR_STAND && MobsConfig.IGNORE_ARMOR_STANDS.get()) return;

        Location location = entity.getLocation();
        Arena arena = plugin.getArenaManager().getArenaAtLocation(location);
        if (arena == null) return;

        if (arena.getState() == GameState.INGAME && !arena.isAboutToEnd()) {
            CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
            /*if (reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && MobsConfig.ALLY_FROM_EGGS.get().contains(entity.getType())) {
                arena.spawnAllyMob(entity.getType(), location);
                event.setCancelled(true);
                return;
            }*/
            if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM || arena.getConfig().getGameplaySettings().isAllowedSpawnReason(reason)) {
                if (!arena.isAboutToSpawnMobs()) {
                    MobManager.setArena(entity, arena);
                    arena.getMobs().getEnemies().add(entity);
                    arena.setRoundTotalMobsAmount(arena.getRoundTotalMobsAmount() + 1);
                }
                this.plugin.runTask(task -> {
                    if (ArenaUtils.isPet(entity)) {
                        if (arena.getConfig().getGameplaySettings().isPetsAllowed()) {
                            arena.getMobs().remove(entity);
                            arena.setRoundTotalMobsAmount(arena.getRoundTotalMobsAmount() - 1);
                        }
                        else entity.remove();
                    }
                });
                return;
            }
        }

        event.setCancelled(true);
    }

    /*@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaMobTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity agressor)) return;
        if (!this.plugin.getMobManager().isArenaEntity(agressor)) return;
        if (EngineUtils.hasPlugin(HookId.MYTHIC_MOBS) && MythicMobsHook.isMythicMob(agressor)) return;

        LivingEntity target = event.getTarget();
        if (target == null) return;

        if (this.plugin.getMobManager().isArenaEntity(target)) {
            event.setCancelled(true);
            return;
        }
        if (target instanceof Player player) {
            if (!ArenaPlayer.isPlaying(player)) {
                event.setCancelled(true);
            }
        }
    }*/

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArenaMobTeleport(EntityTeleportEvent e) {
        Location to = e.getTo();
        if (to == null) return;

        Arena arenaTo = this.manager.getArenaAtLocation(to);
        Arena arenaFrom = this.manager.getArenaAtLocation(e.getFrom());

        if (arenaTo != arenaFrom) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArenaPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().hasPermission(Perms.CREATOR)) return;

        Location to = e.getTo();
        if (to == null) return;

        Arena arenaTo = this.manager.getArenaAtLocation(to);
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(e.getPlayer());
        if (arenaPlayer == null) {
            Arena arenaFrom = this.manager.getArenaAtLocation(e.getFrom());
            if (arenaFrom != null) return;

            if (arenaTo != null) {
                e.setCancelled(true);
            }
        }
        else {
            if (arenaPlayer.isTransfer() || arenaPlayer.isGhost() || arenaPlayer.isDead()) return;
            if (arenaPlayer.getArena().isAboutToEnd()) return;
            if (arenaPlayer.getArena() == arenaTo) return;

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaMobCombust(EntityCombustEvent e) {
        Entity entity = e.getEntity();
        if (this.plugin.getMobManager().isArenaEntity(entity)) {
            if (e instanceof EntityCombustByEntityEvent ec) {
                if (this.plugin.getMobManager().isArenaEntity(ec.getCombuster())) {
                    e.setCancelled(true);
                }
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaMobExplode(EntityExplodeEvent e) {
        if (e.blockList().stream().anyMatch(block -> manager.getArenaAtLocation(block.getLocation()) != null)) {
            e.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBlockExplode(BlockExplodeEvent e) {
        if (e.blockList().stream().anyMatch(block -> manager.getArenaAtLocation(block.getLocation()) != null)) {
            e.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArenaBlockFire(BlockIgniteEvent e) {
        BlockIgniteEvent.IgniteCause cause = e.getCause();
        if (cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return;

        Block block = e.getBlock();
        Arena arena = this.manager.getArenaAtLocation(block.getLocation());
        if (arena != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBlockChange(EntityChangeBlockEvent e) {
        Arena arena = plugin.getArenaManager().getArenaAtLocation(e.getBlock().getLocation());
        if (arena != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBlockForm(EntityBlockFormEvent e) {
        Arena arena = plugin.getArenaManager().getArenaAtLocation(e.getBlock().getLocation());
        if (arena != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBreakVehicle(VehicleDamageEvent e) {
        e.setCancelled(!this.canBreak(e.getAttacker(), e.getVehicle()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBreakDecorations(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) {
            e.setCancelled(!this.canBreak(e.getDamager(), e.getEntity()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBreakPainting(HangingBreakByEntityEvent e) {
        e.setCancelled(!this.canBreak(e.getRemover(), e.getEntity()));
    }

    private boolean canBreak(@Nullable Entity damager, @NotNull Entity entity) {
        if (damager instanceof Player player && ArenaPlayer.isPlaying(player)) {
            return false;
        }
        if (damager != null && damager.hasPermission(Perms.CREATOR)) {
            return true;
        }

        Arena arena = plugin.getArenaManager().getArenaAtLocation(entity.getLocation());
        return arena == null;
    }

    /*@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArenaPlayerTeleport(PlayerTeleportEvent e) {
        Location to = e.getTo();
        if (to == null) return;

        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        AbstractArena arenaTo = this.manager.getArenaAtLocation(to);
        if (arenaPlayer == null) {
            if (arenaTo != null && !to.equals(arenaTo.getConfig().getLocation(ArenaLocationType.SPECTATE))) {
                e.setCancelled(true);
            }
        }
        else {
            AbstractArena arenaFrom = arenaPlayer.getArena();
            if (arenaTo != arenaFrom) {
                e.setCancelled(true);
            }
        }
    }*/

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaPlayerInteractEntity(PlayerInteractEntityEvent e) {
        e.setCancelled(!this.canInteract(e));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaPlayerInteractEntity2(PlayerInteractAtEntityEvent e) {
        e.setCancelled(!this.canInteract(e));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaPlayerInteractStand(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(ArenaPlayer.isPlaying(e.getPlayer()));
    }

    private boolean canInteract(@NotNull PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return true;

        Entity entity = event.getRightClicked();
        if (entity instanceof LivingEntity livingEntity && arenaPlayer.getArena().getMobs().isAlly(livingEntity)) return true;

        return entity instanceof Player || entity instanceof Vehicle || !this.plugin.getMobManager().isArenaEntity(entity);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArenaChunkUnload(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (chunk.getPluginChunkTickets().contains(this.plugin)) return;
        if (this.plugin.getArenaManager().getArenaByChunk(chunk) != null) {
            chunk.addPluginChunkTicket(this.plugin);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArenaChat(AsyncPlayerChatEvent e) {
        if (!Config.CHAT_ENABLED.get()) return;

        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        Kit kit = arenaPlayer.getKit();

        // TODO option for ghosts and dead speak

        e.getRecipients().retainAll(arena.getPlayers().all().stream().map(ArenaPlayer::getPlayer).toList());

        // TODO Do not use real chat I guess ?
        String format = Config.CHAT_FORMAT.get()
            .replace(Placeholders.PLAYER_NAME, "%1$s")
            .replace(Placeholders.GENERIC_MESSAGE, "%2$s");
        format = arenaPlayer.replacePlaceholders().apply(format);
        format = arena.replacePlaceholders().apply(format);

        if (EngineUtils.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }
        e.setFormat(format);
    }
}
