package su.nightexpress.ama.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.config.Lang;

import java.util.Arrays;
import java.util.List;

public class SetActiveCommand extends AbstractCommand<AMA> {

    public SetActiveCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"setactive"}, Perms.COMMAND_SET_ACTIVE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_SETACTIVE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_SETACTIVE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getArenaManager().getArenaIds();
        }
        if (arg == 2) {
            return Arrays.asList(Boolean.TRUE.toString(), Boolean.FALSE.toString());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Arena arena = plugin.getArenaManager().getArenaById(result.getArg(1));
        if (arena == null) {
            plugin.getMessage(Lang.ARENA_ERROR_INVALID).send(sender);
            return;
        }

        boolean state = Boolean.parseBoolean(result.getArg(2));
        arena.getConfig().setActive(state);
        arena.getConfig().save();

        plugin.getMessage(Lang.COMMAND_SETACTIVE_DONE)
            .replace(Placeholders.GENERIC_STATE, LangManager.getBoolean(arena.getConfig().isActive()))
            .replace(Placeholders.ARENA_ID, arena.getId())
            .send(sender);
    }
}
