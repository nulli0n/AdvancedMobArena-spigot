package su.nightexpress.ama.kit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.event.ArenaPlayerJoinEvent;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.kit.menu.KitSelectMenu;
import su.nightexpress.ama.kit.menu.KitShopMenu;

import java.util.*;

public class KitManager extends AbstractManager<AMA> {
	
	private JYML cfg;
	private JYML configPreview;

	private boolean isSavePurchasedKits;

	private Map<String, Kit> kits;
	
	private KitSelectMenu selectMenu;
	private KitShopMenu   shopMenu;
	
	public KitManager(@NotNull AMA plugin) {
		super(plugin);
	}
	
	@Override
	public void onLoad() {
		this.plugin.getConfigManager().extract("/kits/kits/");
		this.cfg = JYML.loadOrExtract(plugin, "/kits/settings.yml");
		this.configPreview = JYML.loadOrExtract(plugin, "/kits/gui.preview.yml");
		this.isSavePurchasedKits = cfg.getBoolean("General.Save_Purchased_Kits");
		
		this.kits = new HashMap<>();
		for (JYML cfg2 : JYML.loadAll(plugin.getDataFolder() + "/kits/kits/", false)) {
			try {
				Kit kit = new Kit(plugin, cfg2);
				this.kits.put(kit.getId(), kit);
				kit.updateHolograms();
			}
			catch (Exception e) {
				this.plugin.error("Could not load kit: " + cfg2.getFile().getName());
				e.printStackTrace();
			}
		}
		this.plugin.info("Kits Loaded: " + kits.size());
		
		this.selectMenu = new KitSelectMenu(plugin, JYML.loadOrExtract(plugin, "/kits/gui.selector.yml"));
		this.shopMenu = new KitShopMenu(this.plugin, JYML.loadOrExtract(plugin, "/kits/gui.shop.yml"));

		this.addListener(new Listener(this.plugin));
	}

	@Override
	public void onShutdown() {
		if (this.kits != null) {
			this.getKits().forEach(Kit::clear);
			this.kits.clear();
			this.kits = null;
		}
		if (this.selectMenu != null) {
			this.selectMenu.clear();
			this.selectMenu = null;
		}
		if (this.shopMenu != null) {
			this.shopMenu.clear();
			this.shopMenu = null;
		}
	}

	@NotNull
	public JYML getConfigPreview() {
		return configPreview;
	}

	@NotNull
	public KitSelectMenu getSelectMenu() {
		return selectMenu;
	}

	@NotNull
	public KitShopMenu getShopMenu() {
		return shopMenu;
	}
    
    public boolean isSavePurchasedKits() {
    	return this.isSavePurchasedKits;
    }
	
	public boolean isKitExists(@NotNull String id) {
		return this.getKitById(id) != null;
	}

	@NotNull
	public Map<String, Kit> getKitsMap() {
		return this.kits;
	}
	
	@NotNull
    public Collection<Kit> getKits() {
    	return this.getKitsMap().values();
    }
	
	@NotNull
	public List<String> getKitIds() {
		return new ArrayList<>(this.kits.keySet());
	}
    
	@Nullable
    public Kit getKitById(@NotNull String id) {
    	return this.kits.get(id.toLowerCase());
    }
    
    @Nullable
    public Kit getDefaultKit() {
    	return Rnd.get(this.getKits().stream().filter(Kit::isDefault).toList());
    }
	
    class Listener extends AbstractListener<AMA> {

		public Listener(@NotNull AMA plugin) {
			super(plugin);
		}

		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void onArenaJoin(ArenaPlayerJoinEvent e) {
			if (!isSavePurchasedKits()) return;

			Player player = e.getPlayer();

			ArenaUser user = plugin.getUserManager().getUserData(player);

			this.plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
				getKits().stream().filter(kit -> {
					if (user.hasKit(kit) || !kit.hasPermission(player)) return false;
					return kit.getCost() <= 0;
				}).forEach(user::addKit);

				user.saveData(this.plugin);
			});
		}
	}
}
