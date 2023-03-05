package su.nightexpress.ama.arena.game.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameConditionList {

    private final Set<ArenaGameEventType> checkTriggers;
    private final List<GameConditionEntry> conditions;

    public GameConditionList() {
        this.checkTriggers = new HashSet<>();
        this.conditions = new ArrayList<>();
    }

    @NotNull
    public Set<ArenaGameEventType> getCheckTriggers() {
        return checkTriggers;
    }

    public <T> void addCondition(@NotNull GameCondition<T> condition, @NotNull T value, @NotNull GameCondition.Operator operator) {
        this.addCondition(new GameConditionEntry(condition, value, operator));
    }

    public void addCondition(@NotNull GameConditionEntry entry) {
        this.conditions.add(entry);
    }

    public boolean isApplicableFor(@NotNull ArenaGameGenericEvent event) {
        return this.getCheckTriggers().contains(event.getEventType());
    }

    public boolean test(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.getCheckTriggers().contains(gameEvent.getEventType())) return true;
        if (this.conditions.isEmpty()) return true;

        return this.conditions.stream().allMatch(entry -> {
            GameCondition<?> condition = entry.getCondition();
            Object value = entry.getValue();
            GameCondition.Operator operator = entry.getOperator();
            return condition.test(gameEvent, value, operator);
        });
    }
}
