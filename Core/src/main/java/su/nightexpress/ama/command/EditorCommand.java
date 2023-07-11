package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;

public class EditorCommand extends AbstractCommand<AMA> {

    public EditorCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"editor"}, Perms.CREATOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Open editor.";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.plugin.getEditor().open(player, 1);
    }
}
