package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.config.Lang;

public class EditorCommand extends AbstractCommand<AMA> {

    public EditorCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"editor"}, Perms.CREATOR);
        this.setDescription(plugin.getMessage(Lang.COMMAND_EDITOR_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.plugin.getEditor().open(player, 1);
    }
}
