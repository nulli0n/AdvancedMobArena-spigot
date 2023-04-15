package su.nightexpress.ama.arena.setup.manager;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.setup.ArenaSetupUtils;
import su.nightexpress.ama.arena.setup.SetupItemType;
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.config.Lang;

public class RegionSetupManager extends AbstractSetupManager<ArenaRegion> {

    private Location[] cuboidCache;
    private VisualTask visualTask;

    public RegionSetupManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.visualTask = new VisualTask();
        this.visualTask.start();
    }

    @Override
    protected void onShutdown() {
        if (this.visualTask != null) {
            this.visualTask.stop();
            this.visualTask = null;
        }
        super.onShutdown();
    }

    @Override
    protected void onSetupStart(@NotNull Player player, @NotNull ArenaRegion region) {
        this.cuboidCache = new Location[2];
        if (region.getCuboid().isPresent()) {
            ArenaCuboid cuboid = region.getCuboid().get();
            this.cuboidCache[0] = cuboid.getMin().clone();
            this.cuboidCache[1] = cuboid.getMax().clone();
        }

        Inventory inventory = player.getInventory();
        inventory.setItem(0, SetupItemType.REGION_CUBOID.getItem());
        inventory.setItem(2, SetupItemType.REGION_SPAWN.getItem());
        inventory.setItem(4, SetupItemType.REGION_SPAWNER.getItem());
        inventory.setItem(8, SetupItemType.REGION_SAVE.getItem());
    }

    @Override
    protected void onSetupEnd(@NotNull Player player, @NotNull ArenaRegion region) {
        region.getEditor().open(player, 1);

        this.cuboidCache = null;
    }

    @Override
    protected void updateVisuals() {
        if (cuboidCache[0] != null) {
            ArenaSetupUtils.addVisualText(player, "&a« [Cuboid] 1st Corner »", cuboidCache[0]);
            ArenaSetupUtils.addVisualBlock(player, cuboidCache[0]);
        }
        if (cuboidCache[1] != null) {
            ArenaSetupUtils.addVisualText(player, "&a« [Cuboid] 2nd Corner »", cuboidCache[1]);
            ArenaSetupUtils.addVisualBlock(player, cuboidCache[1]);
        }

        ArenaRegion region = this.getObject();
        if (region.getSpawnLocation() != null) {
            ArenaSetupUtils.addVisualText(player, "&a« Spawn Location »", region.getSpawnLocation());
            ArenaSetupUtils.addVisualBlock(player, region.getSpawnLocation());
        }

        region.getMobSpawners().values().forEach(spawner -> {
            ArenaSetupUtils.addVisualText(player, "&c« Mob Spawner »", spawner);
            ArenaSetupUtils.addVisualBlock(player, spawner);
        });
    }

    private void updateVisualParticles(@NotNull Player player) {
        if (cuboidCache[0] != null && cuboidCache[1] != null) {
            //ArenaSetupUtils.playCuboid(this.cuboidCache);
            new ArenaCuboid.Visualizer(cuboidCache[0], cuboidCache[1]).draw(player);
        }
    }

    @Override
    protected void removeVisuals() {
        ArenaSetupUtils.removeVisuals(this.player);
    }

    @Override
    protected void handleItem(@NotNull PlayerInteractEvent e, @NotNull Player player, @NotNull ArenaRegion region,
                              @NotNull ItemStack item, @NotNull SetupItemType itemType) {

        switch (itemType) {
            case REGION_CUBOID -> {
                Block block = e.getClickedBlock();
                if (block == null || block.isEmpty()) return;

                Location location = block.getLocation();
                ArenaRegion overlap = region.getArenaConfig().getRegionManager().getRegion(location);
                if (overlap != null && !region.getId().equals(overlap.getId())) {
                    plugin.getMessage(Lang.Setup_Region_Cuboid_Error_Overlap).replace(overlap.replacePlaceholders()).send(player);
                    return;
                }

                Action action = e.getAction();
                int pos = action == Action.LEFT_CLICK_BLOCK ? 0 : 1;
                this.cuboidCache[pos] = location;

                plugin.getMessage(Lang.Setup_Region_Cuboid_Set)
                    .replace(region.replacePlaceholders())
                    .replace("%corner%", String.valueOf(pos + 1))
                    .send(player);

                if (this.cuboidCache[0] == null || this.cuboidCache[1] == null) return;
                ArenaCuboid cuboidNew = new ArenaCuboid(this.cuboidCache[0], this.cuboidCache[1]);
                region.setCuboid(cuboidNew);
            }
            case REGION_SPAWN -> {
                Location location = player.getLocation();
                if (region.getCuboid().isEmpty() || !region.getCuboid().get().contains(location)) {
                    plugin.getMessage(Lang.Setup_Region_Error_Outside).send(player);
                    return;
                }
                region.setSpawnLocation(location);
                plugin.getMessage(Lang.Setup_Region_Spawn_Set).replace(region.replacePlaceholders()).send(player);
            }
            case REGION_SPAWNER -> {
                Block block = e.getClickedBlock();
                if (block == null) return;

                Location location = block.getLocation();
                if (region.getCuboid().isEmpty() || !region.getCuboid().get().contains(location)) {
                    plugin.getMessage(Lang.Setup_Region_Error_Outside).send(player);
                    return;
                }

                Action action = e.getAction();
                if (action == Action.RIGHT_CLICK_BLOCK && region.getMobSpawners().values().remove(location)) {
                    plugin.getMessage(Lang.Setup_Region_Spawner_Remove).replace(region.replacePlaceholders()).send(player);
                    return;
                }
                if (action == Action.LEFT_CLICK_BLOCK && region.addMobSpawner(location)) {
                    plugin.getMessage(Lang.Setup_Region_Spawner_Add).replace(region.replacePlaceholders()).send(player);
                }
            }
            case REGION_SAVE -> {
                if (cuboidCache[0] != null && cuboidCache[1] != null) {
                    region.setCuboid(new ArenaCuboid(cuboidCache[0], cuboidCache[1]));
                }
                region.save();
                this.endSetup(player);
            }
        }
    }

    class VisualTask extends AbstractTask<AMA> {

        public VisualTask() {
            super(plugin(), 15L, true);
        }

        @Override
        public void action() {
            if (player == null) return;
            updateVisualParticles(player);
        }
    }
}
