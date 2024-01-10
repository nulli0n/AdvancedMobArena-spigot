package su.nightexpress.ama.command.coins;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.config.Lang;

public class RemoveCommand extends ManageCommand {

    public RemoveCommand(@NotNull AMA plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"remove"}, Perms.COMMAND_COINS_TAKE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_COINS_REMOVE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_COINS_REMOVE_USAGE));
        this.setNotify(plugin.getMessage(Lang.COMMAND_COINS_REMOVE_DONE));
    }

    @Override
    protected void modify(@NotNull Player player, double amount) {
        this.currency.getHandler().take(player, amount);
    }
}
