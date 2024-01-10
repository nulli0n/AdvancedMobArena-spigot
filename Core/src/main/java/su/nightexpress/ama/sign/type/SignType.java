package su.nightexpress.ama.sign.type;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;

import java.util.Arrays;
import java.util.List;

public enum SignType {
    ARENA_JOIN(Arrays.asList("&8[&4Mob Arena&8]", "&1&l%arena_name%", "&0- &4%arena_state% &0-", "&5%arena_players%&0/&5%arena_players_max%")),
    ARENA_LEAVE(Arrays.asList("&8[&4Mob Arena&8]", "-=*=-", "&1&lLeave Arena", "-=*=-")),
    ARENA_SHOP(Arrays.asList("&8[&4Mob Arena&8]", "-=*=-", "&1&lArena Shop", "-=*=-")),
    ARENA_READY(Arrays.asList("&8[&4Mob Arena&8]", "-=*=-", "&1&lClick to Ready", "-=*=-")),
    KIT_SELECTOR(Arrays.asList("&8[&4Mob Arena&8]", "-=*=-", "&1&lKit Selector", "-=*=-")),
    KIT_SHOP(Arrays.asList("&8[&4Mob Arena&8]", "-=*=-", "&1&lKit Shop", "-=*=-")),
    KIT(Arrays.asList("&8[&4Arena Kit&8]", "&1%kit_name%", "&8[&4Cost&8]", "&1%kit_cost%")),
    STATS(Arrays.asList("&4[ &c%score_stat_type%&4 ]", "&5Top %score_position%", "&1&l%score_name%", "&6&l&n%score_amount%")),
    STATS_OPEN(Arrays.asList("&8[&4Mob Arena&8]", "-=*=-", "&1&lStatistics", "-=*=-")),
    ;

    private final List<String> defaultText;

    SignType(@NotNull List<String> defaultText) {
        this.defaultText = Colorizer.apply(defaultText);
    }

    @NotNull
    public List<String> getDefaultText() {
        return defaultText;
    }
}
