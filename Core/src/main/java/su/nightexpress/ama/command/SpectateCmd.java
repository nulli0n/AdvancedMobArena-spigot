package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.config.Lang;

import java.util.List;

public class SpectateCmd extends AbstractCommand<AMA> {

    public SpectateCmd(@NotNull AMA plugin) {
        super(plugin, new String[]{"spectate"}, Perms.COMMAND_SPECTATE);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.Command_Spectate_Desc).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.Command_Spectate_Usage).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return plugin.getArenaManager().getArenaIds();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() != 2) {
            this.printUsage(sender);
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaById(result.getArg(1));
        if (arena == null) {
            plugin.getMessage(Lang.ARENA_ERROR_INVALID).replace("%id%", result.getArg(1)).send(sender);
            return;
        }

        Player player = (Player) sender;
        arena.joinSpectate(player);
    }
}
