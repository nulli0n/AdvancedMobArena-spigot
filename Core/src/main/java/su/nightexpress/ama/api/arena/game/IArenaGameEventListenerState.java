package su.nightexpress.ama.api.arena.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaLockState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface IArenaGameEventListenerState extends IArenaGameEventListener {

    /**
     * Returns triggers for the OPPOSITE state of the current Lock State.
     * If it is LOCKED, then returns triggers for UNLOCKED state, and vice versa.
     * @return Set of GameEvent triggers.
     */
    @NotNull
    @Override
    default Set<ArenaGameEventTrigger<?>> getTriggers() {
        return this.getStateTriggers(this.getState().getOpposite());
    }

    @NotNull ArenaLockState getState();

    void setState(@NotNull ArenaLockState state);

    default boolean isLocked() {
        return this.getState() == ArenaLockState.LOCKED;
    }

    default boolean isUnlocked() {
        return this.getState() == ArenaLockState.UNLOCKED;
    }

    @NotNull Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> getStateTriggers();

    @NotNull
    default Set<ArenaGameEventTrigger<?>> getStateTriggers(@NotNull ArenaLockState state) {
        return this.getStateTriggers().computeIfAbsent(state, k -> new HashSet<>());
    }
}
