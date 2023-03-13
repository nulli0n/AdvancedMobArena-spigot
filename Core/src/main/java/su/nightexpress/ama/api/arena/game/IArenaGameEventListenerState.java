package su.nightexpress.ama.api.arena.game;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.lock.LockState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Deprecated
public interface IArenaGameEventListenerState extends IArenaGameEventListener {

    /**
     * Returns triggers for the OPPOSITE state of the current Lock State.
     * If it is LOCKED, then returns triggers for UNLOCKED state, and vice versa.
     *
     * @return Set of GameEvent triggers.
     */
    @NotNull
    @Override
    default Set<ArenaGameEventTrigger<?>> getTriggers() {
        return this.getStateTriggers(this.getState().getOpposite());
    }

    @NotNull
    LockState getState();

    void setState(@NotNull LockState state);

    default boolean isLocked() {
        return this.getState() == LockState.LOCKED;
    }

    default boolean isUnlocked() {
        return this.getState() == LockState.UNLOCKED;
    }

    @NotNull
    Map<LockState, Set<ArenaGameEventTrigger<?>>> getStateTriggers();

    @NotNull
    default Set<ArenaGameEventTrigger<?>> getStateTriggers(@NotNull LockState state) {
        return this.getStateTriggers().computeIfAbsent(state, k -> new HashSet<>());
    }
}
