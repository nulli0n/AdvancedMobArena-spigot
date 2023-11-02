package su.nightexpress.ama.arena.script.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.editor.script.ScriptsCategoryEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;

import java.util.*;

public class ScriptCategory extends AbstractConfigHolder<AMA> implements ArenaChild {

    private final ArenaConfig arenaConfig;
    private final Map<String, ArenaScript> scripts;

    private ScriptsCategoryEditor editor;

    public ScriptCategory(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.scripts = new HashMap<>();
    }

    @Override
    public boolean load() {
        for (String scriptId : cfg.getSection("")) {
            String path2 = scriptId + ".";
            GameEventType eventType = cfg.getEnum(path2 + "Event", GameEventType.class);
            if (eventType == null) {
                this.plugin().warn("Invalid event type in '" + scriptId + "' arena script.");
                continue;
            }

            boolean inGameOnly = cfg.getBoolean(path2 + "InGameOnly", true);
            ItemStack icon = cfg.getItem(path2 + "Icon");
            if (icon.getType().isAir()) icon = new ItemStack(Material.MAP);

            ArenaScript script = new ArenaScript(this.arenaConfig, scriptId, eventType, inGameOnly, icon);

            for (String conditionId : cfg.getSection(path2 + "Conditions")) {
                List<ScriptPreparedCondition> entries = new ArrayList<>();
                for (String condition : cfg.getStringList(path2 + "Conditions." + conditionId)) {
                    ScriptPreparedCondition entry = ScriptPreparedCondition.parse(condition);
                    if (entry == null) {
                        this.plugin().warn("Could not parse script condition: '" + condition + "'!");
                    }
                    else entries.add(entry);
                }
                script.getConditions().put(conditionId.toLowerCase(), entries);
            }

            for (String action : cfg.getStringList(path2 + "Actions")) {
                ScriptPreparedAction entry = ScriptPreparedAction.parse(action);
                if (entry == null) {
                    this.plugin().warn("Could not parse script action: '" + action + "'!");
                }
                else script.getActions().add(entry);
            }

            this.getScriptsMap().put(script.getId(), script);
        }
        return true;
    }

    @Override
    public void onSave() {
        cfg.getSection("").forEach(cfg::remove);

        this.getScripts().forEach(script -> {
            String path = script.getId() + ".";

            cfg.set(path + "Event", script.getEventType().name());
            cfg.set(path + "InGameOnly", script.isInGameOnly());
            cfg.setItem(path + "Icon", script.getIcon());
            script.getConditions().forEach((key, value) -> {
                cfg.set(path + "Conditions." + key, value.stream().map(ScriptPreparedCondition::toRaw).toList());
            });
            cfg.set(path + "Actions", script.getActions().stream().map(ScriptPreparedAction::toRaw).toList());
        });
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getScripts().forEach(ArenaScript::clear);
        this.getScripts().clear();
    }

    @NotNull
    public ScriptsCategoryEditor getEditor() {
        if (this.editor == null) {
            this.editor = new ScriptsCategoryEditor(this);
        }
        return editor;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public Map<String, ArenaScript> getScriptsMap() {
        return scripts;
    }

    @NotNull
    public Collection<ArenaScript> getScripts() {
        return this.scripts.values();
    }

    @NotNull
    public Optional<ArenaScript> getScript(@NotNull String id) {
        return Optional.ofNullable(this.getScriptsMap().get(id.toLowerCase()));
    }

    public boolean createScript(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);

        if (this.getScript(id).isPresent()) return false;

        ArenaScript script = new ArenaScript(this.getArenaConfig(), id, GameEventType.WAVE_START, true, new ItemStack(Material.MAP));
        this.getScriptsMap().put(script.getId(), script);
        this.save();
        return true;
    }
}
