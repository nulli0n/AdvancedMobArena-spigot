package su.nightexpress.ama.arena.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListenerState;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaShopEvent;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.ArenaPlayer;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.shop.EditorShopManager;
import su.nightexpress.ama.arena.shop.menu.ArenaShopMainMenu;
import su.nightexpress.ama.config.Lang;

import java.util.*;
import java.util.function.UnaryOperator;

public class ArenaShopManager implements IArenaGameEventListenerState, ConfigHolder, IArenaObject, ILoadable, IEditable, IProblematic {

	private static final String CONFIG_NAME = "shop.yml";

	private final ArenaConfig arenaConfig;
	private final JYML        config;

	private boolean                                            isActive;
	private boolean                                            isHideOtherKitProducts;
	private ArenaLockState                                     state;
	private Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> stateTriggers;
	private Map<String, ArenaShopCategory>                     categories;
	private ArenaShopMainMenu                                  menu;
	private EditorShopManager                                  editor;

	public ArenaShopManager(@NotNull ArenaConfig arenaConfig) {
		this.arenaConfig = arenaConfig;
		this.config = new JYML(arenaConfig.getFile().getParentFile().getAbsolutePath(), CONFIG_NAME);
	}

	@Override
	public void setup() {
		this.setActive(config.getBoolean("Settings.Active"));
		this.setHideOtherKitProducts(config.getBoolean("Settings.Hide_Other_Kit_Products"));
		this.setState(ArenaLockState.UNLOCKED);

		this.stateTriggers = new HashMap<>();
		for (ArenaLockState lockState : ArenaLockState.values()) {
			this.stateTriggers.put(lockState, ArenaGameEventTrigger.parse(config, "Settings.State." + lockState.name() + ".Triggers"));
		}

		this.categories = new LinkedHashMap<>();
		for (String catId : config.getSection("Categories")) {
			String path = "Categories." + catId + ".";

			String catName = StringUtil.color(config.getString(path + "Name", catId));
			List<String> catDesc = config.getStringList(path + "Description");
			ItemStack catIcon = config.getItem(path + "Icon");
			Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> catStateTriggers = new HashMap<>();
			for (ArenaLockState lockState : ArenaLockState.values()) {
				catStateTriggers.put(lockState, ArenaGameEventTrigger.parse(config, path + "State." + lockState.name() + ".Triggers"));
			}
			Set<String> catAllowedKits = config.getStringSet(path + "Allowed_Kits");
			Map<String, ArenaShopProduct> catProducts = new LinkedHashMap<>();

			ArenaShopCategory category = new ArenaShopCategory(arenaConfig, catId, catName, catDesc, catIcon, catStateTriggers, catAllowedKits, catProducts);

			for (String prId : config.getSection(path + "Products")) {
				String path2 = path + "Products." + prId + ".";

				String prName = config.getString(path2 + "Name", prId);
				List<String> prDesc = config.getStringList(path2 + "Description");

				ICurrency prCurrency = plugin().getCurrencyManager().getCurrency(config.getString(path2 + "Currency", ""));
				if (prCurrency == null) prCurrency = plugin().getCurrencyManager().getCurrencyFirst();

				double prPrice = config.getDouble(path2 + "Price");
				Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> prStateTriggers = new HashMap<>();
				for (ArenaLockState lockState : ArenaLockState.values()) {
					prStateTriggers.put(lockState, ArenaGameEventTrigger.parse(config, path2 + "State." + lockState.name() + ".Triggers"));
				}

				Set<String> prAllowedKits = config.getStringSet(path2 + "Allowed_Kits");
				ItemStack prPreview = config.getItem(path2 + "Preview");
				if (prPreview.getType().isAir()) {
					plugin().error("Null preview for '" + prId + "' item in '" + arenaConfig.getId() + "' arena shop!");
					//continue;
				}

				List<String> prCommands = config.getStringList(path2 + "Commands");
				List<ItemStack> prItems = Arrays.asList(config.getItemsEncoded(path2 + "Items"));

				ArenaShopProduct product = new ArenaShopProduct(category, prId, prName, prDesc, prCurrency, prPrice, prStateTriggers, prAllowedKits, prPreview, prCommands, prItems);
				category.getProductsMap().put(product.getId(), product);
			}

			this.categories.put(category.getId(), category);
		}
	}
	
	@Override
	public void shutdown() {
		this.getCategories().forEach(ArenaShopCategory::clear);
		this.categories.clear();
		if (this.menu != null) {
			this.menu.clear();
			this.menu = null;
		}
		if (this.editor != null) {
			this.editor.clear();
			this.editor = null;
		}
	}
	
