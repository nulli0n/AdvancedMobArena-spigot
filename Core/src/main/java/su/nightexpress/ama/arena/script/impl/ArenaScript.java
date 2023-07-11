package su.nightexpress.ama.arena.script.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.editor.script.ScriptActionsEditor;
import su.nightexpress.ama.arena.editor.script.ScriptConditionsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.ScriptAction;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptCondition;
import su.nightexpress.ama.arena.script.condition.ScriptConditions;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;

import java.util.*;

public class ArenaScript implements ArenaChild {

    private final ArenaConfig arenaConfig;
    private final String id;
    private ArenaGameEventType                               eventType;
    private final Map<String, List<ScriptPreparedCondition>> conditions;
    private final List<ScriptPreparedAction>                 actions;

    private ScriptActionsEditor actionsEditor;
    private ScriptConditionsEditor conditionsEditor;

    public ArenaScript(@NotNull ArenaConfig arenaConfig, @NotNull String id, @NotNull ArenaGameEventType eventType) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();
        this.setEventType(eventType);
        this.conditions = new HashMap<>();
        this.actions = new ArrayList<>();
    }

    @NotNull
    public static Map<String, List<ScriptPreparedCondition>> ofGameTrigger(@NotNull ArenaGameEventType eventType, @NotNull String values) {
        Map<String, List<ScriptPreparedCondition>> map = new HashMap<>();

        //for (String eventName : cfg.getSection(path)) {
            //ArenaGameEventType eventType = StringUtil.getEnum(eventName, ArenaGameEventType.class).orElse(null);
            //if (eventType == null) continue;

            //String values = cfg.getString(path + "." + eventName, "");
            String[] sections = values.split(" OR ");

            ScriptCondition<?, ?> condition = switch (eventType) {
                case WAVE_END, WAVE_START -> ScriptConditions.WAVE_NUMBER;
                case SHOP_LOCKED -> ScriptConditions.SHOP_LOCKED;
                case SHOP_UNLOCKED -> ScriptConditions.SHOP_UNLOCKED;
                case REGION_LOCKED -> ScriptConditions.REGION_LOCKED;
                case REGION_UNLOCKED -> ScriptConditions.REGION_UNLOCKED;
                case SHOP_ITEM_LOCKED -> ScriptConditions.SHOP_PRODUCT_LOCKED;
                case SHOP_ITEM_UNLOCKED -> ScriptConditions.SHOP_PRODUCT_UNLOCKED;
                case SHOP_CATEGORY_LOCKED -> ScriptConditions.SHOP_CATEGORY_LOCKED;
                case SHOP_CATEGORY_UNLOCKED -> ScriptConditions.SHOP_CATEGORY_UNLOCKED;
                default -> null;
            };

            if (condition == null) return map;

            for (String valuesPart : sections) {
                String[] singles = values.split(",");

                List<ScriptPreparedCondition> conditions = new ArrayList<>();
                String sectionId = UUID.randomUUID().toString();

                for (String single : singles) {
                    ScriptCondition.Operator operator = ScriptCondition.Operator.EQUAL;
                    for (ScriptCondition.Operator operator1 : ScriptCondition.Operator.values()) {
                        if (single.startsWith(operator1.getRaw())) {
                            single = single.substring(operator1.getRaw().length());
                            operator = operator1;
                            break;
                        }
                    }

                    ScriptPreparedCondition preparedCondition = new ScriptPreparedCondition(condition, condition.getParser().apply(single), operator);
                    conditions.add(preparedCondition);
                }

                map.put(sectionId, conditions);
            }
        //}

        return map;
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
    public ArenaGameEventType getEventType() {
        return eventType;
    }

    public void setEventType(@NotNull ArenaGameEventType eventType) {
        this.eventType = eventType;
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
