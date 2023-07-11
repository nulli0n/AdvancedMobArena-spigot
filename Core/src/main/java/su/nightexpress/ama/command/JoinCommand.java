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

public class JoinCommand extends AbstractCommand<AMA> {

    public JoinCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"join"}, Perms.COMMAND_JOIN);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_JOIN_DESC).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_JOIN_USAGE).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getArenaManager().getArenas(player).stream().map(Arena::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        Arena arena;

        if (result.length() == 2) {
            arena = plugin.getArenaManager().getArenaById(result.getArg(1));
            if (arena == null) {
                plugin.getMessage(Lang.ARENA_ERROR_INVALID).send(sender);
                return;
            }
        }
        else {
            arena = plugin.getArenaManager().getArenas(player).stream().findFirst().orElse(null);
            if (arena == null) {
                plugin.getMessage(Lang.COMMAND_JOIN_NOTHING).send(player);
                return;
            }
        }

        arena.joinLobby(player);
    }
}
