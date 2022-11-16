package su.nightexpress.ama.api.hologram;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum HologramType {
    DEFAULT(Collections.emptyList()),
    ARENA(Arrays.asList("ICON: " + Material.SPAWNER.name(), "&e&lArena: &f" + Placeholders.ARENA_NAME, "&7State: &f" + Placeholders.ARENA_STATE, "&7Players: &f" + Placeholders.ARENA_PLAYERS + "&8/&f" + Placeholders.ARENA_PLAYERS_MAX, "&7Current Wave: &f" + Placeholders.ARENA_WAVE_NUMBER, "", "&aClick to Join/Spectate!")),
    ARENA_STATS(Arrays.asList("&8[ &aBest Arena Results &7- &2" + Placeholders.STATS_SCORE_TYPE + " &8]", "&c" + Placeholders.ARENA_NAME, "&8----=======----", "&a" + Placeholders.STATS_SCORE_POSITION + ". &2" + Placeholders.STATS_SCORE_NAME + " &7- &a" + Placeholders.STATS_SCORE_AMOUNT + " &7" + Placeholders.STATS_SCORE_TYPE, "&8----=======----")),
    REGION_UNLOCKED(Arrays.asList("&e&lRegion: &f" + Placeholders.REGION_NAME, "&7State: &a" + Placeholders.REGION_STATE, "", "&aLet's slay mobs there!")),
    REGION_LOCKED(Arrays.asList("&e&lRegion: &f" + Placeholders.REGION_NAME, "&7State: &c" + Placeholders.REGION_STATE, "", "&cPlease, come back later!")),
    KIT(Arrays.asList("ICON: " + Placeholders.KIT_ICON_MATERIAL, "&e&lKit: &f" + Placeholders.KIT_NAME, Placeholders.KIT_DESCRIPTION, "&7Cost: &c" + Placeholders.KIT_COST, "", "&aClick to &fBuy/Select", "&aShift-Click to &fPreview")),
    ;

    private final List<String> defaultFormat;

    HologramType(@NotNull List<String> defaultFormat) {
        this.defaultFormat = StringUtil.color(defaultFormat);
    }

    @NotNull
    public List<String> getDefaultFormat() {
        return defaultFormat;
    }

    public boolean isDummy() {
        return this == DEFAULT;
    }
}
