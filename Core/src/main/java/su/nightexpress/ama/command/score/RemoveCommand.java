package su.nightexpress.ama.command.score;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Lang;

class RemoveCommand extends ManageCommand {

    public RemoveCommand(@NotNull AMA plugin) {
        super(plugin, "remove");
        this.setDescription(plugin.getMessage(Lang.COMMAND_SCORE_REMOVE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SCORE_REMOVE_USAGE));
        this.setNotify(plugin.getMessage(Lang.COMMAND_SCORE_REMOVE_DONE));
    }

    @Override
    protected void manage(@NotNull ArenaPlayer arenaPlayer, int amount) {
        arenaPlayer.removeScore(amount);
    }
}
