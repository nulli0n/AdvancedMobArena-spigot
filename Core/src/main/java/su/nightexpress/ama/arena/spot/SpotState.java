package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.event.ArenaSpotStateChangeEvent;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotState implements ArenaChild, Placeholder {

    private final Spot   spot;
    private final String id;
    private final PlaceholderMap placeholderMap;

    private final List<String>             schemeRaw;
    private final Map<Location, BlockData> scheme;

    public SpotState(
        @NotNull Spot spot,
        @NotNull String id,
        @NotNull List<String> schemeRaw
    ) {
        this.spot = spot;
        this.id = id.toLowerCase();
        this.scheme = new HashMap<>();
        this.schemeRaw = new ArrayList<>();
        this.setSchemeRaw(schemeRaw);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.SPOT_STATE_ID, this::getId);
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Spot getSpot() {
        return this.spot;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.getSpot().getArenaConfig();
    }

    public void setSchemeRaw(@NotNull List<String> schemeRaw) {
        this.schemeRaw.clear();
        this.scheme.clear();

        AMA plugin = this.getSpot().plugin();
        for (String rawData : schemeRaw) {
            String[] blockSplit = rawData.split("~");
            if (blockSplit.length != 2) {
                plugin.error("Invalid block '" + rawData + "' in '" + id + "' state of '" + spot.getFile().getName() + "' spot in '" + spot.getArenaConfig().getId() + "' arena!");
                continue;
            }
            Location blockLoc = LocationUtil.deserialize(blockSplit[0]);
            if (blockLoc == null) {
                plugin.error("Invalid block location '" + rawData + "' in '" + id + "' state of '" + spot.getFile().getName() + "' spot in '" + spot.getArenaConfig().getId() + "' arena!");
                continue;
            }
            if (this.spot.getCuboid().isEmpty() || !this.spot.getCuboid().get().contains(blockLoc)) {
                plugin.error("Block is outside of the spot region: '" + rawData + "' in '" + id + "' state of '" + spot.getFile().getName() + "' spot in '" + spot.getArenaConfig().getId() + "' arena!");
                continue;
            }

            try {
                BlockData blockData = plugin.getServer().createBlockData(blockSplit[1]);
                this.scheme.put(blockLoc, blockData);
                this.schemeRaw.add(rawData);
            }
            catch (IllegalArgumentException exception) {
                plugin.warn("Could not create block data from string: '" + blockSplit[1] + "'.");
                //exception.printStackTrace();
            }
        }
    }

    @NotNull
    public List<String> getSchemeRaw() {
        return this.schemeRaw;
    }

    @NotNull
    public Map<Location, BlockData> getScheme() {
        return scheme;
    }

    public void build() {
        this.getScheme().forEach((location, data) -> {
            Block block = location.getBlock();
            if (block.getBlockData().matches(data)) return;
            block.setBlockData(data);
        });
    }

    public void build(@NotNull Arena arena) {
        this.build();

        ArenaSpotStateChangeEvent event = new ArenaSpotStateChangeEvent(arena, this.getSpot(), this);
        this.getSpot().plugin().getPluginManager().callEvent(event);
    }
}
