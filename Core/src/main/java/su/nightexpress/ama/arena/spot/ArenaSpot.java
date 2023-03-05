package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.*;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.editor.spot.EditorSpotMain;

import java.util.*;
import java.util.function.UnaryOperator;

public class ArenaSpot extends AbstractLoadableItem<AMA> implements ArenaChild, IEditable, ICleanable, IProblematic {

    private final ArenaConfig arenaConfig;

    private       boolean                     isActive;
    private       String                      name;
    private       ArenaCuboid                 cuboid;
    private final Map<String, ArenaSpotState> states;

    private EditorSpotMain editor;

    public ArenaSpot(@NotNull ArenaConfig arenaConfig, @NotNull String path, @NotNull ArenaCuboid cuboid) {
        super(arenaConfig.plugin(), path);
        this.arenaConfig = arenaConfig;

        this.setActive(false);
        this.setName(StringUtil.capitalizeFully(this.getId()) + " Spot");
        this.setCuboid(cuboid);
        this.states = new HashMap<>();
    }

    public ArenaSpot(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;

        this.setActive(cfg.getBoolean("Enabled"));
        this.setName(cfg.getString("Name", this.getId()));
        this.states = new HashMap<>();

        Location from = cfg.getLocation("Bounds.From");
        Location to = cfg.getLocation("Bounds.To");
        if (from == null || to == null) {
            plugin.error("Invalid cuboid bounds in '" + getId() + "' spot of '" + arenaConfig.getId() + "' arena!");
            this.setCuboid(ArenaCuboid.empty());
        }
        else {
            this.setCuboid(new ArenaCuboid(from, to));
        }

        for (String stateId : cfg.getSection("States")) {
            String path2 = "States." + stateId + ".";

            Set<ArenaGameEventTrigger<?>> triggers = ArenaGameEventTrigger.parse(cfg, path2 + "Triggers");
            List<String> blockSchemeRaw = new ArrayList<>(cfg.getStringList(path2 + "Scheme"));

            ArenaSpotState state = new ArenaSpotState(this, stateId, triggers, blockSchemeRaw);
            this.states.put(state.getId(), state);
        }
    }

    @Override
    public void onSave() {
        cfg.set("Enabled", this.isActive());
        cfg.set("Name", this.getName());

        cfg.set("Bounds", null);
        if (!this.cuboid.isEmpty()) {
            cfg.set("Bounds.From", cuboid.getLocationMin());
            cfg.set("Bounds.To", cuboid.getLocationMax());
        }

        cfg.set("States", null);
        this.states.forEach((id, state) -> {
            String path2 = "States." + id + ".";

            state.getTriggers().forEach(trigger -> {
                cfg.set(path2 + "Triggers." + trigger.getType().name(), trigger.getValuesRaw());
            });
            cfg.set(path2 + "Scheme", state.getSchemeRaw());
        });
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getCuboid().isEmpty()) {
            list.add("Invalid Cuboid Selection!");
        }
        if (this.getStates().isEmpty()) {
            list.add("No Spot States!");
        }

        return list;
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()))
            .replace(Placeholders.SPOT_ID, this.getId())
            .replace(Placeholders.SPOT_NAME, this.getName())
            .replace(Placeholders.SPOT_ACTIVE, LangManager.getBoolean(this.isActive()))
            ;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @Override
    @NotNull
    public EditorSpotMain getEditor() {
        if (this.editor == null) {
            this.editor = new EditorSpotMain(this);
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
        this.name = StringUtil.color(name);
    }

    @NotNull
    public ArenaCuboid getCuboid() {
        return this.cuboid;
    }

    public void setCuboid(@NotNull ArenaCuboid cuboid) {
        this.cuboid = cuboid;
    }

    @NotNull
    public Map<String, ArenaSpotState> getStates() {
        return this.states;
    }

    @Nullable
    public ArenaSpotState getState(@NotNull String id) {
        return this.getStates().get(id.toLowerCase());
    }

    public void setState(@NotNull Arena arena, @NotNull String id) {
        ArenaSpotState state = this.getState(id);
        if (state == null) return;
        state.build(arena);
    }
}
