package su.nightexpress.ama.arena.script.condition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ScriptPreparedCondition {

    private final ScriptCondition<?, ?> condition;
    private final Object                value;
    private final ScriptCondition.Operator operator;

    public ScriptPreparedCondition(
        @NotNull ScriptCondition<?, ?> condition,
        @NotNull Object value,
        @NotNull ScriptCondition.Operator operator) {
        this.condition = condition;
        this.value = value;
        this.operator = operator;
    }

    @Nullable
    public static ScriptPreparedCondition parse(@NotNull String str) {
        String[] split = str.split(" ");
        if (split.length < 3) return null;

        ScriptCondition<?, ?> condition = ScriptConditions.getByName(split[0].replace("[", "").replace("]", ""));
        if (condition == null) return null;

        Object value = condition.getParser().apply(split[2]);
        if (value == null) return null;

        ScriptCondition.Operator operator = ScriptCondition.Operator.fromString(split[1]).orElse(ScriptCondition.Operator.EQUAL);
        return new ScriptPreparedCondition(condition, value, operator);
    }

    @NotNull
    public String toRaw() {
        String prefix = "[" + this.getCondition().getName() + "]";
        String oper = this.getOperator().getRaw();
        String value;
        if (this.getValue() instanceof Number[] array) {
            value = String.join(",", Arrays.stream(array).map(String::valueOf).toList());
        }
        else {
            value = String.valueOf(this.getValue());
        }
        return prefix + " " + oper + " " + value;
    }

    @NotNull
    public ScriptCondition<?, ?> getCondition() {
        return condition;
    }

    @NotNull
    public ScriptCondition.Operator getOperator() {
        return operator;
    }

    @NotNull
    public Object getValue() {
        return value;
    }
}
