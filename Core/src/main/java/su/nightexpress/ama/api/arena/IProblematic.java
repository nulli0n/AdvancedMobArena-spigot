package su.nightexpress.ama.api.arena;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;

import java.util.List;

public interface IProblematic extends IPlaceholder {

    default boolean hasProblems() {
        return !this.getProblems().isEmpty();
    }

    @NotNull
    List<String> getProblems();
}
