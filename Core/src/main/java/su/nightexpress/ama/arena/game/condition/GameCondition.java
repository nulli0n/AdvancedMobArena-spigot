package su.nightexpress.ama.arena.game.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class GameCondition<T> {

    private final String                             name;
    private final Function<String, T>                parser;
    private final Function<ArenaGameGenericEvent, T> extractor;

    public GameCondition(@NotNull String name, @NotNull Function<String, T> parser, @NotNull Function<ArenaGameGenericEvent, T> extractor) {
        this.name = name.toLowerCase();
        this.parser = parser;
        this.extractor = extractor;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Function<String, T> getParser() {
        return parser;
    }

    @NotNull
    public Function<ArenaGameGenericEvent, T> getExtractor() {
        return extractor;
    }

    @SuppressWarnings("unchecked")
    public boolean test(@NotNull ArenaGameGenericEvent event, @NotNull Object value, @NotNull Operator operator) {
        T eventValue = this.extractor.apply(event);
        T conditionValue = (T) value;
        return operator.compare(eventValue, conditionValue);
    }

    /*public static class Value<T> {

        private final T value;

        public Value(@NotNull T value) {
            this.value = value;
        }

        @NotNull
        public T getValue() {
            return value;
        }
    }*/

    public enum Operator {
        EQUAL("="),
        NOT_EQUAL("!="),
        GREATER(">"),
        SMALLER("<"),
        EACH("%"),
        NOT_EACH("!%");

        private final String raw;

        Operator(@NotNull String raw) {
            this.raw = raw;
        }

        @NotNull
        public String getRaw() {
            return raw;
        }

        public boolean compare(@NotNull Object what, @NotNull Object with) {
            return switch (this) {
                case EQUAL -> what.equals(with);
                case NOT_EQUAL -> !what.equals(with);
                case GREATER -> {
                    if (what instanceof Number nWhat && with instanceof Number nWith) {
                        yield nWhat.doubleValue() > nWith.doubleValue();
                    }
                    yield false;
                }
                case SMALLER -> {
                    if (what instanceof Number nWhat && with instanceof Number nWith) {
                        yield nWhat.doubleValue() < nWith.doubleValue();
                    }
                    yield false;
                }
                case EACH -> {
                    if (what instanceof Number nWhat && with instanceof Number nWith) {
                        yield nWhat.intValue() % nWith.intValue() == 0;
                    }
                    yield false;
                }
                case NOT_EACH -> {
                    if (what instanceof Number nWhat && with instanceof Number nWith) {
                        yield nWhat.intValue() % nWith.intValue() != 0;
                    }
                    yield false;
                }
            };
        }

        @NotNull
        public static Optional<Operator> fromString(@NotNull String str) {
            return Stream.of(Operator.values()).filter(operator -> operator.getRaw().equalsIgnoreCase(str))
                .findFirst();
        }
    }
}
