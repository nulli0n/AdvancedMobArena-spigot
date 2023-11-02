package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.arena.editor.spot.SpotSettingsEditor;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.util.ArenaCuboid;

import java.util.*;

public class ArenaSpot extends AbstractConfigHolder<AMA> implements ArenaChild, Inspectable, Placeholder {

    private final ArenaConfig            arenaConfig;
    private final Map<String, SpotState> states;
    private final PlaceholderMap         placeholderMap;

    private boolean     isActive;
    private String      name;
    private ArenaCuboid cuboid;

    private SpotSettingsEditor editor;

    public ArenaSpot(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.states = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.SPOT_REPORT, () -> String.join("\n", this.getReport().getFullReport()))
            .add(Placeholders.SPOT_ID, this::getId)
            .add(Placeholders.SPOT_NAME, this::getName)
            .add(Placeholders.SPOT_ACTIVE, () -> LangManager.getBoolean(this.isActive()))
        ;
    }

    @Override
    public boolean load() {
        this.setActive(cfg.getBoolean("Enabled"));
        this.setName(cfg.getString("Name", this.getId()));

        Location from = cfg.getLocation("Bounds.From");
        Location to = cfg.getLocation("Bounds.To");
        if (from != null && to != null) {
            this.setCuboid(new ArenaCuboid(from, to));
        }

        for (String stateId : cfg.getSection("States")) {
            String path2 = "States." + stateId + ".";
            List<String> blockSchemeRaw = new ArrayList<>(cfg.getStringList(path2 + "Scheme"));
            SpotState state = new SpotState(this, stateId, blockSchemeRaw);
            this.states.put(state.getId(), state);
        }
        cfg.saveChanges();
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Enabled", this.isActive());
        cfg.set("Name", this.getName());

        cfg.set("Bounds", null);
        this.getCuboid().ifPresent(cuboid -> {
            cfg.set("Bounds.From", cuboid.getMin());
            cfg.set("Bounds.To", cuboid.getMax());
        });

        cfg.set("States", null);
        this.states.forEach((id, state) -> {
            String path2 = "States." + id + ".";
            cfg.set(path2 + "Scheme", state.getSchemeRaw());
        });
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        if (this.getCuboid().isEmpty()) {
            report.addProblem("Invalid selection!");
        }
        if (this.getStates().isEmpty()) {
            report.addWarn("No states!");
        }

        return report;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public SpotSettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SpotSettingsEditor(this);
        }
        return this.editor;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public Optional<ArenaCuboid> getCuboid() {
        return Optional.ofNullable(this.cuboid);
    }

    public void setCuboid(@Nullable ArenaCuboid cuboid) {
        this.cuboid = cuboid;
    }

    @NotNull
    public Map<String, SpotState> getStates() {
        return this.states;
    }

    @Nullable
    public SpotState getState(@NotNull String id) {
        return this.getStates().get(id.toLowerCase());
    }

    public void setState(@NotNull Arena arena, @NotNull String id) {
        SpotState state = this.getState(id);
        if (state == null) return;
        state.build(arena);
    }
}
