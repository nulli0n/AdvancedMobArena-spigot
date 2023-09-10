package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.config.Lang;

import java.util.List;

public class JoinCommand extends AbstractCommand<AMA> {

    public JoinCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"join"}, Perms.COMMAND_JOIN);
        this.setDescription(plugin.getMessage(Lang.COMMAND_JOIN_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_JOIN_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getArenaManager().getArenas(player).stream().map(Arena::getId).toList();
        }
        if (arg == 2 && player.hasPermission(Perms.COMMAND_JOIN_OTHERS)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player;
        Arena arena;

        if (result.length() >= 2) {
            player = plugin.getServer().getPlayer(result.getArg(2, sender.getName()));
            if (player == null) {
                this.errorPlayer(sender);
                return;
            }
            if (!sender.hasPermission(Perms.COMMAND_JOIN_OTHERS) && !player.getName().equalsIgnoreCase(sender.getName())) {
                this.errorPermission(sender);
                return;
            }

            arena = plugin.getArenaManager().getArenaById(result.getArg(1));
            if (arena == null) {
                plugin.getMessage(Lang.ARENA_ERROR_INVALID).send(sender);
                return;
            }
        }
        else {
            if (!(sender instanceof Player)) {
                this.errorSender(sender);
                return;
            }

            player = (Player) sender;
            arena = plugin.getArenaManager().getArenas(player).stream().findFirst().orElse(null);
            if (arena == null) {
                plugin.getMessage(Lang.COMMAND_JOIN_NOTHING).send(player);
                return;
            }
        }

        arena.joinLobby(player);
    }
}
