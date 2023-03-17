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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.external.MythicMobsHook;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.mob.config.MobConfig;

public class ArenaGenericListener extends AbstractListener<AMA> {

    private final ArenaManager manager;

    public ArenaGenericListener(@NotNull ArenaManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaPlayerBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (ArenaPlayer.isPlaying(player)) {
            e.setCancelled(true);
            return;
        }

        Block block = e.getBlock();
        Arena arena = this.manager.getArenaAtLocation(block.getLocation());
        if (arena != null && !player.hasPermission(Perms.CREATOR)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaDamageGeneric(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        MobConfig mob = this.plugin.getMobManager().getEntityMobConfig(entity);
        if (mob != null && mob.isBarEnabled()) {
            this.plugin.runTask(task -> mob.createOrUpdateBar((LivingEntity) entity));
            return;
        }

        if (!(entity instanceof Player player)) return;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        // Avoid damage in lobby
        if (arenaPlayer.getState() != GameState.INGAME) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaDamageFriendly(EntityDamageByEntityEvent e) {
        Entity eVictim = e.getEntity();
        Entity eDamager = e.getDamager();

        if (!(eVictim instanceof LivingEntity victim)) return;
        if (eDamager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity living) {
            eDamager = living;
        }

        if (plugin.getMobManager().isArenaEntity(eDamager) && plugin.getMobManager().isArenaEntity(victim)) {
            e.setCancelled(true);
        }
        else if (eDamager instanceof Player pDamager && victim instanceof Player pVictim) {
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
        if (!arena.getConfig().getGameplayManager().isKitsEnabled()) return;
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
    public void onArenaMobSpawn(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity instanceof Player || Hooks.isCitizensNPC(entity)) return;

        Location location = entity.getLocation();
        Arena arena = plugin.getArenaManager().getArenaAtLocation(location);
        if (arena == null/* || !arena.getConfig().isActive()*/) return;

        if (arena.getState() == GameState.INGAME && !arena.isAboutToFinish()) {
            CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();
            if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM || arena.getConfig().getGameplayManager().isAllowedSpawnReason(reason)) {
                if (!arena.isAboutToSpawnMobs()) {
                    this.plugin.getMobManager().setArena(entity, arena); // Add Arena tag to entity
                    //MobManager.setOutsider(entity);
                    arena.getMobs().add(entity);
                    arena.setWaveMobsTotalAmount(arena.getWaveMobsTotalAmount() + 1);
                }
                return;
            }
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onArenaMobTarget(EntityTargetEvent e) {
        if (!(e.getEntity() instanceof LivingEntity agressor)) return;
        if (!(e.getTarget() instanceof LivingEntity target)) return;

        if (!this.plugin.getMobManager().isArenaEntity(agressor)) return;
        //if (MobManager.isOutsider(agressor)) return;
        if (Hooks.hasMythicMobs() && MythicMobsHook.isMythicMob(agressor)) return;

        if (this.plugin.getMobManager().isArenaEntity(target)) {
            e.setCancelled(true);
            return;
        }
        if (target instanceof Player player) {
            if (!ArenaPlayer.isPlaying(player)) {
                e.setCancelled(true);
            }
        }
    }

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
        Location to = e.getTo();
        if (to == null) return;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(e.getPlayer());
        Arena arenaFrom = arenaPlayer == null ? null : arenaPlayer.getArena();
        if (arenaFrom != null && arenaFrom.isAboutToFinish()) return;

        Arena arenaTo = this.manager.getArenaAtLocation(to);
        if (arenaFrom != null && arenaTo != arenaFrom && arenaFrom.getPlayers().contains(arenaPlayer)) {
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
        e.setCancelled(!this.canDamage(e.getAttacker(), e.getVehicle()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBreakDecorations(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) {
            e.setCancelled(!this.canDamage(e.getDamager(), e.getEntity()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaBreakPainting(HangingBreakByEntityEvent e) {
        e.setCancelled(!this.canDamage(e.getRemover(), e.getEntity()));
    }

    private boolean canDamage(@Nullable Entity damager, @NotNull Entity entity) {
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

    /**
     * Prevent interact with non-mob entities in arena
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaPlayerInteractEntity(PlayerInteractEntityEvent e) {
        e.setCancelled(!this.canInteract(e));
    }

    /**
     * Prevent interact with non-mob entities in arena
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaPlayerInteractEntity2(PlayerInteractAtEntityEvent e) {
        e.setCancelled(!this.canInteract(e));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArenaPlayerInteractStand(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(ArenaPlayer.isPlaying(e.getPlayer()));
    }

    private boolean canInteract(@NotNull PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if (!ArenaPlayer.isPlaying(player)) return true;

        Entity entity = e.getRightClicked();
        return entity instanceof Player || entity instanceof Vehicle || !this.plugin.getMobManager().isArenaEntity(entity);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArenaChunkUnload(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        if (chunk.getPluginChunkTickets().contains(this.plugin)) return;

        for (Entity entity : e.getChunk().getEntities()) {
            if (plugin.getMobManager().isArenaEntity(entity)) {
                chunk.addPluginChunkTicket(this.plugin);
                return;
            }
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

        e.getRecipients().retainAll(arena.getPlayers().stream().map(ArenaPlayer::getPlayer).toList());

        String format = Config.CHAT_FORMAT.get()
            .replace(Placeholders.Player.NAME, "%1$s")
            .replace(Placeholders.GENERIC_MESSAGE, "%2$s");
        format = arenaPlayer.replacePlaceholders().apply(format);
        format = arena.replacePlaceholders().apply(format);

        if (Hooks.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }
        e.setFormat(format);
    }
}
