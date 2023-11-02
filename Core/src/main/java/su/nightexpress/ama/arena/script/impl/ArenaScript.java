package su.nightexpress.ama.arena.script.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.editor.script.ScriptActionsEditor;
import su.nightexpress.ama.arena.editor.script.ScriptConditionsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.ScriptAction;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptCondition;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaScript implements ArenaChild {

    private final ArenaConfig arenaConfig;
    private final String      id;

    private final Map<String, List<ScriptPreparedCondition>> conditions;
    private final List<ScriptPreparedAction>                 actions;

    private GameEventType eventType;
    private boolean       inGameOnly;
    private ItemStack     icon;

    private ScriptActionsEditor actionsEditor;
    private ScriptConditionsEditor conditionsEditor;

    public ArenaScript(@NotNull ArenaConfig arenaConfig, @NotNull String id, @NotNull GameEventType eventType,
                       boolean inGameOnly,
                       @NotNull ItemStack icon) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();
        this.setEventType(eventType);
        this.setInGameOnly(inGameOnly);
        this.setIcon(icon);
        this.conditions = new HashMap<>();
        this.actions = new ArrayList<>();
    }

    public void clear() {
        if (this.actionsEditor != null) {
            this.actionsEditor.clear();
            this.actionsEditor = null;
        }
        if (this.conditionsEditor != null) {
            this.conditionsEditor.clear();
            this.conditionsEditor = null;
        }
    }

    @NotNull
    public ScriptActionsEditor getActionsEditor(@NotNull ScriptCategory category) {
        if (this.actionsEditor == null) {
            this.actionsEditor = new ScriptActionsEditor(category, this);
        }
        return actionsEditor;
    }

    @NotNull
    public ScriptConditionsEditor getConditionsEditor(@NotNull ScriptCategory category) {
        if (this.conditionsEditor == null) {
            this.conditionsEditor = new ScriptConditionsEditor(category, this);
        }
        return conditionsEditor;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public GameEventType getEventType() {
        return eventType;
    }

    public void setEventType(@NotNull GameEventType eventType) {
        this.eventType = eventType;
    }

    public boolean isInGameOnly() {
        return inGameOnly;
    }

    public void setInGameOnly(boolean inGameOnly) {
        this.inGameOnly = inGameOnly;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }

    @NotNull
    public Map<String, List<ScriptPreparedCondition>> getConditions() {
        return conditions;
    }

    @NotNull
    public List<ScriptPreparedAction> getActions() {
        return actions;
    }

    public boolean onArenaEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (this.getEventType() != gameEvent.getEventType()) return false;
        //System.out.println("process script for " + gameEvent.getEventType().name());

        if (!this.conditions.isEmpty() && this.conditions.values().stream().noneMatch(list -> {
            return list.stream().allMatch(entry -> {
                ScriptCondition<?, ?> condition = entry.getCondition();
                Object value = entry.getValue();
                ScriptCondition.Operator operator = entry.getOperator();
                return condition.test(gameEvent, value, operator);
            });
        })) return false;

        //System.out.println("process script 4");
        this.actions.forEach(entry -> {
            //System.out.println("process script action");
            ScriptAction action = entry.getAction();
            ParameterResult parameterResult = entry.getParameters();
            action.run(gameEvent, parameterResult);
        });

        return true;
    }
}
