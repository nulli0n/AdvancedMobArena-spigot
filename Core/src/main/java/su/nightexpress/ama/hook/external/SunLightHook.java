package su.nightexpress.ama.hook.external;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.SunUser;
import su.nightexpress.sunlight.modules.scoreboard.ScoreboardManager;

public class SunLightHook {

    private static SunLight sunlight = (SunLight) Bukkit.getPluginManager().getPlugin(HookId.SUNLIGHT);

    public static void disableGod(@NotNull Player player) {
        SunUser user = sunlight.getUserManager().getUserData(player);
        user.setGodMode(false);
    }

    public static void disableBoard(@NotNull Player player) {
        ScoreboardManager scoreboardManager = sunlight.getModuleManager().getScoreboardManager();
        if (scoreboardManager == null) return;

        scoreboardManager.removeBoard(player);
    }
}
