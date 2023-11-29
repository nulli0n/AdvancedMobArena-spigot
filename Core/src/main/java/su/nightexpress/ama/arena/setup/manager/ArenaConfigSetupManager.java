package su.nightexpress.ama.arena.setup.manager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.setup.ArenaSetupUtils;
import su.nightexpress.ama.arena.setup.SetupItemType;
import su.nightexpress.ama.arena.util.BlockPos;
import su.nightexpress.ama.arena.util.Cuboid;
import su.nightexpress.ama.config.Lang;

import static su.nexmedia.engine.utils.Colors2.*;

public class ArenaConfigSetupManager extends AbstractSetupManager<ArenaConfig> {

    private BlockPos[] cuboidCache;

    public ArenaConfigSetupManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    protected void onSetupStart(@NotNull Player player, @NotNull ArenaConfig arenaConfig) {
        this.cuboidCache = new BlockPos[2];
        if (!arenaConfig.getProtectionZone().isEmpty()) {
            Cuboid cuboid = arenaConfig.getProtectionZone();
            this.cuboidCache[0] = cuboid.getMin().copy();
            this.cuboidCache[1] = cuboid.getMax().copy();
        }

        Inventory inventory = player.getInventory();
        inventory.setItem(1, SetupItemType.ARENA_PROTECTION_ZONE.getItem());
        inventory.setItem(3, SetupItemType.ARENA_LOCATION_LOBBY.getItem());
        inventory.setItem(4, SetupItemType.ARENA_LOCATION_LEAVE.getItem());
        inventory.setItem(5, SetupItemType.ARENA_LOCATION_SPECTATE.getItem());
        inventory.setItem(8, SetupItemType.ARENA_EXIT.getItem());
    }

    @Override
    protected void onSetupEnd(@NotNull Player player, @NotNull ArenaConfig arenaConfig) {
        arenaConfig.getEditor().open(player, 1);

        this.cuboidCache = null;
    }

    @Override
    protected void handleItem(@NotNull PlayerInteractEvent event, @NotNull Player player, @NotNull ArenaConfig arenaConfig, @NotNull ItemStack item, @NotNull SetupItemType itemType) {
        switch (itemType) {
            case ARENA_PROTECTION_ZONE -> {
                Block block = event.getClickedBlock();
                if (block == null || block.isEmpty()) return;

                Location location = block.getLocation();

                Action action = event.getAction();
                int pos = action == Action.LEFT_CLICK_BLOCK ? 0 : 1;
                this.cuboidCache[pos] = BlockPos.from(location);

                plugin.getMessage(Lang.Setup_Arena_Cuboid_Set)
                    .replace(arenaConfig.replacePlaceholders())
                    .replace("%corner%", String.valueOf(pos + 1))
                    .send(player);

                arenaConfig.setWorld(block.getWorld());

                if (this.cuboidCache[0] == null || this.cuboidCache[1] == null) return;

                Cuboid cuboid = new Cuboid(this.cuboidCache[0], this.cuboidCache[1]);
                arenaConfig.setProtectionZone(cuboid);
                arenaConfig.save();
            }
            case ARENA_LOCATION_LOBBY -> {
                Location location = player.getLocation();
                arenaConfig.setLocation(ArenaLocationType.LOBBY, location);
                arenaConfig.save();

                plugin.getMessage(Lang.Setup_Arena_Lobby_Set).replace(arenaConfig.replacePlaceholders()).send(player);
            }
            case ARENA_LOCATION_LEAVE -> {
                Location location = player.getLocation();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                    plugin.getMessage(Lang.Setup_Arena_Leave_Set).replace(arenaConfig.replacePlaceholders()).send(player);
                    arenaConfig.setLocation(ArenaLocationType.LEAVE, location);
                }
                else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
                    plugin.getMessage(Lang.Setup_Arena_Leave_UnSet).replace(arenaConfig.replacePlaceholders()).send(player);
                    arenaConfig.setLocation(ArenaLocationType.LEAVE, null);
                }
                else return;

                arenaConfig.save();
            }
            case ARENA_LOCATION_SPECTATE -> {
                Location loc = player.getLocation();
                arenaConfig.setLocation(ArenaLocationType.SPECTATE, loc);
                arenaConfig.save();

                plugin.getMessage(Lang.Setup_Arena_Spectate_Set).replace(arenaConfig.replacePlaceholders()).send(player);
            }
            case ARENA_EXIT -> this.endSetup(player);
        }
    }

    @Override
    protected void updateVisuals() {
        World world = this.object.getWorld();

        if (cuboidCache[0] != null && world != null) {
            ArenaSetupUtils.addVisualText(player, CYAN + "[Protection]" + GRAY + " 1st Corner", cuboidCache[0].toLocation(world));
            ArenaSetupUtils.addVisualBlock(player, cuboidCache[0].toLocation(world));
        }
        if (cuboidCache[1] != null && world != null) {
            ArenaSetupUtils.addVisualText(player, CYAN + "[Protection]" + GRAY + " 2nd Corner", cuboidCache[1].toLocation(world));
            ArenaSetupUtils.addVisualBlock(player, cuboidCache[1].toLocation(world));
        }

        Location lobby = object.getLocation(ArenaLocationType.LOBBY);
        if (lobby != null) {
            ArenaSetupUtils.addVisualText(player, "&a« Lobby Location »", lobby);
            ArenaSetupUtils.addVisualBlock(player, lobby);
        }

        Location leave = object.getLocation(ArenaLocationType.LEAVE);
        if (leave != null) {
            ArenaSetupUtils.addVisualText(player, "&c« Leave Location »", leave);
            ArenaSetupUtils.addVisualBlock(player, leave);
        }

        Location spec = object.getLocation(ArenaLocationType.SPECTATE);
        if (spec != null) {
            ArenaSetupUtils.addVisualText(player, "&b« Spectate Location »", spec);
            ArenaSetupUtils.addVisualBlock(player, spec);
        }
    }

    @Override
    protected void removeVisuals() {
        ArenaSetupUtils.removeVisuals(this.player);
    }
}
