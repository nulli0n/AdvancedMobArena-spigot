package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.arena.ArenaPlayer;
import su.nightexpress.ama.config.Lang;

public class ShopCommand extends AbstractCommand<AMA> {

	public ShopCommand(@NotNull AMA plugin) {
		super(plugin, new String[] {"shop"}, Perms.COMMAND_SHOP);
	}

	@Override
	@NotNull
	public String getUsage() {
		return "";
	}

	@Override
	@NotNull
	public String getDescription() {
		return plugin.getMessage(Lang.COMMAND_SHOP_DESC).getLocalized();
	}

	@Override
	public boolean isPlayerOnly() {
		return true;
	}

	@Override
	public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
		Player player = (Player) sender;
		
		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
		if (arenaPlayer == null) {
			plugin.getMessage(Lang.ARENA_GAME_ERROR_NOT_IN_GAME).send(player);
			return;
		}
		
		arenaPlayer.getArena().getConfig().getShopManager().open(player);
	}
}
