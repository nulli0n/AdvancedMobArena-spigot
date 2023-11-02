package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.config.Lang;

import java.util.List;

public class SpectateCommand extends AbstractCommand<AMA> {

    public SpectateCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"spectate"}, Perms.COMMAND_SPECTATE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_SPECTATE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SPECTATE_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getArenaManager().getArenas().stream()
                .filter(arena -> arena.getState() == GameState.INGAME && arena.getConfig().getGameplaySettings().isSpectateEnabled())
                .map(Arena::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaById(result.getArg(1));
        if (arena == null) {
            plugin.getMessage(Lang.ARENA_ERROR_INVALID).send(sender);
            return;
        }

        Player player = (Player) sender;
        arena.joinSpectate(player);
    }
}
