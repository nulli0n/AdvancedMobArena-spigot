package su.nightexpress.ama.api.arena.game;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.data.Pair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ArenaGameTriggerValue<T> {

    protected final String  valueRaw;
    protected final List<Pair<T, Operator<T>>> values;
    protected final Map<String, Operator<T>> operators;
    protected final Predicate<T> predicate;

    protected static final String OPERATOR_NEGATE = "!";
    protected static final String OPERATOR_INTERVAL = "%";
    protected static final String OPERATOR_GREATER = ">";
    protected static final String OPERATOR_SMALLER = "<";

    public interface ValueParser<T> {
        @NotNull T parse(@NotNull String valueRaw);
    }

    public interface Operator<T> {
        boolean compare(@NotNull T arenaValue, @NotNull T triggerValue);
    }

    public ArenaGameTriggerValue(@NotNull String valueRaw, @NotNull ValueParser<T> parser) {
        this.operators = new LinkedHashMap<>();
        this.addOperators();

        this.valueRaw = valueRaw;
        this.values = Stream.of(valueRaw.split(",")).map(StringUtil::noSpace).map(valueStr -> {
            // Получаем пару: Строковое значение (уже без знака оператора) - Оператор
            Pair<String, Operator<T>> result = this.getOperator(valueStr);
            // Парсим строковое значение в "актуальное".
            T triggerValue = parser.parse(result.getFirst());
            Operator<T> operator = result.getSecond();

            return Pair.of(triggerValue, operator);
        }).toList();

        this.predicate = (arenaValue) -> {
            return this.getValues().stream().allMatch(result -> {
                T triggerValue = result.getFirst();
                Operator<T> operator = result.getSecond();
                return operator.compare(arenaValue, triggerValue);
            });
        };
    }

    public abstract void addOperators();

    public void addOperator(@NotNull String prefix, @NotNull Operator<T> operator) {
        this.operators.put(prefix.toLowerCase(), operator);
    }

    @NotNull
    public abstract Operator<T> getDefaultOperator();

    /*@NotNull
    public Operator<T> getOperator(@NotNull String value) {
        String prefix = this.operators.keySet().stream().filter(value::startsWith).findFirst().orElse("");
        return this.operators.getOrDefault(prefix, this.getDefaultOperator());
    }*/

    @NotNull
    public Pair<String, Operator<T>> getOperator(@NotNull String value) {
        String prefix = this.operators.keySet().stream().filter(value::startsWith).findFirst().orElse("");
        Operator<T> operator = this.operators.getOrDefault(prefix, this.getDefaultOperator());

        if (!prefix.isEmpty()) {
            value = value.substring(prefix.length());
        }
        return Pair.of(value, operator);
    }

    public boolean test(@NotNull T arenaValue) {
        return this.predicate.test(arenaValue);
    }

    @NotNull
    public String getRaw() {
        return valueRaw;
    }

    @NotNull
    public List<Pair<T, Operator<T>>> getValues() {
        return values;
    }
}
