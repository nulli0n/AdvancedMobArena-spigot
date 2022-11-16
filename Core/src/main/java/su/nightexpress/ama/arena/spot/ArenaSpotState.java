package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaSpotStateChangeEvent;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.config.ArenaConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ArenaSpotState implements IArenaGameEventListener, IArenaObject {

    private final ArenaSpot spot;

    private final String                        id;
    private final Set<ArenaGameEventTrigger<?>> triggers;
    private       List<String>                  schemeRaw;
    private final Map<Location, BlockData>      scheme;

    public ArenaSpotState(
        @NotNull ArenaSpot spot,
        @NotNull String id,
        @NotNull Set<ArenaGameEventTrigger<?>> triggers,
        @NotNull List<String> schemeRaw
    ) {
        this.spot = spot;
        this.id = id.toLowerCase();

        this.triggers = triggers;
        this.scheme = new HashMap<>();
        this.setSchemeRaw(schemeRaw);
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.SPOT_STATE_TRIGGERS, Placeholders.format(this.getTriggers()))
            .replace(Placeholders.SPOT_STATE_ID, this.getId())
            ;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public ArenaSpot getSpot() {
        return this.spot;
    }

    @NotNull
    @Override
    public Set<ArenaGameEventTrigger<?>> getTriggers() {
        return triggers;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        this.build(gameEvent.getArena());
        return true;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.getSpot().getArenaConfig();
    }

    public void setSchemeRaw(@NotNull List<String> schemeRaw) {
        this.schemeRaw = schemeRaw;
        this.scheme.clear();

        AMA plugin = this.getSpot().plugin();
        for (String block : schemeRaw) {
            String[] blockSplit = block.split("~");
            if (blockSplit.length != 2) {
                plugin.error("Invalid block '" + block + "' in '" + id + "' state of '" + spot.getFile().getName() + "' spot in '" + spot.getArenaConfig().getId() + "' arena!");
                continue;
            }
            Location blockLoc = LocationUtil.deserialize(blockSplit[0]);
            if (blockLoc == null) {
                plugin.error("Invalid block location '" + block + "' in '" + id + "' state of '" + spot.getFile().getName() + "' spot in '" + spot.getArenaConfig().getId() + "' arena!");
                continue;
            }
            if (this.spot.getCuboid().isEmpty() || !this.spot.getCuboid().contains(blockLoc)) {
                plugin.error("Block is outside of the spot region: '" + block + "' in '" + id + "' state of '" + spot.getFile().getName() + "' spot in '" + spot.getArenaConfig().getId() + "' arena!");
                continue;
            }

            BlockData blockData = plugin.getServer().createBlockData(blockSplit[1]);
            this.scheme.put(blockLoc, blockData);
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

    public void build(@NotNull AbstractArena arena) {
        this.build();

        ArenaSpotStateChangeEvent event = new ArenaSpotStateChangeEvent(arena, this.getSpot(), this);
        this.getSpot().plugin().getPluginManager().callEvent(event);
    }
}
