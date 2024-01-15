package su.nightexpress.ama.arena.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.util.LobbyItem;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.mob.config.MobsConfig;

import java.util.Set;

public class ArenaItemsListener extends AbstractListener<AMA> {

    public ArenaItemsListener(@NotNull AMA plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameItemUseGeneric(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        if (arenaPlayer.isDead()) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            return;
        }

        if (arenaPlayer.getState() != GameState.INGAME) {
            // Prevent using lobby items on arena signs and interactable blocks.
            Block block = event.getClickedBlock();
            if (block != null && block.getType().isInteractable()) return;

            LobbyItem lobbyItem = LobbyItem.get(item);
            if (lobbyItem != null) {
                lobbyItem.use(arenaPlayer);
            }

            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            return;
        }

        /*Arena arena = arenaPlayer.getArena();
        if (arena.getConfig().getGameplaySettings().getBannedItems().contains(item.getType())) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
        }*/
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onGameItemUseTNT(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Block block = event.getBlock();
        ItemStack item = event.getItemInHand();

        if (block.getType() == Material.TNT && Config.ARENA_TNT_ALLOWED_PLACEMENT.get()) {
            item.setAmount(item.getAmount() - 1);
            TNTPrimed tnt = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
            tnt.setSource(player);
            tnt.setFuseTicks(Config.ARENA_TNT_FUSE_TICKS.get());

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGameItemUseFireball(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.useItemInHand() == Event.Result.DENY) return;

        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Block block = event.getClickedBlock();
        if (block != null && !block.getType().isAir()) return;

        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        ItemStack item = player.getInventory().getItem(slot);
        if (item == null || item.getType().isAir()) return;

        if (item.getType() == Material.FIRE_CHARGE && Config.ARENA_FIRE_CHARGE_ALLOW_LAUNCH.get()) {
            item.setAmount(item.getAmount() - 1);

            Location location = player.getEyeLocation().add(player.getLocation().getDirection());
            SmallFireball fireball = player.getWorld().spawn(location, SmallFireball.class);
            fireball.setShooter(player);
            fireball.setIsIncendiary(true);

            if (slot == EquipmentSlot.OFF_HAND) {
                player.swingOffHand();
            }
            else player.swingMainHand();

            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGameItemUseSpawnEgg(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Player player = event.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        Set<String> allowed = MobsConfig.ALLY_FROM_EGGS.get();
        if (allowed.isEmpty()) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType().isAir() || block.getType().isInteractable()) return;

        EntityType entityType = plugin.getArenaNMS().getSpawnEggType(item);
        if (entityType == null) return;

        if (allowed.contains(entityType.getKey().getKey()) || allowed.contains(Placeholders.WILDCARD)) {
            Arena arena = arenaPlayer.getArena();
            arena.spawnAllyMob(entityType, block.getRelative(BlockFace.UP).getLocation(), player);
            item.setAmount(item.getAmount() - 1);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }
}
