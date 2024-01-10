package su.nightexpress.ama.command.score;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Lang;

import java.util.Arrays;
import java.util.List;

abstract class ManageCommand extends AbstractCommand<AMA> {

    protected LangMessage notify;

    public ManageCommand(@NotNull AMA plugin, @NotNull String name) {
        super(plugin, new String[]{name}, Perms.COMMAND_SCORE);
    }

    protected void setNotify(@NotNull LangMessage notify) {
        this.notify = notify;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2) {
            return ArenaPlayer.getPlayers().stream().filter(ArenaPlayer::isReal).map(arenaPlayer -> arenaPlayer.getPlayer().getName()).toList();
        }
        if (arg == 3) {
            return Arrays.asList("1", "5", "50", "100");
        }
        return super.getTab(player, arg, args);
    }

    protected abstract void manage(@NotNull ArenaPlayer arenaPlayer, int amount);

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 4) {
            this.printUsage(sender);
            return;
        }

        Player player = PlayerUtil.getPlayer(result.getArg(2));
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) {
            plugin.getMessage(Lang.ARENA_ERROR_PLAYER_NOT_IN_GAME).send(sender);
            return;
        }

        int amount = result.getInt(3, -1);
        if (amount < 0) return;

        this.manage(arenaPlayer, amount);

        if (this.notify != null) {
            this.notify
                .replace(Placeholders.forPlayer(player))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .send(sender);
        }
    }
}
