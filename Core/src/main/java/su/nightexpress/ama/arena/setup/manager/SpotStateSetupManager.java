package su.nightexpress.ama.arena.setup.manager;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.arena.editor.spot.SpotSettingsEditor;
import su.nightexpress.ama.arena.setup.ArenaSetupUtils;
import su.nightexpress.ama.arena.setup.SetupItemType;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.spot.ArenaSpotState;
import su.nightexpress.ama.config.Lang;

import java.util.ArrayList;
import java.util.List;

public class SpotStateSetupManager extends AbstractSetupManager<ArenaSpotState> {

    private VisualTask visualTask;

    public SpotStateSetupManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    protected void onSetupStart(@NotNull Player player, @NotNull ArenaSpotState state) {
        this.visualTask = new VisualTask();
        this.visualTask.start();

        Inventory inventory = player.getInventory();
        inventory.setItem(6, SetupItemType.SPOT_STATE_PREVIEW.getItem());
        inventory.setItem(7, SetupItemType.SPOT_SAVE.getItem());
        inventory.setItem(8, SetupItemType.SPOT_STATE_EXIT.getItem());
    }

    @Override
    protected void onSetupEnd(@NotNull Player player, @NotNull ArenaSpotState state) {
        if (this.visualTask != null) {
            this.visualTask.stop();
            this.visualTask = null;
        }

        SpotSettingsEditor spotMain = state.getSpot().getEditor();
        spotMain.getStatesEditor().open(player, 1);
    }

    @Override
    protected void handleItem(@NotNull PlayerInteractEvent e, @NotNull Player player, @NotNull ArenaSpotState state, @NotNull ItemStack item, @NotNull SetupItemType itemType) {
        switch (itemType) {
            case SPOT_STATE_PREVIEW -> state.build();
            case SPOT_STATE_EXIT -> this.endSetup(player);
            case SPOT_SAVE -> {
                ArenaCuboid cuboid = state.getSpot().getCuboid().orElse(null);
                if (cuboid != null) {
                    List<String> scheme = new ArrayList<>();
                    for (Block block : cuboid.getBlocks()) {
                        String bLoc = LocationUtil.serialize(block.getLocation());
                        String bData = block.getBlockData().getAsString();
                        scheme.add(bLoc + "~" + bData);
                    }
                    state.setSchemeRaw(scheme);
                    state.getSpot().save();
                }
                this.endSetup(player);
            }
        }
    }

    @Override
    protected void updateVisuals() {
        ArenaCuboid cuboid = this.getObject().getSpot().getCuboid().orElse(null);
        if (cuboid == null) return;

        Location loc1 = cuboid.getMin();
        Location loc2 = cuboid.getMax();

        ArenaSetupUtils.addVisualText(player, "&9« 1st Corner »", loc1);
        ArenaSetupUtils.addVisualBlock(player, loc1);
        ArenaSetupUtils.addVisualText(player, "&9« 2nd Corner »", loc2);
        ArenaSetupUtils.addVisualBlock(player, loc2);
    }

    private void updateVisualParticles(@NotNull Player player) {
        this.getObject().getSpot().getCuboid().ifPresent(cuboid -> {
            cuboid.getVisualizer().draw(player);
        });
    }

    @Override
    protected void removeVisuals() {
        ArenaSetupUtils.removeVisuals(this.player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpotBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (!this.isEditing(player)) return;

        ArenaSpot spot = this.getObject().getSpot();

        Block block = e.getBlock();
        ArenaCuboid cuboid = spot.getCuboid().orElse(null);
        if (cuboid == null || !cuboid.contains(block.getLocation())) {
            plugin.getMessage(Lang.SETUP_SPOT_STATE_ERROR_OUTSIDE).send(player);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpotBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!this.isEditing(player)) return;

        ArenaSpot spot = this.getObject().getSpot();

        Block block = e.getBlock();
        ArenaCuboid cuboid = spot.getCuboid().orElse(null);
        if (cuboid == null || !cuboid.contains(block.getLocation())) {
            plugin.getMessage(Lang.SETUP_SPOT_STATE_ERROR_OUTSIDE).send(player);
            e.setCancelled(true);
        }
    }

    class VisualTask extends AbstractTask<AMA> {

        public VisualTask() {
            super(SpotStateSetupManager.this.plugin, 10L, true);
        }

        @Override
        public void action() {
            if (player == null) return;
            updateVisualParticles(player);
        }
    }
}
