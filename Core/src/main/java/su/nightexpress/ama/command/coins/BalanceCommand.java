package su.nightexpress.ama.command.coins;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.config.Lang;

import java.util.List;

public class BalanceCommand extends AbstractCommand<AMA> {

    private final Currency currency;

    public BalanceCommand(@NotNull AMA plugin, @NotNull Currency currency) {
        super(plugin, new String[]{"balance"}, Perms.COMMAND_BALANCE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_COINS_BALANCE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_COINS_BALANCE_USAGE));

        this.currency = currency;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2 && player.hasPermission(Perms.COMMAND_BALANCE_OTHERS)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        String targetName = result.getArg(2, sender.getName());
        if (!targetName.equalsIgnoreCase(sender.getName()) && !sender.hasPermission(Perms.COMMAND_BALANCE_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAsync(targetName).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            boolean isSelf = sender.getName().equalsIgnoreCase(user.getName());
            plugin.getMessage(isSelf ? Lang.COMMAND_COINS_BALANCE_DONE_SELF : Lang.COMMAND_COINS_BALANCE_DONE_OTHERS)
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .replace(Placeholders.GENERIC_AMOUNT, this.currency.format(user.getCoins()))
                .send(sender);
        });
    }
}
