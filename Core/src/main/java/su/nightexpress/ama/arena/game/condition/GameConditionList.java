package su.nightexpress.ama.arena.game.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.script.condition.ScriptCondition;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Deprecated
public class GameConditionList {

    private final Set<ArenaGameEventType>       checkTriggers;
    private final List<ScriptPreparedCondition> conditions;

    public GameConditionList() {
        this.checkTriggers = new HashSet<>();
        this.conditions = new ArrayList<>();
    }

    @NotNull
    public Set<ArenaGameEventType> getCheckTriggers() {
        return checkTriggers;
    }

    public <T, E> void addCondition(@NotNull ScriptCondition<T, E> condition, @NotNull T value, @NotNull ScriptCondition.Operator operator) {
        this.addCondition(new ScriptPreparedCondition(condition, value, operator));
    }

    public void addCondition(@NotNull ScriptPreparedCondition entry) {
        this.conditions.add(entry);
    }

    public boolean isApplicableFor(@NotNull ArenaGameGenericEvent event) {
        return this.getCheckTriggers().contains(event.getEventType());
    }

    public boolean test(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.getCheckTriggers().contains(gameEvent.getEventType())) return true;
        if (this.conditions.isEmpty()) return true;

        return this.conditions.stream().allMatch(entry -> {
            ScriptCondition<?, ?> condition = entry.getCondition();
            Object value = entry.getValue();
            ScriptCondition.Operator operator = entry.getOperator();
            return condition.test(gameEvent, value, operator);
        });
    }
}
