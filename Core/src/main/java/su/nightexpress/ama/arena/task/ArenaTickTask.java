package su.nightexpress.ama.arena.task;

import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaTickTask extends AbstractTask<AMA> {

    private final ArenaManager arenaManager;

    public ArenaTickTask(ArenaManager arenaManager) {
        super(arenaManager.plugin(), 1, false);
        this.arenaManager = arenaManager;
    }

    @Override
    public void action() {
        this.arenaManager.getArenas().stream().filter(arena -> arena.getConfig().isActive() && !arena.getConfig().hasProblems())
            .forEach(Arena::tick);
    }
}
