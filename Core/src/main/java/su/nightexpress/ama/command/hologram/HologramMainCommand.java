package su.nightexpress.ama.command.hologram;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.config.Lang;

public class HologramMainCommand extends GeneralCommand<AMA> {

    public HologramMainCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"hologram"}, Perms.COMMAND_HOLOGRAM);
        this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
        this.addChildren(new HologramAddCommand(this.plugin));
        this.addChildren(new HologramRemoveCommand(this.plugin));
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_HOLOGRAM_DESC).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_HOLOGRAM_USAGE).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }
}
