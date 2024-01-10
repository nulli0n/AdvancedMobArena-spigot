package su.nightexpress.ama.arena.task;

import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaOpenTask extends AbstractTask<AMA> {

    private final ArenaManager arenaManager;

    public ArenaOpenTask(ArenaManager arenaManager) {
        super(arenaManager.plugin(), 60, true);
        this.arenaManager = arenaManager;
    }

    @Override
    public void action() {
        this.arenaManager.getArenas().forEach(Arena::tickOpenCloseTimes);
    }
}