	@Override
	public void onSave() {
		config.set("Settings.Active", this.isActive());
		config.set("Settings.Hide_Other_Kit_Products", this.isHideOtherKitProducts());
		config.set("Settings.State", null);
		this.getStateTriggers().forEach((lockState, triggers) -> {
			String path = "Settings.State." + lockState.name() + ".Triggers.";
			triggers.forEach(trigger -> {
				config.set(path + trigger.getType().name(), trigger.getValuesRaw());
			});
		});

		config.set("Categories", null);
		this.getCategories().forEach(category -> {
			String path = "Categories." + category.getId() + ".";

			config.set(path + "Name", category.getName());
			config.set(path + "Description", category.getDescription());
			config.setItem(path + "Icon", category.getIcon());
			category.getStateTriggers().forEach((lockState, triggers) -> {
				String path2 = path + "State." + lockState.name() + ".Triggers.";
				triggers.forEach(trigger -> {
					config.set(path2 + trigger.getType().name(), trigger.getValuesRaw());
				});
			});
			config.set(path + "Allowed_Kits", category.getAllowedKits());
			category.getProducts().forEach(shopProduct -> {
				String path2 = path + "Products." + shopProduct.getId() + ".";

				config.set(path2 + "Name", shopProduct.getName());
				config.set(path2 + "Description", shopProduct.getDescription());
				shopProduct.getStateTriggers().forEach((lockState, triggers) -> {
					String path3 = path2 + "State." + lockState.name() + ".Triggers.";
					triggers.forEach(trigger -> {
						config.set(path3 + trigger.getType().name(), trigger.getValuesRaw());
					});
				});

				config.set(path2 + "Price", shopProduct.getPrice());
				config.set(path2 + "Allowed_Kits", shopProduct.getApplicableKits());
				config.setItem(path2 + "Preview", shopProduct.getIcon());
				config.set(path2 + "Commands", shopProduct.getCommands());
				config.setItemsEncoded(path2 + "Items", shopProduct.getItems());
			});
		});
	}

	@Override
	@NotNull
	public UnaryOperator<String> replacePlaceholders() {
		return str -> str
			.replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()))
			.replace(Placeholders.SHOP_MANAGER_IS_ACTIVE, LangManager.getBoolean(this.isActive()))
			.replace(Placeholders.SHOP_MANAGER_HIDE_OTHER_KIT_ITEMS, LangManager.getBoolean(this.isHideOtherKitProducts()))
			.replace(Placeholders.SHOP_TRIGGERS_LOCKED, Placeholders.format(this.getStateTriggers(ArenaLockState.LOCKED)))
			.replace(Placeholders.SHOP_TRIGGERS_UNLOCKED, Placeholders.format(this.getStateTriggers(ArenaLockState.UNLOCKED)))
			;
	}

	@Override
	public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
		if (!this.isReady(gameEvent)) return false;

		ArenaLockState state = this.getState().getOpposite();
		this.setState(state);

		ArenaGameEventType eventType = state == ArenaLockState.LOCKED ? ArenaGameEventType.SHOP_LOCKED : ArenaGameEventType.SHOP_UNLOCKED;
		ArenaShopEvent event = new ArenaShopEvent(gameEvent.getArena(), eventType);
		ArenaAPI.PLUGIN.getPluginManager().callEvent(event);

		return true;
	}

	@Override
	@NotNull
	public ArenaConfig getArenaConfig() {
		return this.arenaConfig;
	}

	@NotNull
	@Override
	public JYML getConfig() {
		return config;
	}

	@Override
	@NotNull
	public List<String> getProblems() {
		return new ArrayList<>();
	}

	@Override
	@NotNull
	public EditorShopManager getEditor() {
		if (this.editor == null) {
			this.editor = new EditorShopManager(this);
		}
		return this.editor;
	}

	@NotNull
	public ArenaShopMainMenu getMenu() {
		if (this.menu == null) {
			this.menu = new ArenaShopMainMenu(this);
		}
		return menu;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	@NotNull
	@Override
	public Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> getStateTriggers() {
		return stateTriggers;
	}

	@NotNull
	@Override
	public ArenaLockState getState() {
		return state;
	}

	@Override
	public void setState(@NotNull ArenaLockState state) {
		this.state = state;
	}

	public boolean isHideOtherKitProducts() {
		return this.isHideOtherKitProducts;
	}

	public void setHideOtherKitProducts(boolean hideOtherKitProducts) {
		this.isHideOtherKitProducts = hideOtherKitProducts;
	}

	@NotNull
	public Map<String, ArenaShopCategory> getCategoryMap() {
		return this.categories;
	}

	@NotNull
	public Collection<ArenaShopCategory> getCategories() {
		return this.getCategoryMap().values();
	}

	@Nullable
	public ArenaShopCategory getCategory(@NotNull String id) {
		return this.getCategoryMap().get(id.toLowerCase());
	}

	public boolean open(@NotNull Player player) {
		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
		if (arenaPlayer == null) {
			return false;
		}
		
		AbstractArena arena = arenaPlayer.getArena();
		if (arena.getState() != ArenaState.INGAME || !this.isActive()) {
			plugin().getMessage(Lang.SHOP_OPEN_ERROR_DISABLED).send(player);
			return false;
		}
		
		if (this.getState() == ArenaLockState.LOCKED) {
			plugin().getMessage(Lang.SHOP_OPEN_ERROR_LOCKED).send(player);
			return false;
		}
		
		this.getMenu().open(player, 1);
		return true;
	}
}
