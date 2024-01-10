package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.config.Lang;

import java.util.Arrays;
import java.util.List;

public class SkipRoundCommand extends AbstractCommand<AMA> {

    public SkipRoundCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"skipround"}, Perms.COMMAND_SKIPROUND);
        this.setDescription(plugin.getMessage(Lang.COMMAND_SKIP_ROUND_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SKIP_ROUND_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getArenaManager().getArenas().stream()
                .filter(arena -> arena.getState() == GameState.INGAME && !arena.isAboutToEnd())
                .map(Arena::getId).toList();
        }
        if (arg == 2) {
            return Arrays.asList("1", "5", "10");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Arena arena;
        if (result.length() >= 2) {
            arena = plugin.getArenaManager().getArenaById(result.getArg(1));
            if (arena == null) {
                this.plugin.getMessage(Lang.ARENA_ERROR_INVALID).send(sender);
                return;
            }
        }
        else if (sender instanceof Player player) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) {
                plugin.getMessage(Lang.ARENA_GAME_ERROR_NOT_IN_GAME).send(player);
                return;
            }
            arena = arenaPlayer.getArena();
        }
        else {
            this.printUsage(sender);
            return;
        }

        if (arena.getState() != GameState.INGAME || arena.isAboutToEnd()) {
            this.plugin.getMessage(Lang.COMMAND_SKIP_ROUND_ERROR_NOT_IN_GAME).replace(arena.replacePlaceholders()).send(sender);
            return;
        }

        int amount = result.getInt(2, 1);
        arena.setSkipRounds(arena.getSkipRounds() + amount);
        this.plugin.getMessage(Lang.COMMAND_SKIP_ROUND_DONE)
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .replace(arena.replacePlaceholders())
            .send(sender);
    }
}
