package su.nightexpress.ama.command.hologram;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.config.Lang;

public class HologramAddCommand extends AbstractHologramCommand {

    public HologramAddCommand(@NotNull AMA plugin) {
        super(plugin, new String[]{"add"}, Perms.COMMAND_HOLOGRAM_ADD);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_HOLOGRAM_ADD_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_HOLOGRAM_ADD_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void perform(@NotNull CommandSender sender, @NotNull HologramType type, @NotNull HologramHolder holder) {
        Player player = (Player) sender;
        Location location = LocationUtil.getCenter(player.getLocation(), false);
        holder.getHologramLocations().add(location);
        holder.updateHolograms();

        plugin.getMessage(Lang.COMMAND_HOLOGRAM_ADD_DONE)
            .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(type))
            .send(player);
    }
}
