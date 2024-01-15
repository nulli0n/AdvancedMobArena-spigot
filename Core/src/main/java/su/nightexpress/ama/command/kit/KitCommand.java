package su.nightexpress.ama.command.kit;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.config.Lang;

public class KitCommand extends GeneralCommand<AMA> {

    public KitCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"kit"}, Perms.COMMAND_KIT);
        this.setDescription(plugin.getMessage(Lang.COMMAND_KIT_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_KIT_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new AddCommand(plugin));
        this.addChildren(new RemoveCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
