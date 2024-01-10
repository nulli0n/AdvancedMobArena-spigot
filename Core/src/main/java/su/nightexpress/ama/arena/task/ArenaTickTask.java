package su.nightexpress.ama.arena.task;

import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.ArenaManager;

public class ArenaTickTask extends AbstractTask<AMA> {

    private final ArenaManager arenaManager;

    public ArenaTickTask(ArenaManager arenaManager) {
        super(arenaManager.plugin(), 1, false);
        this.arenaManager = arenaManager;
    }

    @Override
    public void action() {
        this.arenaManager.getArenas().forEach(arena -> {
            if (!arena.getConfig().isActive() || arena.getConfig().hasProblems()) return;

            arena.tick();
        });
    }
}
