package su.nightexpress.ama.hook.external;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.data.SunUser;
import su.nightexpress.sunlight.modules.scoreboard.ScoreboardManager;
import su.nightexpress.sunlight.user.settings.UserSetting;

public class SunLightHook {

    private static final SunLight SUNLIGHT = SunLightAPI.PLUGIN;

    public static void disableGod(@NotNull Player player) {
        SunUser user = SUNLIGHT.getUserManager().getUserData(player);
        user.getSettings().set(UserSetting.GOD_MODE, false);
    }

    public static void disableBoard(@NotNull Player player) {
        ScoreboardManager scoreboardManager = SUNLIGHT.getModuleManager().getScoreboardManager();
        if (scoreboardManager == null) return;

        scoreboardManager.removeBoard(player);
    }
}
