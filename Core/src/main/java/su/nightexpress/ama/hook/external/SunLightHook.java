package su.nightexpress.ama.hook.external;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.DefaultSettings;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;

public class SunLightHook {

    private static final SunLight SUNLIGHT = SunLightAPI.PLUGIN;

    public static void disableGod(@NotNull Player player) {
        SunUser user = SUNLIGHT.getUserManager().getUserData(player);
        user.getSettings().set(DefaultSettings.GOD_MODE, false);
    }

    public static void disableBoard(@NotNull Player player) {
        ScoreboardModule scoreboardManager = SUNLIGHT.getModuleManager().getModule(ScoreboardModule.class).orElse(null);
        if (scoreboardManager == null) return;

        scoreboardManager.removeBoard(player);
    }
}
