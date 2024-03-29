package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.config.Lang;

import java.util.Arrays;
import java.util.List;

public class RegionCommand extends AbstractCommand<AMA> {

    public RegionCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"region"}, Perms.COMMAND_REGION);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.Command_Region_Usage).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.Command_Region_Desc).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("lock", "unlock");
        }
        if (arg == 2) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return super.getTab(player, arg, args);

            return arenaPlayer.getArena().getConfig().getRegionManager().getRegions()
                .stream().map(Region::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) {
            plugin.getMessage(Lang.Command_Region_State_Error_NotInGame).send(sender);
            return;
        }

        Arena arena = arenaPlayer.getArena();
        boolean lock = result.getArg(1).equalsIgnoreCase("lock");
        boolean unlock = result.getArg(1).equalsIgnoreCase("unlock");
        if (!lock && !unlock) {
            this.printUsage(sender);
            return;
        }

        String regId = result.getArg(2);
        Region region = arena.getConfig().getRegionManager().getRegion(regId);
        if (region == null) {
            plugin.getMessage(Lang.Command_Region_State_Error_InvalidRegion).send(sender);
            return;
        }

        if (lock) region.lock();
        else region.unlock();

        plugin.getMessage(Lang.Command_Region_State_Done).replace(region.replacePlaceholders()).send(sender);
    }
}
