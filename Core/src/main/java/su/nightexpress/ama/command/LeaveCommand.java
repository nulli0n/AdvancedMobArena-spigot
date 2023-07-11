package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Lang;

public class LeaveCommand extends AbstractCommand<AMA> {

    public LeaveCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"leave"}, Perms.COMMAND_LEAVE);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_LEAVE_DESC).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) {
            plugin.getMessage(Lang.ARENA_GAME_ERROR_NOT_IN_GAME).send(player);
            return;
        }

        arenaPlayer.leaveArena();
    }
}
