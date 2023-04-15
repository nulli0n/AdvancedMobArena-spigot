package su.nightexpress.ama.api.arena;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;

import java.util.List;

public interface Problematic {

    String PREFIX = Colorizer.apply("#ff9a9a[!] #ddecee");

    default boolean hasProblems() {
        return !this.getProblems().isEmpty();
    }

    @NotNull
    default String problem(@NotNull String text) {
        return PREFIX + text;
    }

    @NotNull List<String> getProblems();
}
