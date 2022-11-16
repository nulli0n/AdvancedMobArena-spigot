package su.nightexpress.ama.api;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.currency.CurrencyManager;
import su.nightexpress.ama.data.ArenaUserManager;
import su.nightexpress.ama.kit.KitManager;
import su.nightexpress.ama.nms.ArenaNMS;
import su.nightexpress.ama.stats.StatsManager;

public class ArenaAPI {

	public static final AMA PLUGIN = AMA.getPlugin(AMA.class);
	
	@NotNull
	public static ArenaManager getArenaManager() {
		return PLUGIN.getArenaManager();
	}

	@NotNull
	public static ArenaUserManager getUserManager() {
		return PLUGIN.getUserManager();
	}
	
	@NotNull
	public static KitManager getKitManager() {
		return PLUGIN.getKitManager();
	}
	
	@NotNull
	public static StatsManager getStatsManager() {
		return PLUGIN.getStatsManager();
	}
	
	@NotNull
	public static CurrencyManager getCurrencyManager() {
		return PLUGIN.getCurrencyManager();
	}

	@NotNull
	public static ArenaNMS getArenaNMS() {
		return PLUGIN.getArenaNMS();
	}
}
