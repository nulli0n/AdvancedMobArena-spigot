package su.nightexpress.ama.arena.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.event.ArenaShopEvent;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.editor.shop.ShopManagerEditor;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.lock.Lockable;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.arena.shop.menu.ShopMainMenu;
import su.nightexpress.ama.config.Lang;

import java.util.*;

public class ShopManager extends AbstractConfigHolder<AMA> implements ArenaChild, Lockable, Problematic, Placeholder {

    public static final String CONFIG_NAME = "shop.yml";

    private final ArenaConfig                    arenaConfig;
    private final Map<String, ShopCategory> categories;
    private final PlaceholderMap placeholderMap;

    private boolean   isActive;
    private boolean   isHideOtherKitProducts;
    private LockState lockState;

    private ShopMainMenu      menu;
    private ShopManagerEditor editor;

    public ShopManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.categories = new LinkedHashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.SHOP_MANAGER_IS_ACTIVE, () -> LangManager.getBoolean(this.isActive()))
            .add(Placeholders.SHOP_MANAGER_HIDE_OTHER_KIT_ITEMS, () -> LangManager.getBoolean(this.isHideOtherKitProducts()))
        ;
    }

    @Override
    public boolean load() {
        this.setActive(cfg.getBoolean("Settings.Active"));
        this.setHideOtherKitProducts(cfg.getBoolean("Settings.Hide_Other_Kit_Products"));
        this.lockState = (LockState.UNLOCKED);

        for (String catId : cfg.getSection("Categories")) {
            String path = "Categories." + catId + ".";

            String catName = cfg.getString(path + "Name", catId);
            List<String> catDesc = cfg.getStringList(path + "Description");
            ItemStack catIcon = cfg.getItem(path + "Icon");
            Set<String> catAllowedKits = cfg.getStringSet(path + "Allowed_Kits");
            Map<String, ShopProduct> catProducts = new LinkedHashMap<>();

            ShopCategory category = new ShopCategory(arenaConfig, catId, catName, catDesc, catIcon, catAllowedKits, catProducts);

            for (String prId : cfg.getSection(path + "Products")) {
                if (!plugin().getCurrencyManager().hasCurrency()) continue;

                String path2 = path + "Products." + prId + ".";

                String prName = cfg.getString(path2 + "Name", prId);
                List<String> prDesc = cfg.getStringList(path2 + "Description");

                Currency prCurrency = plugin().getCurrencyManager().getCurrency(cfg.getString(path2 + "Currency", ""));
                if (prCurrency == null) prCurrency = plugin().getCurrencyManager().getAny();

                double prPrice = cfg.getDouble(path2 + "Price");

                Set<String> prAllowedKits = cfg.getStringSet(path2 + "Allowed_Kits");
                ItemStack prPreview = cfg.getItem(path2 + "Preview");
                if (prPreview.getType().isAir()) {
                    plugin().error("Null preview for '" + prId + "' item in '" + arenaConfig.getId() + "' arena shop!");
                    //continue;
                }

                List<String> prCommands = cfg.getStringList(path2 + "Commands");
                List<ItemStack> prItems = Arrays.asList(cfg.getItemsEncoded(path2 + "Items"));

                ShopProduct product = new ShopProduct(category, prId, prName, prDesc, prCurrency, prPrice, prAllowedKits, prPreview, prCommands, prItems);
                category.getProductsMap().put(product.getId(), product);
            }

            this.categories.put(category.getId(), category);
        }

        cfg.saveChanges();
        return true;
    }

    public void clear() {
        this.getCategories().forEach(ShopCategory::clear);
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
        cfg.set("Settings.Active", this.isActive());
        cfg.set("Settings.Hide_Other_Kit_Products", this.isHideOtherKitProducts());
        cfg.set("Settings.State", null);

        cfg.remove("Categories");
        this.getCategories().forEach(category -> {
            String path = "Categories." + category.getId() + ".";

            cfg.set(path + "Name", category.getName());
            cfg.set(path + "Description", category.getDescription());
            cfg.setItem(path + "Icon", category.getIcon());
            cfg.set(path + "Allowed_Kits", category.getAllowedKits());
            category.getProducts().forEach(shopProduct -> {
                String path2 = path + "Products." + shopProduct.getId() + ".";

                cfg.set(path2 + "Name", shopProduct.getName());
                cfg.set(path2 + "Description", shopProduct.getDescription());
                cfg.set(path2 + "Price", shopProduct.getPrice());
                cfg.set(path2 + "Allowed_Kits", shopProduct.getAllowedKits());
                cfg.setItem(path2 + "Preview", shopProduct.getIcon());
                cfg.set(path2 + "Commands", shopProduct.getCommands());
                cfg.setItemsEncoded(path2 + "Items", shopProduct.getItems());
            });
        });
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        return new ArrayList<>();
    }

    @NotNull
    public ShopManagerEditor getEditor() {
        if (this.editor == null) {
            this.editor = new ShopManagerEditor(this);
        }
        return this.editor;
    }

    @NotNull
    public ShopMainMenu getMenu() {
        if (this.menu == null) {
            this.menu = new ShopMainMenu(this);
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
    public LockState getLockState() {
        return lockState;
    }

    @Override
    public void setLockState(@NotNull LockState lockState) {
        this.lockState = lockState;

        GameEventType eventType = this.isLocked() ? GameEventType.SHOP_LOCKED : GameEventType.SHOP_UNLOCKED;
        ArenaShopEvent event = new ArenaShopEvent(this.getArena(), eventType);
        this.plugin().getPluginManager().callEvent(event);
    }

    public boolean isHideOtherKitProducts() {
        return this.isHideOtherKitProducts;
    }

    public void setHideOtherKitProducts(boolean hideOtherKitProducts) {
        this.isHideOtherKitProducts = hideOtherKitProducts;
    }

    @NotNull
    public Map<String, ShopCategory> getCategoryMap() {
        return this.categories;
    }

    @NotNull
    public Collection<ShopCategory> getCategories() {
        return this.getCategoryMap().values();
    }

    @Nullable
    public ShopCategory getCategory(@NotNull String id) {
        return this.getCategoryMap().get(id.toLowerCase());
    }

    public boolean createCategory(@NotNull String id) {
        if (this.getCategory(id) != null) return false;

        ShopCategory category = new ShopCategory(this.getArenaConfig(), id);
        this.getCategoryMap().put(category.getId(), category);
        return true;
    }

    public boolean open(@NotNull Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) {
            return false;
        }

        Arena arena = arenaPlayer.getArena();
        if (arena.getState() != GameState.INGAME || !this.isActive()) {
            plugin().getMessage(Lang.SHOP_OPEN_ERROR_DISABLED).send(player);
            return false;
        }

        if (this.isLocked()) {
            plugin().getMessage(Lang.SHOP_OPEN_ERROR_LOCKED).send(player);
            return false;
        }

        this.getMenu().open(player, 1);
        return true;
    }
}
