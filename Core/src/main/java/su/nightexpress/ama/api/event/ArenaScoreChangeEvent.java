package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaScoreChangeEvent extends ArenaEvent {

    private final int oldScore;
    private final int newScore;

    public ArenaScoreChangeEvent(@NotNull Arena arena, int oldScore, int newScore) {
        super(arena);
        this.oldScore = oldScore;
        this.newScore = newScore;
    }

    public int getOldScore() {
        return this.oldScore;
    }

    public int getNewScore() {
        return this.newScore;
    }

    public int getAmount() {
        return this.getNewScore() - this.getOldScore();
    }
}
