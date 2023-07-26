package su.nightexpress.ama.command.coins;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.config.Lang;

class SetCommand extends ManageCommand {

    public SetCommand(@NotNull AMA plugin, @NotNull Currency currency) {
        super(plugin, currency, new String[]{"set"}, Perms.COMMAND_COINS_SET);
        this.setDescription(plugin.getMessage(Lang.COMMAND_COINS_SET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_COINS_SET_USAGE));
        this.setDoneMessage(plugin.getMessage(Lang.COMMAND_COINS_SET_DONE));
    }

    @Override
    protected void modify(@NotNull Player player, double amount) {
        this.currency.getHandler().set(player, amount);
    }
}
