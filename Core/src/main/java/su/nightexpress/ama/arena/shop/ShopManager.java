package su.nightexpress.ama.arena.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.Loadable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.api.event.ArenaShopEvent;
import su.nightexpress.ama.arena.editor.shop.ShopManagerEditor;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.lock.Lockable;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.Parameters;
import su.nightexpress.ama.arena.script.action.ScriptActions;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.arena.shop.menu.ShopMainMenu;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.config.Lang;

import java.util.*;

public class ShopManager implements ConfigHolder, ArenaChild, Lockable, Loadable, Problematic, Placeholder {

    private static final String CONFIG_NAME = "shop.yml";

    private final ArenaConfig                    arenaConfig;
    private final JYML                      config;
    private final Map<String, ShopCategory> categories;
    private final PlaceholderMap placeholderMap;

    private boolean   isActive;
    private boolean   isHideOtherKitProducts;
    private LockState lockState;

    private ShopMainMenu      menu;
    private ShopManagerEditor editor;

    public ShopManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.config = new JYML(arenaConfig.getFile().getParentFile().getAbsolutePath(), CONFIG_NAME);
        this.categories = new LinkedHashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.SHOP_MANAGER_IS_ACTIVE, () -> LangManager.getBoolean(this.isActive()))
            .add(Placeholders.SHOP_MANAGER_HIDE_OTHER_KIT_ITEMS, () -> LangManager.getBoolean(this.isHideOtherKitProducts()))
        ;
    }

    @Override
    public void setup() {
        this.setActive(config.getBoolean("Settings.Active"));
        this.setHideOtherKitProducts(config.getBoolean("Settings.Hide_Other_Kit_Products"));
        this.lockState = (LockState.UNLOCKED);

        for (LockState lockState : LockState.values()) {
            // ----------- CONVERT SCRIPTS START -----------
            for (String eventRaw : config.getSection("Settings.State." + lockState.name() + ".Triggers")) {
                ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                if (eventType == null) continue;

                String name = "shop_" + lockState.name().toLowerCase();
                ArenaScript script = new ArenaScript(this.arenaConfig, name, eventType);

                String values = config.getString("Settings.State." + lockState.name() + ".Triggers." + eventRaw, "");
                Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                script.getConditions().putAll(conditions);

                ScriptPreparedAction action = new ScriptPreparedAction(lockState == LockState.LOCKED ? ScriptActions.LOCK_SHOP : ScriptActions.UNLOCK_SHOP, new ParameterResult());
                //action.getParameters().add(Parameters.REGION, this.getId());
                script.getActions().add(action);

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            config.remove("Settings.State." + lockState.name() + ".Triggers");
            // ----------- CONVERT SCRIPTS END -----------
        }

        for (String catId : config.getSection("Categories")) {
            String path = "Categories." + catId + ".";

            String catName = config.getString(path + "Name", catId);
            List<String> catDesc = config.getStringList(path + "Description");
            ItemStack catIcon = config.getItem(path + "Icon");

            for (LockState lockState : LockState.values()) {
                // ----------- CONVERT SCRIPTS START -----------
                for (String eventRaw : config.getSection(path + "State." + lockState.name() + ".Triggers")) {
                    ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                    if (eventType == null) continue;

                    String name = "shop_category_" + catId + "_" + lockState.name().toLowerCase();
                    ArenaScript script = new ArenaScript(this.arenaConfig, name, eventType);

                    String values = config.getString(path + "State." + lockState.name() + ".Triggers." + eventRaw, "");
                    Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                    script.getConditions().putAll(conditions);

                    ScriptPreparedAction action = new ScriptPreparedAction(lockState == LockState.LOCKED ? ScriptActions.LOCK_SHOP_CATEGORY : ScriptActions.UNLOCK_SHOP_CATEGORY, new ParameterResult());
                    action.getParameters().add(Parameters.SHOP_CATEGORY, catId);
                    script.getActions().add(action);

                    this.getArenaConfig().getScriptManager().addConverted(script);
                }
                config.remove(path + "State." + lockState.name() + ".Triggers");
                // ----------- CONVERT SCRIPTS END -----------
            }
            Set<String> catAllowedKits = config.getStringSet(path + "Allowed_Kits");
            Map<String, ShopProduct> catProducts = new LinkedHashMap<>();

            ShopCategory category = new ShopCategory(arenaConfig, catId, catName, catDesc, catIcon, catAllowedKits, catProducts);

            for (String prId : config.getSection(path + "Products")) {
                String path2 = path + "Products." + prId + ".";

                String prName = config.getString(path2 + "Name", prId);
                List<String> prDesc = config.getStringList(path2 + "Description");

                ICurrency prCurrency = plugin().getCurrencyManager().getCurrency(config.getString(path2 + "Currency", ""));
                if (prCurrency == null) prCurrency = plugin().getCurrencyManager().getCurrencyFirst();

                double prPrice = config.getDouble(path2 + "Price");

                for (LockState lockState : LockState.values()) {
                    // ----------- CONVERT SCRIPTS START -----------
                    for (String eventRaw : config.getSection(path2 + "State." + lockState.name() + ".Triggers")) {
                        ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                        if (eventType == null) continue;

                        String name = "shop_product_" + catId + "_" + prId + "_" + lockState.name().toLowerCase();
                        ArenaScript script = new ArenaScript(this.arenaConfig, name, eventType);

                        String values = config.getString(path2 + "State." + lockState.name() + ".Triggers." + eventRaw, "");
                        Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                        script.getConditions().putAll(conditions);

                        ScriptPreparedAction action = new ScriptPreparedAction(lockState == LockState.LOCKED ? ScriptActions.LOCK_SHOP_PRODUCT : ScriptActions.UNLOCK_SHOP_PRODUCT, new ParameterResult());
                        action.getParameters().add(Parameters.SHOP_CATEGORY, catId);
                        action.getParameters().add(Parameters.SHOP_PRODUCT, prId);
                        script.getActions().add(action);

                        this.getArenaConfig().getScriptManager().addConverted(script);
                    }
                    config.remove(path2 + "State." + lockState.name() + ".Triggers");
                    // ----------- CONVERT SCRIPTS END -----------
                }

                Set<String> prAllowedKits = config.getStringSet(path2 + "Allowed_Kits");
                ItemStack prPreview = config.getItem(path2 + "Preview");
                if (prPreview.getType().isAir()) {
                    plugin().error("Null preview for '" + prId + "' item in '" + arenaConfig.getId() + "' arena shop!");
                    //continue;
                }

                List<String> prCommands = config.getStringList(path2 + "Commands");
                List<ItemStack> prItems = Arrays.asList(config.getItemsEncoded(path2 + "Items"));

                ShopProduct product = new ShopProduct(category, prId, prName, prDesc, prCurrency, prPrice, prAllowedKits, prPreview, prCommands, prItems);
                category.getProductsMap().put(product.getId(), product);
            }

            this.categories.put(category.getId(), category);
        }

        config.saveChanges();
    }

    @Override
    public void shutdown() {
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
        config.set("Settings.Active", this.isActive());
        config.set("Settings.Hide_Other_Kit_Products", this.isHideOtherKitProducts());
        config.set("Settings.State", null);

        config.set("Categories", null);
        this.getCategories().forEach(category -> {
            String path = "Categories." + category.getId() + ".";

            config.set(path + "Name", category.getName());
            config.set(path + "Description", category.getDescription());
            config.setItem(path + "Icon", category.getIcon());
            config.set(path + "Allowed_Kits", category.getAllowedKits());
            category.getProducts().forEach(shopProduct -> {
                String path2 = path + "Products." + shopProduct.getId() + ".";

                config.set(path2 + "Name", shopProduct.getName());
                config.set(path2 + "Description", shopProduct.getDescription());
                config.set(path2 + "Price", shopProduct.getPrice());
                config.set(path2 + "Allowed_Kits", shopProduct.getAllowedKits());
                config.setItem(path2 + "Preview", shopProduct.getIcon());
                config.set(path2 + "Commands", shopProduct.getCommands());
                config.setItemsEncoded(path2 + "Items", shopProduct.getItems());
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

        ArenaGameEventType eventType = this.isLocked() ? ArenaGameEventType.SHOP_LOCKED : ArenaGameEventType.SHOP_UNLOCKED;
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
