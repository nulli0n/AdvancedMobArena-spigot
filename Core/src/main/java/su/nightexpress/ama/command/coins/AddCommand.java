package su.nightexpress.ama.command.coins;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.config.Lang;

class AddCommand extends ManageCommand {

    public AddCommand(@NotNull AMA plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"give"}, Perms.COMMAND_COINS_GIVE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_COINS_ADD_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_COINS_ADD_USAGE));
        this.setNotify(plugin.getMessage(Lang.COMMAND_COINS_ADD_DONE));
    }

    @Override
    protected void modify(@NotNull Player player, double amount) {
        this.currency.getHandler().give(player, amount);
    }
}
