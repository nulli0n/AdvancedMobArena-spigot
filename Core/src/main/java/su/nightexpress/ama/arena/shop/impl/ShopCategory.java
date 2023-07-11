package su.nightexpress.ama.arena.shop.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.api.event.ArenaShopCategoryEvent;
import su.nightexpress.ama.arena.editor.shop.ShopCategorySettingsEditor;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.lock.Lockable;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.shop.menu.ShopCategoryMenu;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.kit.Kit;

import java.util.*;
import java.util.stream.Collectors;

public class ShopCategory implements ArenaChild, Lockable, Placeholder {

    private final ArenaConfig              arenaConfig;
    private final String                   id;
    private final PlaceholderMap           placeholderMap;
    private final Map<String, ShopProduct> products;

    private String       name;
    private List<String> description;
    private ItemStack    icon;
    private LockState    lockState;
    private Set<String>  allowedKits;

    private ShopCategoryMenu           menu;
    private ShopCategorySettingsEditor editor;

    public ShopCategory(@NotNull ArenaConfig config, @NotNull String id) {
        this(config, id,
            StringUtil.capitalizeUnderscored(id),
            new ArrayList<>(),
            new ItemStack(Material.APPLE),
            new HashSet<>(),
            new HashMap<>());
    }

    public ShopCategory(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull String name,
        @NotNull List<String> description,
        @NotNull ItemStack icon,
        @NotNull Set<String> allowedKits,
        @NotNull Map<String, ShopProduct> products
    ) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();

        this.setName(name);
        this.setDescription(description);
        this.setIcon(icon);
        this.lockState = LockState.UNLOCKED;
        this.setAllowedKits(allowedKits);
        this.products = products;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.SHOP_CATEGORY_ID, this::getId)
            .add(Placeholders.SHOP_CATEGORY_NAME, this::getName)
            .add(Placeholders.SHOP_CATEGORY_DESCRIPTION, () -> String.join("\n", this.getDescription()))
            .add(Placeholders.SHOP_CATEGORY_ALLOWED_KITS, () -> this.getAllowedKits().stream()
                .map(kidId -> plugin().getKitManager().getKitById(kidId)).filter(Objects::nonNull)
                .map(Kit::getName).map(Colorizer::strip).collect(Collectors.joining(", ")))
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public ShopCategorySettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new ShopCategorySettingsEditor(this);
        }
        return this.editor;
    }

    public void clear() {
        this.getProducts().forEach(ShopProduct::clear);
        this.getProducts().clear();
        if (this.menu != null) {
            this.menu.clear();
            this.menu = null;
        }
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public ShopCategoryMenu getMenu() {
        if (this.menu == null) {
            this.menu = new ShopCategoryMenu(this);
        }
        return this.menu;
    }

    public boolean createProduct(@NotNull String id) {
        if (this.getProduct(id) != null) return false;

        ICurrency currency = plugin().getCurrencyManager().getCurrencyFirst();
        ShopProduct product = new ShopProduct(this, id, currency);
        this.getProductsMap().put(product.getId(), product);
        return true;
    }

    public boolean isAvailable(@NotNull ArenaPlayer arenaPlayer) {
        if (this.getArenaConfig().getGameplayManager().isKitsEnabled()) {
            if (this.getAllowedKits().isEmpty() || this.getAllowedKits().contains(Placeholders.WILDCARD)) return true;

            Kit kit = arenaPlayer.getKit();
            return kit != null && this.getAllowedKits().contains(kit.getId());
        }
        return true;
    }

    public boolean open(@NotNull ArenaPlayer arenaPlayer) {
        Arena arena = arenaPlayer.getArena();
        if (arena.getState() != GameState.INGAME || !arena.getConfig().getShopManager().isActive()) {
            plugin().getMessage(Lang.SHOP_OPEN_ERROR_DISABLED).send(arenaPlayer.getPlayer());
            return false;
        }

        if (this.isLocked()) {
            plugin().getMessage(Lang.SHOP_CATEGORY_OPEN_ERROR_LOCKED).replace(this.replacePlaceholders()).send(arenaPlayer.getPlayer());
            return false;
        }

        if (!this.isAvailable(arenaPlayer)) {
            plugin().getMessage(Lang.SHOP_CATEGORY_OPEN_ERROR_UNAVAILABLE).send(arenaPlayer.getPlayer());
            return false;
        }

        this.getMenu().open(arenaPlayer.getPlayer(), 1);
        return true;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public ShopManager getShopManager() {
        return this.getArenaConfig().getShopManager();
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public LockState getLockState() {
        return lockState;
    }

    @Override
    public void setLockState(@NotNull LockState lockState) {
        this.lockState = lockState;

        ArenaGameEventType eventType = this.isLocked() ? ArenaGameEventType.SHOP_CATEGORY_LOCKED : ArenaGameEventType.SHOP_CATEGORY_UNLOCKED;
        ArenaShopCategoryEvent event = new ArenaShopCategoryEvent(this.getArena(), this, eventType);
        this.plugin().getPluginManager().callEvent(event);
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = Colorizer.apply(description);
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
        ItemUtil.mapMeta(this.icon, meta -> {
            meta.setDisplayName(null);
            meta.setLore(null);
            meta.addItemFlags(ItemFlag.values());
        });
    }

    @NotNull
    public Set<String> getAllowedKits() {
        return allowedKits;
    }

    public void setAllowedKits(@NotNull Set<String> allowedKits) {
        this.allowedKits = allowedKits;
    }

    @NotNull
    public Map<String, ShopProduct> getProductsMap() {
        return this.products;
    }

    @NotNull
    public Collection<ShopProduct> getProducts() {
        return this.getProductsMap().values();
    }

    @Nullable
    public ShopProduct getProduct(@NotNull String id) {
        return this.getProductsMap().get(id.toLowerCase());
    }
}
