package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.ArenaPlayer;
import su.nightexpress.ama.config.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreCmd extends AbstractCommand<AMA> {

	public ScoreCmd(@NotNull AMA plugin) {
		super(plugin, new String[] {"score"}, Perms.COMMAND_SCORE);
	}

	@Override
	@NotNull
	public String getUsage() {
		return plugin.getMessage(Lang.Command_Score_Usage).getLocalized();
	}

	@Override
	@NotNull
	public String getDescription() {
		return plugin.getMessage(Lang.Command_Score_Desc).getLocalized();
	}

	@Override
	public boolean isPlayerOnly() {
		return false;
	}

	@Override
	@NotNull
	public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
		if (i == 1) {
	       	return Arrays.asList("add", "take", "set");
	    }
		if (i == 2) {
			List<String> names = new ArrayList<>();
			plugin.getArenaManager().getArenas().forEach(arena -> {
				names.addAll(arena.getPlayers().stream()
						.map(ap -> ap.getPlayer().getName()).collect(Collectors.toList()));
			});
			
	       	return names;
	    }
		if (i == 3) {
	       	return Arrays.asList("<amount>","10","50","100");
	    }
		return super.getTab(player, i, args);
	}
	
	@Override
	public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		if (args.length != 4) {
			plugin.getMessage(Lang.Help_Score).send(sender);
			return;
		}
		
		if (args[1].equalsIgnoreCase("add")) {
			Player player = plugin.getServer().getPlayer(args[2]);
			if (player == null) {
				this.errorPlayer(sender);
				return;
			}
			
			ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
			if (arenaPlayer == null) {
				plugin.getMessage(Lang.Command_Score_Error_NotInGame).send(sender);
				return;
			}
			
			int amount = StringUtil.getInteger(args[3], -1);
			if (amount < 0) return;
			
			arenaPlayer.addScore(amount);
			
			plugin.getMessage(Lang.Command_Score_Add_Done)
				.replace("%player%", player.getName())
				.replace("%points%", amount)
				.send(sender);
		}
		else if (args[1].equalsIgnoreCase("take")) {
			Player player = plugin.getServer().getPlayer(args[2]);
			if (player == null) {
				this.errorPlayer(sender);
				return;
			}
			
			ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
			if (arenaPlayer == null) {
				plugin.getMessage(Lang.Command_Score_Error_NotInGame).send(sender);
				return;
			}
			
			int amount = StringUtil.getInteger(args[3], -1);
			if (amount < 0) return;
			
			arenaPlayer.addScore(-amount);
			
			plugin.getMessage(Lang.Command_Score_Take_Done)
				.replace("%player%", player.getName())
				.replace("%points%", amount)
				.send(sender);
		}
		else if (args[1].equalsIgnoreCase("set")) {
			Player player = plugin.getServer().getPlayer(args[2]);
			if (player == null) {
				this.errorPlayer(sender);
				return;
			}
			
			ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
			if (arenaPlayer == null) {
				plugin.getMessage(Lang.Command_Score_Error_NotInGame).send(sender);
				return;
			}
			
			int amount = StringUtil.getInteger(args[3], -1);
			if (amount < 0) return;
			
			arenaPlayer.setScore(amount);
			
			plugin.getMessage(Lang.Command_Score_Set_Done)
				.replace("%player%", player.getName())
				.replace("%score%", amount)
				.send(sender);
		}
	}
}
