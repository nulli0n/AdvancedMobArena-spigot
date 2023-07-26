package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.spot.ArenaSpotState;
import su.nightexpress.ama.config.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpotCommand extends AbstractCommand<AMA> {

    public SpotCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"spot"}, Perms.COMMAND_SPOT);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.Command_Spot_Usage).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.Command_Spot_Desc).getLocalized();
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("state");
        }
        if (arg == 2) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return super.getTab(player, arg, args);

            return arenaPlayer.getArena().getConfig().getSpotManager().getSpotsMap()
                .values().stream().map(ArenaSpot::getId).collect(Collectors.toList());
        }
        if (arg == 3) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return super.getTab(player, arg, args);

            String spotId = args[2];
            ArenaSpot spot = arenaPlayer.getArena().getConfig().getSpotManager().getSpot(spotId);
            if (spot == null) return super.getTab(player, arg, args);

            return new ArrayList<>(spot.getStates().keySet());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        if (result.getArg(1).equalsIgnoreCase("state")) {
            if (result.length() != 4) {
                this.printUsage(sender);
                return;
            }

            Player player = (Player) sender;
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) {
                plugin.getMessage(Lang.Command_Spot_State_Error_NotInGame).send(sender);
                return;
            }

            String spotId = result.getArg(2);
            ArenaSpot spot = arenaPlayer.getArena().getConfig().getSpotManager().getSpot(spotId);
            if (spot == null) {
                plugin.getMessage(Lang.Command_Spot_State_Error_InvalidSpot).send(sender);
                return;
            }

            String stateId = result.getArg(3);
            ArenaSpotState state = spot.getState(stateId);
            if (state == null) {
                plugin.getMessage(Lang.Command_Spot_State_Error_InvalidState).send(sender);
                return;
            }

            spot.setState(arenaPlayer.getArena(), stateId);

            plugin.getMessage(Lang.Command_Spot_State_Done)
                .replace("%spot%", spot.getName())
                .replace("%state%", state.getId())
                .send(sender);
        }
    }
}
