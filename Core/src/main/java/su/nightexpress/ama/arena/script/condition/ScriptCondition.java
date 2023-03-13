package su.nightexpress.ama.arena.script.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.utils.TriFunction;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class ScriptCondition<C, E> {

    private final String                               name;
    private final Function<String, C>                  parser;
    private final Function<ArenaGameGenericEvent, E>   extractor;
    private final TriFunction<E, C, Operator, Boolean> tester;

    public ScriptCondition(@NotNull String name,
                           @NotNull Function<String, C> parser,
                           @NotNull Function<ArenaGameGenericEvent, E> extractor,
                           @NotNull TriFunction<E, C, Operator, Boolean> tester
                         ) {
        this.name = name.toLowerCase();
        this.parser = parser;
        this.extractor = extractor;
        this.tester = tester;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Function<String, C> getParser() {
        return parser;
    }

    @NotNull
    public Function<ArenaGameGenericEvent, E> getExtractor() {
        return extractor;
    }

    @NotNull
    public TriFunction<E, C, Operator, Boolean> getTester() {
        return tester;
    }

    @SuppressWarnings("unchecked")
    public boolean test(@NotNull ArenaGameGenericEvent event, @NotNull Object value, @NotNull Operator operator) {
        E eventValue = this.getExtractor().apply(event);
        C conditionValue = (C) value;
        return this.getTester().apply(eventValue, conditionValue, operator);
        //return operator.compare(eventValue, conditionValue);
    }

    public enum Operator {
        EQUAL("="),
        NOT_EQUAL("!="),
        GREATER(">"),
        SMALLER("<"),
        EACH("%"),
        EACH_NOT("!%");

        private final String raw;

        Operator(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        public String getRaw() {
            return raw;
        }

        @NotNull
        public static Optional<Operator> fromString(@NotNull String str) {
            return Stream.of(Operator.values()).filter(operator -> operator.getRaw().equalsIgnoreCase(str))
                .findFirst();
        }
    }
}
