package su.nightexpress.ama.api.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.hologram.HologramManager;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface HologramHolder {

    @NotNull
    Set<Location> getHologramLocations();

    @NotNull
    Set<UUID> getHologramIds();

    @NotNull
    List<String> getHologramFormat();

    @NotNull
    HologramType getHologramType();

    default void createHolograms() {
        HologramManager holograms = ArenaAPI.PLUGIN.getHologramManager();
        if (holograms == null) return;

        List<String> text = this.getHologramFormat();
        if (text.isEmpty()) return;

        this.getHologramLocations().forEach(location -> {
            this.getHologramIds().add(holograms.create(this.getHologramType(), location, text));
        });
    }

    default void removeHolograms() {
        HologramManager holograms = ArenaAPI.PLUGIN.getHologramManager();
        if (holograms == null) return;

        this.getHologramIds().forEach(id -> holograms.delete(this.getHologramType(), id));
        this.getHologramIds().clear();
    }

    default void setHologramClick(@NotNull IHologramClick click) {
        HologramManager holograms = ArenaAPI.PLUGIN.getHologramManager();
        if (holograms == null) return;

        this.getHologramIds().forEach(id -> holograms.setClick(this.getHologramType(), id, click));
    }

    default void updateHolograms() {
        this.removeHolograms();
        this.createHolograms();
    }
}
