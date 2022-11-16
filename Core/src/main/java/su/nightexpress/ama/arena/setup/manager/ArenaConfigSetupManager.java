package su.nightexpress.ama.arena.setup.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.setup.ArenaSetupUtils;
import su.nightexpress.ama.arena.setup.SetupItemType;
import su.nightexpress.ama.config.Lang;

public class ArenaConfigSetupManager extends AbstractSetupManager<ArenaConfig> {

    public ArenaConfigSetupManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    protected void onSetupStart(@NotNull Player player, @NotNull ArenaConfig arenaConfig) {
        Inventory inventory = player.getInventory();
        inventory.setItem(2, SetupItemType.ARENA_LOCATION_LOBBY.getItem());
        inventory.setItem(4, SetupItemType.ARENA_LOCATION_LEAVE.getItem());
        inventory.setItem(6, SetupItemType.ARENA_LOCATION_SPECTATE.getItem());
        inventory.setItem(8, SetupItemType.ARENA_EXIT.getItem());
    }

    @Override
    protected void onSetupEnd(@NotNull Player player, @NotNull ArenaConfig arenaConfig) {
        arenaConfig.getEditor().open(player, 1);
    }

    @Override
    protected void handleItem(@NotNull PlayerInteractEvent e, @NotNull Player player, @NotNull ArenaConfig arenaConfig, @NotNull ItemStack item, @NotNull SetupItemType itemType) {
        switch (itemType) {
            case ARENA_LOCATION_LOBBY -> {
                Location location = player.getLocation();
                arenaConfig.setLocation(ArenaLocationType.LOBBY, location);
                arenaConfig.save();

                plugin.getMessage(Lang.Setup_Arena_Lobby_Set).replace(arenaConfig.replacePlaceholders()).send(player);
            }
            case ARENA_LOCATION_LEAVE -> {
                Location location = player.getLocation();
                Action action = e.getAction();

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
