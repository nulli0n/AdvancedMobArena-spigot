package su.nightexpress.ama.arena.game.condition;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaLockState;

import java.util.*;

public interface BiGameEventListener extends GameEventListener {

    /**
     * Returns triggers for the OPPOSITE state of the current Lock State.
     * If it is LOCKED, then returns triggers for UNLOCKED state, and vice versa.
     *
     * @return Set of GameEvent triggers.
     */
    @NotNull
    @Override
    default Set<ArenaGameEventType> getTriggers() {
        return this.getStateTriggers(this.getState().getOpposite());
    }

    @NotNull
    @Override
    default List<GameConditionList> getConditions() {
        return this.getStateConditions(this.getState().getOpposite());
    }

    @NotNull
    ArenaLockState getState();

    void setState(@NotNull ArenaLockState state);

    default boolean isLocked() {
        return this.getState() == ArenaLockState.LOCKED;
    }

    default boolean isUnlocked() {
        return this.getState() == ArenaLockState.UNLOCKED;
    }

    @NotNull Map<ArenaLockState, Set<ArenaGameEventType>> getStateTriggers();

    @NotNull Map<ArenaLockState, List<GameConditionList>> getStateConditions();

    @NotNull
    default Set<ArenaGameEventType> getStateTriggers(@NotNull ArenaLockState state) {
        return this.getStateTriggers().computeIfAbsent(state, k -> new HashSet<>());
    }

    @NotNull
    default List<GameConditionList> getStateConditions(@NotNull ArenaLockState state) {
        return this.getStateConditions().computeIfAbsent(state, k -> new ArrayList<>());
    }
}
