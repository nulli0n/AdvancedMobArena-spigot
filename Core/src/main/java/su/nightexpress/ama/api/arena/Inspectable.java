package su.nightexpress.ama.api.arena;

import org.jetbrains.annotations.NotNull;

public interface Inspectable {

    @NotNull
    Report getReport();

    default boolean hasProblems() {
        return this.getReport().hasProblems();
    }

    default boolean hasWarns() {
        return this.getReport().hasWarns();
    }
}
