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
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.editor.spot.SpotSettingsEditor;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.Parameters;
import su.nightexpress.ama.arena.script.action.ScriptActions;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.util.ArenaCuboid;

import java.util.*;

public class ArenaSpot extends AbstractConfigHolder<AMA> implements ArenaChild, Problematic, Placeholder {

    private final ArenaConfig arenaConfig;
    private final Map<String, ArenaSpotState> states;
    private final PlaceholderMap placeholderMap;

    private boolean     isActive;
    private String      name;
    private ArenaCuboid cuboid;

    private SpotSettingsEditor editor;

    public ArenaSpot(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.states = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
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

            // ----------- CONVERT SCRIPTS START -----------
            for (String eventRaw : cfg.getSection(path2 + "Triggers")) {
                ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                if (eventType == null) continue;

                String sName = "spot_change_" + this.getId() + "_to_" + stateId;
                ArenaScript script = new ArenaScript(this.arenaConfig, sName, eventType);

                String values = cfg.getString(path2 + "Triggers." + eventRaw, "");
                Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                script.getConditions().putAll(conditions);

                ScriptPreparedAction action = new ScriptPreparedAction(ScriptActions.CHANGE_SPOT, new ParameterResult());
                action.getParameters().add(Parameters.SPOT, this.getId());
                action.getParameters().add(Parameters.STATE,stateId);
                script.getActions().add(action);

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            cfg.remove(path2 + "Triggers");
            // ----------- CONVERT SCRIPTS END -----------

            List<String> blockSchemeRaw = new ArrayList<>(cfg.getStringList(path2 + "Scheme"));
            ArenaSpotState state = new ArenaSpotState(this, stateId, blockSchemeRaw);
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

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getCuboid().isEmpty()) {
            list.add(problem("Invalid Cuboid Selection!"));
        }
        if (this.getStates().isEmpty()) {
            list.add(problem("No Spot States!"));
        }

        return list;
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
