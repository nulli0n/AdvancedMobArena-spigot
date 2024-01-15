package su.nightexpress.ama.command.kit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.List;

class AddCommand extends AbstractCommand<AMA> {

    public AddCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"add"}, Perms.COMMAND_KIT);
        this.setDescription(plugin.getMessage(Lang.COMMAND_KIT_ADD_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_KIT_ADD_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 3) {
            return plugin.getKitManager().getKitIds();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 4) {
            this.printUsage(sender);
            return;
        }

        Kit kit = plugin.getKitManager().getKitById(result.getArg(3));
        if (kit == null) {
            this.plugin.getMessage(Lang.KIT_ERROR_INVALID).send(sender);
            return;
        }

        plugin.getUserManager().getUserDataAndPerformAsync(result.getArg(2), user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            if (user.addKit(kit)) {
                plugin.getUserManager().saveUser(user);
            }

            this.plugin.getMessage(Lang.COMMAND_KIT_ADD_DONE)
                .replace(kit.replacePlaceholders())
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .send(sender);
        });
    }
}
