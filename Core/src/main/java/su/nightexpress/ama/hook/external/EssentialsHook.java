package su.nightexpress.ama.hook.external;

import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.hook.AbstractHook;
import su.nightexpress.ama.AMA;

public class EssentialsHook extends AbstractHook<AMA> {
	
	private static Essentials essentials;
	
	public EssentialsHook(@NotNull AMA plugin, @NotNull String pluginName) {
		super(plugin, pluginName);
	}
	
	@Override
	public boolean setup() {
		essentials = (Essentials) this.plugin.getPluginManager().getPlugin(this.getPluginName());
		return essentials != null;
	}

	@Override
	public void shutdown() {
		
	}

	public static void disableGod(@NotNull Player player) {
		essentials.getUser(player).setGodModeEnabled(false);
	}
}
