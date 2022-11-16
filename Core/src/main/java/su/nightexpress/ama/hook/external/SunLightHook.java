package su.nightexpress.ama.hook.external;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.hook.AbstractHook;
import su.nightexpress.ama.AMA;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.SunUser;
import su.nightexpress.sunlight.modules.scoreboard.ScoreboardManager;

public class SunLightHook extends AbstractHook<AMA> {
	
	private static SunLight sunlight;
	
	public SunLightHook(@NotNull AMA plugin, @NotNull String pluginName) {
		super(plugin, pluginName);
	}
	
	@Override
	public boolean setup() {
		sunlight = (SunLight) plugin.getPluginManager().getPlugin(this.getPluginName());
		return sunlight != null;
	}

	@Override
	public void shutdown() {
		
	}

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
