package su.nightexpress.ama.command.score;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Lang;

class SetCommand extends ManageCommand {

    public SetCommand(@NotNull AMA plugin) {
        super(plugin, "set");
        this.setDescription(plugin.getMessage(Lang.COMMAND_SCORE_SET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SCORE_SET_USAGE));
        this.setNotify(plugin.getMessage(Lang.COMMAND_SCORE_SET_DONE));
    }

    @Override
    protected void manage(@NotNull ArenaPlayer arenaPlayer, int amount) {
        arenaPlayer.setScore(amount);
    }
}
