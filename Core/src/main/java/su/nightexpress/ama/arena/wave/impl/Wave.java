package su.nightexpress.ama.arena.wave.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.arena.editor.wave.WaveMobsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Wave implements ArenaChild, Placeholder {

    private final ArenaConfig    arenaConfig;
    private final String         id;
    private final Set<WaveMob>   mobs;
    private final PlaceholderMap placeholderMap;

    private ItemStack icon;

    private WaveMobsEditor editor;

    public Wave(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull Set<WaveMob> mobs
    ) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();
        this.mobs = new HashSet<>(mobs);
        this.setIcon(new ItemStack(Material.BLAZE_POWDER));

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.ARENA_WAVE_ID, this::getId)
            .add(Placeholders.ARENA_WAVE_MOBS, () -> {
                return this.getMobs().stream().map(WaveMob::getMobId).map(Report::good).collect(Collectors.joining("\n"));
            })
        ;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public WaveMobsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new WaveMobsEditor(this.getArenaConfig().plugin(), this);
        }
        return this.editor;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Set<WaveMob> getMobs() {
        return this.mobs;
    }

    @NotNull
    public List<WaveMob> getMobsByChance() {
        return this.getMobs().stream().filter(mob -> mob.getAmount() > 0 && Rnd.chance(mob.getChance())).toList();
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        if (icon.getType().isAir()) icon = new ItemStack(Material.BLAZE_POWDER);

        this.icon = new ItemStack(icon);
    }
}
