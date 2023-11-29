package su.nightexpress.ama.command.score;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Lang;

class AddCommand extends ManageCommand {

    public AddCommand(@NotNull AMA plugin) {
        super(plugin, "add");
        this.setDescription(plugin.getMessage(Lang.COMMAND_SCORE_ADD_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SCORE_ADD_USAGE));
        this.setNotify(plugin.getMessage(Lang.COMMAND_SCORE_ADD_DONE));
    }

    @Override
    protected void manage(@NotNull ArenaPlayer arenaPlayer, int amount) {
        arenaPlayer.addScore(amount);
    }
}
