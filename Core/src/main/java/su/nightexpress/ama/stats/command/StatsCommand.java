package su.nightexpress.ama.stats.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.stats.StatsManager;

import java.util.Map;

public class StatsCommand extends AbstractCommand<AMA> {

    private final StatsManager statsManager;

    public StatsCommand(@NotNull StatsManager statsManager) {
        super(statsManager.plugin(), new String[]{"stats"}, Perms.COMMAND_STATS);
        this.statsManager = statsManager;
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_STATS_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.statsManager.getStatsMenu().open(player, 1);
    }
}
