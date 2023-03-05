package su.nightexpress.ama.arena.game.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;

import java.util.List;
import java.util.Set;

public interface GameEventListener {

    @NotNull Set<ArenaGameEventType> getTriggers();

    @NotNull List<GameConditionList> getConditions();

    default boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.getTriggers().contains(gameEvent.getEventType())) return false;
        if (this.getConditions().stream().filter(list -> list.isApplicableFor(gameEvent)).noneMatch(list -> list.test(gameEvent))) return false;

        this.run(gameEvent);
        return true;
    }

    void run(@NotNull ArenaGameGenericEvent gameEvent);
}
