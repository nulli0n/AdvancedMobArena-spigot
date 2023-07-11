package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.config.Lang;

public class SkipwaveCmd extends AbstractCommand<AMA> {

    public SkipwaveCmd(@NotNull AMA plugin) {
        super(plugin, new String[]{"skipwave"}, Perms.COMMAND_SKIPWAVE);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.Command_Skipwave_Desc).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.Command_Skipwave_Usage).getLocalized();
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

        int amount = result.length() >= 2 ? StringUtil.getInteger(result.getArg(1), 1) : 1;
        Arena arena = arenaPlayer.getArena();

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (this.count++ >= amount || arena.getState() != GameState.INGAME) {
                    this.cancel();
                    return;
                }
                arena.skipWave();
            }
        }.runTaskTimer(plugin, 0L, 45L);
    }
}
