package su.nightexpress.ama.command.score;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.config.Lang;

public class ScoreCommand extends AbstractCommand<AMA> {

    public ScoreCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"score"}, Perms.COMMAND_SCORE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_SCORE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SCORE_USAGE));

        this.addChildren(new HelpSubCommand<>(plugin));
        this.addChildren(new AddCommand(plugin));
        this.addChildren(new RemoveCommand(plugin));
        this.addChildren(new SetCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender commandSender, @NotNull CommandResult commandResult) {

    }
}
