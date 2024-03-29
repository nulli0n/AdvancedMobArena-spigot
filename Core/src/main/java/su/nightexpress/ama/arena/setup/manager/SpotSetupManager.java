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
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.arena.setup.ArenaSetupUtils;
import su.nightexpress.ama.arena.setup.SetupItemType;
import su.nightexpress.ama.arena.spot.Spot;
import su.nightexpress.ama.config.Lang;

public class SpotSetupManager extends AbstractSetupManager<Spot> {

    private Location[] cuboidCache;

    private VisualTask visualTask;

    public SpotSetupManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    protected void onSetupStart(@NotNull Player player, @NotNull Spot spot) {
        this.cuboidCache = new Location[2];
        spot.getCuboid().ifPresent(cuboid -> {
            this.cuboidCache[0] = cuboid.getMin().clone();
            this.cuboidCache[1] = cuboid.getMax().clone();
        });

        this.visualTask = new VisualTask();
        this.visualTask.start();

        Inventory inventory = player.getInventory();
        inventory.setItem(0, SetupItemType.SPOT_CUBOID.getItem());
        inventory.setItem(8, SetupItemType.SPOT_SAVE.getItem());
    }

    @Override
    protected void onSetupEnd(@NotNull Player player, @NotNull Spot spot) {
        if (this.visualTask != null) {
            this.visualTask.stop();
            this.visualTask = null;
        }

        spot.getEditor().open(player, 1);
        this.cuboidCache = null;
    }

    @Override
    protected void handleItem(@NotNull PlayerInteractEvent e, @NotNull Player player, @NotNull Spot spot,
                              @NotNull ItemStack item, @NotNull SetupItemType itemType) {
        switch (itemType) {
            case SPOT_CUBOID -> {
                Block block = e.getClickedBlock();
                if (block == null || block.isEmpty()) return;

                Action action = e.getAction();
                Location location = block.getLocation();

                Spot overlap = spot.getArenaConfig().getSpotManager().getSpot(location);
                if (overlap != null && !spot.getId().equals(overlap.getId())) {
                    plugin.getMessage(Lang.SETUP_SPOT_CUBOID_ERROR_OVERLAP).replace(overlap.replacePlaceholders()).send(player);
                    return;
                }

                int index = action == Action.LEFT_CLICK_BLOCK ? 0 : 1;
                this.cuboidCache[index] = location;
                plugin.getMessage(Lang.SETUP_SPOT_CUBOID_SET).replace(spot.replacePlaceholders()).replace("%corner%", index + 1).send(player);
            }
            case SPOT_SAVE -> {
                if (this.cuboidCache[0] != null && this.cuboidCache[1] != null) {
                    ArenaCuboid cuboid = new ArenaCuboid(this.cuboidCache[0], this.cuboidCache[1]);
                    spot.setCuboid(cuboid);
                }

                spot.save();
                this.endSetup(player);
            }
        }
    }

    @Override
    protected void updateVisuals() {
        if (this.cuboidCache != null) {
            if (cuboidCache[0] != null) {
                ArenaSetupUtils.addVisualText(player, "&9« 1st Corner »", cuboidCache[0]);
                ArenaSetupUtils.addVisualBlock(player, cuboidCache[0]);
            }
            if (cuboidCache[1] != null) {
                ArenaSetupUtils.addVisualText(player, "&9« 2nd Corner »", cuboidCache[1]);
                ArenaSetupUtils.addVisualBlock(player, cuboidCache[1]);
            }
        }
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

    class VisualTask extends AbstractTask<AMA> {

        public VisualTask() {
            super(SpotSetupManager.this.plugin, 10L, true);
        }

        @Override
        public void action() {
            if (player == null) return;
            updateVisualParticles(player);
        }
    }
}
