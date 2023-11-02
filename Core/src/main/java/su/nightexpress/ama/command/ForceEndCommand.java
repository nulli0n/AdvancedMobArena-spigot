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

public class ForceEndCommand extends AbstractCommand<AMA> {

    public ForceEndCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"forceend"}, Perms.COMMAND_FORCEEND);
        this.setDescription(plugin.getMessage(Lang.COMMAND_FORCE_END_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_FORCE_END_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getArenaManager().getArenas().stream().filter(arena -> arena.getState() == GameState.INGAME)
                .map(Arena::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Arena arena = null;

        if (result.length() < 2) {
            if (sender instanceof Player player) {
                arena = plugin.getArenaManager().getArena(player);
            }
            if (arena == null) {
                this.printUsage(sender);
                return;
            }
        }
        else {
            arena = plugin.getArenaManager().getArenaById(result.getArg(1));
            if (arena == null) {
                plugin.getMessage(Lang.ARENA_ERROR_INVALID).send(sender);
                return;
            }
        }

        if (arena.getState() != GameState.INGAME) {
            plugin.getMessage(Lang.COMMAND_FORCE_END_ERROR_NOT_IN_GAME).replace(arena.replacePlaceholders()).send(sender);
            return;
        }

        plugin.getMessage(Lang.COMMAND_FORCE_END_DONE).replace(arena.replacePlaceholders()).send(sender);
        arena.stop();
    }
}
