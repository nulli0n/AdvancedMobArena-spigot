package su.nightexpress.ama.command.hologram;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.config.Lang;

public class HologramRemoveCommand extends AbstractHologramCommand {

    public HologramRemoveCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"remove"}, Perms.COMMAND_HOLOGRAM_REMOVE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_HOLOGRAM_REMOVE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_HOLOGRAM_REMOVE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void perform(@NotNull CommandSender sender, @NotNull HologramType type, @NotNull HologramHolder holder) {
        holder.removeHolograms();
        holder.getHologramLocations().clear();

        plugin.getMessage(Lang.COMMAND_HOLOGRAM_REMOVE_DONE)
            .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(type))
            .send(sender);
    }
}
