package su.nightexpress.ama.command.hologram;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.ArenaStatsHologram;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.stats.object.StatType;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractHologramCommand extends AbstractCommand<AMA> {

    public AbstractHologramCommand(@NotNull AMA plugin, @NotNull String[] aliases, @NotNull Permission permission) {
        super(plugin, aliases, permission);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2) {
            return Stream.of(HologramType.values()).filter(Predicate.not(HologramType::isDummy)).map(Enum::name).toList();
        }
        if (arg > 2) {
            HologramType hologramType = CollectionsUtil.getEnum(args[2], HologramType.class);
            if (hologramType == null || hologramType.isDummy()) return super.getTab(player, arg, args);

            if (arg == 3) {
                switch (hologramType) {
                    case ARENA, ARENA_STATS, REGION_UNLOCKED, REGION_LOCKED -> {
                        return plugin.getArenaManager().getArenaIds();
                    }
                    case KIT -> {
                        return plugin.getKitManager().getKitIds();
                    }
                }
            }
            if (arg == 4) {
                switch (hologramType) {
                    case REGION_UNLOCKED, REGION_LOCKED -> {
                        Arena arena = plugin.getArenaManager().getArenaById(args[3]);
                        if (arena == null) return super.getTab(player, arg, args);

                        return arena.getConfig().getRegionManager().getRegions().stream().map(ArenaRegion::getId).toList();
                    }
                    case ARENA_STATS -> {
                        return CollectionsUtil.getEnumsList(StatType.class);
                    }
                }
            }
        }
        return super.getTab(player, arg, args);
    }

    protected abstract void perform(@NotNull CommandSender sender, @NotNull HologramType type, @NotNull HologramHolder holder);

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length < 4) {
            this.printUsage(sender);
            return;
        }

        HologramType type = CollectionsUtil.getEnum(args[2], HologramType.class);
        if (type == null) {
            this.errorType(sender, HologramType.class);
            return;
        }

        HologramHolder holder = switch (type) {
            case ARENA -> {
                Arena arena = plugin.getArenaManager().getArenaById(args[3]);
                if (arena == null) yield null;

                yield arena.getConfig();
            }
            case DEFAULT -> null;
            case REGION_LOCKED, REGION_UNLOCKED -> {
                if (args.length < 5) yield null;

                Arena arena = plugin.getArenaManager().getArenaById(args[3]);
                if (arena == null) yield null;

                yield arena.getConfig().getRegionManager().getRegion(args[4]);
            }
            case KIT -> plugin.getKitManager().getKitById(args[3]);
            case ARENA_STATS -> {
                if (args.length < 5) yield null;

                Arena arena = plugin.getArenaManager().getArenaById(args[3]);
                if (arena == null) yield null;

                StatType statType = CollectionsUtil.getEnum(args[4], StatType.class);
                if (statType == null) yield null;

                yield arena.getConfig().getStatsHologram(statType);
            }
        };

        if (holder == null) {
            plugin.getMessage(Lang.COMMAND_HOLOGRAM_ERROR).send(sender);
            return;
        }

        this.perform(sender, type, holder);

        if (holder instanceof ConfigHolder configHolder) {
            configHolder.save();
        }
        else if (holder instanceof ArenaStatsHologram statsHologram) {
            statsHologram.getArenaConfig().save();
        }
    }
}
