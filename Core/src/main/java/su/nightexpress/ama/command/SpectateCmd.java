package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.config.Lang;

import java.util.List;
import java.util.Map;

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
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length != 2) {
            this.printUsage(sender);
            return;
        }

        AbstractArena arena = plugin.getArenaManager().getArenaById(args[1]);
        if (arena == null) {
            plugin.getMessage(Lang.ARENA_ERROR_INVALID).replace("%id%", args[1]).send(sender);
            return;
        }

        Player player = (Player) sender;
        arena.joinSpectate(player);
    }
}
