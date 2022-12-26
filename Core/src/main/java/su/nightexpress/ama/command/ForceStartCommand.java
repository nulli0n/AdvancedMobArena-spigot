package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.config.Lang;

import java.util.List;
import java.util.Map;

public class ForceStartCommand extends AbstractCommand<AMA> {

    public ForceStartCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"forcestart"}, Perms.COMMAND_FORCESTART);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_FORCE_START_DESC).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_FORCE_START_USAGE).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getArenaManager().getArenas().stream().filter(arena -> arena.getState() == ArenaState.READY)
                .map(AbstractArena::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length != 2) {
            this.printUsage(sender);
            return;
        }

        AbstractArena arena = plugin.getArenaManager().getArenaById(args[1]);
        if (arena == null) {
            plugin.getMessage(Lang.ARENA_ERROR_INVALID).send(sender);
            return;
        }

        if (arena.getState() != ArenaState.READY) {
            plugin.getMessage(Lang.COMMAND_FORCE_START_ERROR_NOT_READY).replace(arena.replacePlaceholders()).send(sender);
            return;
        }

        plugin.getMessage(Lang.COMMAND_FORCE_START_DONE).replace(arena.replacePlaceholders()).send(sender);
        arena.setLobbyTimeleft(0);
    }
}
