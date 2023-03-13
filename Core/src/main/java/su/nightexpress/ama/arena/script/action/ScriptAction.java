package su.nightexpress.ama.arena.script.action;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class ScriptAction {

    private final String                                             name;
    private final Set<Parameter<?>>                                  parameters;
    private final BiConsumer<ArenaGameGenericEvent, ParameterResult> executor;

    public ScriptAction(@NotNull String name,
                        @NotNull BiConsumer<ArenaGameGenericEvent, ParameterResult> executor,
                        Parameter<?>...                       parameters) {
        this.name = name.toLowerCase();
        this.parameters = new HashSet<>(Arrays.asList(parameters));
        this.executor = executor;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Set<Parameter<?>> getParameters() {
        return parameters;
    }

    @NotNull
    public BiConsumer<ArenaGameGenericEvent, ParameterResult> getExecutor() {
        return executor;
    }

    public void run(@NotNull ArenaGameGenericEvent event, @NotNull ParameterResult parameterResult) {
        this.getExecutor().accept(event, parameterResult);
    }
}
