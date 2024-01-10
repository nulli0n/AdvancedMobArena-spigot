package su.nightexpress.ama.command.coins;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.config.Lang;

public class CoinsCommand extends GeneralCommand<AMA> {

    public CoinsCommand(@NotNull AMA plugin, @NotNull Currency currency) {
        super(plugin, new String[]{"coins"}, Perms.COMMAND_COINS);
        this.setDescription(plugin.getMessage(Lang.COMMAND_COINS_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_COINS_USAGE));

        this.addDefaultCommand(new BalanceCommand(plugin, currency));
        this.addChildren(new HelpSubCommand<>(plugin));
        this.addChildren(new AddCommand(plugin, currency));
        this.addChildren(new RemoveCommand(plugin, currency));
        this.addChildren(new SetCommand(plugin, currency));
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
