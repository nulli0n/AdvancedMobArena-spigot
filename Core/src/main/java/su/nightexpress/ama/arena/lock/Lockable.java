package su.nightexpress.ama.arena.lock;

import org.jetbrains.annotations.NotNull;

public interface Lockable {

    @NotNull LockState getLockState();

    void setLockState(@NotNull LockState state);

    default void lock() {
        this.setLockState(LockState.LOCKED);
    }

    default void unlock() {
        this.setLockState(LockState.UNLOCKED);
    }

    default boolean isLocked() {
        return this.getLockState() == LockState.LOCKED;
    }

    default boolean isUnlocked() {
        return this.getLockState() == LockState.UNLOCKED;
    }
}
