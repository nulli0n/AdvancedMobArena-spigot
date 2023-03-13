package su.nightexpress.ama.api.arena.game;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;

import java.util.Set;

@Deprecated
public interface IArenaGameEventListener extends IPlaceholder {

    @NotNull
    Set<ArenaGameEventTrigger<?>> getTriggers();

    boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent);

    @Deprecated
    default void run(@NotNull ArenaGameGenericEvent gaveEvent) {
        // TODO
    }

    default boolean isReady(@NotNull ArenaGameGenericEvent gameEvent) {
        return this.getTriggers().stream().anyMatch(trigger -> trigger.isReady(gameEvent));
    }
}
