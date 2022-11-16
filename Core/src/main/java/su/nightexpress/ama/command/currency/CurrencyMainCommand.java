package su.nightexpress.ama.command.currency;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.config.Lang;

import java.util.Map;

public class CurrencyMainCommand extends GeneralCommand<AMA> {

    public CurrencyMainCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"currency"}, Perms.COMMAND_CURRENCY);

        this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
        this.addChildren(new CurrencyGiveTakeCommand(this.plugin, CurrencyGiveTakeCommand.Mode.GIVE));
        this.addChildren(new CurrencyGiveTakeCommand(this.plugin, CurrencyGiveTakeCommand.Mode.TAKE));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_CURRENCY_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_CURRENCY_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {

    }
}
