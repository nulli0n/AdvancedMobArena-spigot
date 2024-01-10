package su.nightexpress.ama.command.coins;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.currency.Currency;

import java.util.Arrays;
import java.util.List;

abstract class ManageCommand extends AbstractCommand<AMA> {

    protected final Currency currency;

    private LangMessage notify;

    public ManageCommand(@NotNull AMA plugin, @NotNull Currency currency, @NotNull String[] aliases, @NotNull Permission permission) {
        super(plugin, aliases, permission);
        this.currency = currency;
    }

    protected void setNotify(@NotNull LangMessage notify) {
        this.notify = notify;
    }

    protected abstract void modify(@NotNull Player player, double amount);

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 3) {
            return Arrays.asList("1", "10", "100");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 4) {
            this.printUsage(sender);
            return;
        }

        Player player = PlayerUtil.getPlayer(result.getArg(2));
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        double amount = result.getDouble(3, 0);
        if (amount <= 0D) {
            this.errorNumber(sender, result.getArg(3));
            return;
        }

        this.modify(player, amount);

        this.notify
            .replace(this.currency.replacePlaceholders())
            .replace(Placeholders.forPlayer(player))
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
            .send(sender);
    }
}
