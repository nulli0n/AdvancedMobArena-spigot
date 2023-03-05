package su.nightexpress.ama.arena.game.condition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameConditionEntry {

    private final GameCondition<?>       condition;
    private final Object value;
    private final GameCondition.Operator operator;

    public GameConditionEntry(
        @NotNull GameCondition<?> condition,
        @NotNull Object value,
        @NotNull GameCondition.Operator operator) {
        this.condition = condition;
        this.value = value;
        this.operator = operator;
    }

    @Nullable
    public static GameConditionEntry parse(@NotNull String str) {
        String[] split = str.split(" ");
        if (split.length < 3) return null;

        GameCondition<?> condition = GameConditions.getByName(split[0]);
        if (condition == null) return null;

        Object value = condition.getParser().apply(split[2]);
        if (value == null) return null;

        GameCondition.Operator operator = GameCondition.Operator.fromString(split[1]).orElse(GameCondition.Operator.EQUAL);
        return new GameConditionEntry(condition, value, operator);
    }

    @NotNull
    public GameCondition<?> getCondition() {
        return condition;
    }

    @NotNull
    public GameCondition.Operator getOperator() {
        return operator;
    }

    @NotNull
    public Object getValue() {
        return value;
    }
}
