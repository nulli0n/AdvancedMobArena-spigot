package su.nightexpress.ama.arena.lock;

import org.jetbrains.annotations.NotNull;

public enum LockState {

    UNLOCKED, LOCKED;

    @NotNull
    public LockState getOpposite() {
        return this == LOCKED ? UNLOCKED : LOCKED;
    }
}
