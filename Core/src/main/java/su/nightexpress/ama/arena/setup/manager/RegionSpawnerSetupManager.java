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
import su.nexmedia.engine.utils.Colors2;
import su.nexmedia.engine.utils.Pair;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.setup.ArenaSetupUtils;
import su.nightexpress.ama.arena.setup.SetupItemType;
import su.nightexpress.ama.config.Lang;

public class RegionSpawnerSetupManager extends AbstractSetupManager<Pair<Region, String>> {

    public RegionSpawnerSetupManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    protected void onSetupStart(@NotNull Player player, @NotNull Pair<Region, String> pair) {
        Inventory inventory = player.getInventory();
        inventory.setItem(4, SetupItemType.REGION_SPAWNER.getItem());
        inventory.setItem(8, SetupItemType.REGION_SAVE.getItem());
    }

    @Override
    protected void onSetupEnd(@NotNull Player player, @NotNull Pair<Region, String> pair) {
        pair.getFirst().getEditor().getSpawnersEditor().open(player, 1);
    }

    @Override
    protected void updateVisuals() {
        Region region = this.getObject().getFirst();
        World world = region.getArenaConfig().getWorld();

        region.getMobSpawners().values().forEach(spawners -> {
            spawners.forEach(pos -> {
                Location location = pos.toLocation(world);

                ArenaSetupUtils.addVisualText(player, Colors2.ORANGE + Colors2.BOLD + "Mob Spawner", location);
                ArenaSetupUtils.addVisualBlock(player, location);
            });
        });
    }

    @Override
    protected void removeVisuals() {
        ArenaSetupUtils.removeVisuals(this.player);
    }

    @Override
    protected void handleItem(@NotNull PlayerInteractEvent event, @NotNull Player player, @NotNull Pair<Region, String> pair,
                              @NotNull ItemStack item, @NotNull SetupItemType itemType) {

        if (itemType == SetupItemType.REGION_SPAWNER) {
            Block block = event.getClickedBlock();
            if (block == null) return;

            Region region = pair.getFirst();
            Location location = block.getLocation();
            if (region.getCuboid().isEmpty() || !region.getCuboid().get().contains(location)) {
                plugin.getMessage(Lang.Setup_Region_Error_Outside).send(player);
                return;
            }

            String category = pair.getSecond();

            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_BLOCK && region.removeMobSpawner(category, location)) {
                plugin.getMessage(Lang.Setup_Region_Spawner_Remove).replace(region.replacePlaceholders()).send(player);
                return;
            }
            if (action == Action.LEFT_CLICK_BLOCK && region.addMobSpawner(category, location)) {
                plugin.getMessage(Lang.Setup_Region_Spawner_Add).replace(region.replacePlaceholders()).send(player);
            }
        }
        else if (itemType == SetupItemType.REGION_SAVE) {
            Region region = pair.getFirst();
            region.save();
            this.endSetup(player);
        }
    }
}
