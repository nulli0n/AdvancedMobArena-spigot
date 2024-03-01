package su.nightexpress.ama.arena.spot;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.event.ArenaSpotStateChangeEvent;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.ArrayList;
import java.util.List;

public class SpotState implements ArenaChild, Placeholder {

    private final AMA             plugin;
    private final Spot            spot;
    private final String          id;
    private final List<SpotBlock> scheme;
    private final PlaceholderMap  placeholderMap;

    public SpotState(
        @NotNull AMA plugin,
        @NotNull Spot spot,
        @NotNull String id,
        @NotNull List<String> schemeRaw
    ) {
        this.plugin = plugin;
        this.spot = spot;
        this.id = id.toLowerCase();
        this.scheme = new ArrayList<>();
        this.readScheme(schemeRaw);

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

    public void createScheme(@NotNull List<Block> blocks) {
        this.setScheme(blocks.stream().map(block -> SpotBlock.fromBlock(plugin, block)).toList());
    }

    public void readScheme(@NotNull List<String> schemeRaw) {
        List<SpotBlock> blocks = new ArrayList<>();
        for (String rawData : schemeRaw) {
            SpotBlock spotBlock = SpotBlock.fromString(plugin, rawData);
            if (spotBlock == null) {
                this.plugin.error("Invalid spot block data '" + rawData + "' in '" + id + "' state of '" + spot.getFile().getName() + "' spot in '" + spot.getArenaConfig().getId() + "' arena!");
                continue;
            }

            blocks.add(spotBlock);
        }
        this.setScheme(blocks);
    }

    @NotNull
    public List<SpotBlock> getScheme() {
        return scheme;
    }

    public void setScheme(@NotNull List<SpotBlock> blocks) {
        this.scheme.clear();
        this.scheme.addAll(blocks);
    }

    public void build() {
        World world = this.getArenaConfig().getWorld();
        this.getScheme().forEach(spotBlock -> {
            spotBlock.setBlock(this.plugin, world);
        });
    }

    public void build(@NotNull Arena arena) {
        this.build();

        ArenaSpotStateChangeEvent event = new ArenaSpotStateChangeEvent(arena, this.getSpot(), this);
        this.plugin.getPluginManager().callEvent(event);
    }
}
