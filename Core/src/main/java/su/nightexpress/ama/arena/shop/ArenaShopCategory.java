package su.nightexpress.ama.arena.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListenerState;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaShopCategoryEvent;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.ArenaPlayer;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.shop.EditorShopCategorySettings;
import su.nightexpress.ama.arena.shop.menu.ArenaShopCategoryMenu;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.kit.Kit;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ArenaShopCategory implements IArenaGameEventListenerState, IArenaObject, ICleanable, IEditable {

    private final ArenaConfig                                        arenaConfig;
    private final String                                             id;
    private final Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> stateTriggers;
    private final Map<String, ArenaShopProduct>                      products;

    private String       name;
    private List<String> description;
    private ItemStack    icon;
    private ArenaLockState state;
    private Set<String> allowedKits;

    private ArenaShopCategoryMenu      menu;
    private EditorShopCategorySettings editor;

    public ArenaShopCategory(@NotNull ArenaConfig config, @NotNull String id) {
        this(config, id,
            StringUtil.capitalizeFully(id.replace("_", " ")),
            new ArrayList<>(),
            new ItemStack(Material.APPLE),
            new HashMap<>(),
            new HashSet<>(),
            new HashMap<>());
    }

    public ArenaShopCategory(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull String name,
        @NotNull List<String> description,
        @NotNull ItemStack icon,
        @NotNull Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> stateTriggers,
        @NotNull Set<String> allowedKits,
        @NotNull Map<String, ArenaShopProduct> products
    ) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();

        this.setName(name);
        this.setDescription(description);
        this.setIcon(icon);
        this.setState(ArenaLockState.UNLOCKED);
        this.stateTriggers = stateTriggers;
        this.setAllowedKits(allowedKits);
        this.products = products;

        ItemUtil.replace(this.icon, this.replacePlaceholders());
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.SHOP_CATEGORY_ID, this.getId())
            .replace(Placeholders.SHOP_CATEGORY_NAME, this.getName())
            .replace(Placeholders.SHOP_CATEGORY_DESCRIPTION, String.join("\n", this.getDescription()))
            .replace(Placeholders.SHOP_CATEGORY_ICON_TYPE, plugin().getLangManager().getEnum(this.getIcon().getType()))
            //.replace(Placeholders.SHOP_CATEGORY_ICON_NAME, ItemUtil.getItemName(this.getIcon()))
            //.replace(Placeholders.SHOP_CATEGORY_ICON_LORE, String.join("\n", ItemUtil.getLore(this.getIcon())))
            .replace(Placeholders.SHOP_CATEGORY_ALLOWED_KITS, this.getAllowedKits().stream()
                .map(kidId -> ArenaAPI.getKitManager().getKitById(kidId)).filter(Objects::nonNull)
                .map(Kit::getName).map(StringUtil::colorOff).collect(Collectors.joining(", ")))
            .replace(Placeholders.SHOP_CATEGORY_TRIGGERS_LOCKED, Placeholders.format(this.getStateTriggers(ArenaLockState.LOCKED)))
            .replace(Placeholders.SHOP_CATEGORY_TRIGGERS_UNLOCKED, Placeholders.format(this.getStateTriggers(ArenaLockState.UNLOCKED)))
            ;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        ArenaLockState state = this.getState().getOpposite();
        this.setState(state);

        ArenaGameEventType eventType = state == ArenaLockState.LOCKED ? ArenaGameEventType.SHOP_CATEGORY_LOCKED : ArenaGameEventType.SHOP_CATEGORY_UNLOCKED;
        ArenaShopCategoryEvent event = new ArenaShopCategoryEvent(gameEvent.getArena(), this, eventType);
        ArenaAPI.PLUGIN.getPluginManager().callEvent(event);

        return true;
    }

    public boolean isAvailable(@NotNull ArenaPlayer arenaPlayer) {
        if (!this.getAllowedKits().isEmpty() && this.getArena().getConfig().getGameplayManager().isKitsEnabled()) {
            Kit kit = arenaPlayer.getKit();
            if (kit == null || !this.getAllowedKits().contains(kit.getId())) {
                return false;
            }
        }

        return true;
    }

    @Override
    @NotNull
    public EditorShopCategorySettings getEditor() {
        if (this.editor == null) {
            this.editor = new EditorShopCategorySettings(this);
        }
        return this.editor;
    }

    @Override
    public void clear() {
        this.getProducts().forEach(ArenaShopProduct::clear);
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
    public ArenaShopCategoryMenu getMenu() {
        if (this.menu == null) {
            this.menu = new ArenaShopCategoryMenu(this);
        }
        return this.menu;
    }

    public boolean open(@NotNull ArenaPlayer arenaPlayer) {
        AbstractArena arena = arenaPlayer.getArena();
        if (arena.getState() != ArenaState.INGAME || !arena.getConfig().getShopManager().isActive()) {
            plugin().getMessage(Lang.SHOP_OPEN_ERROR_DISABLED).send(arenaPlayer.getPlayer());
            return false;
        }

        if (this.getState() == ArenaLockState.LOCKED) {
            plugin().getMessage(Lang.SHOP_CATEGORY_OPEN_ERROR_LOCKED)
                .replace(this.replacePlaceholders())
                .send(arenaPlayer.getPlayer());
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
    public ArenaShopManager getShopManager() {
        return this.getArenaConfig().getShopManager();
    }

    @NotNull
    public String getId() {
        return id;
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

    @NotNull
    @Override
    public Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> getStateTriggers() {
        return stateTriggers;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUtil.color(name);
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = StringUtil.color(description);
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }

    @NotNull
    public Set<String> getAllowedKits() {
        return allowedKits;
    }

    public void setAllowedKits(@NotNull Set<String> allowedKits) {
        this.allowedKits = allowedKits;
    }

    @NotNull
    public Map<String, ArenaShopProduct> getProductsMap() {
        return this.products;
    }

    @NotNull
    public Collection<ArenaShopProduct> getProducts() {
        return this.getProductsMap().values();
    }

    @Nullable
    public ArenaShopProduct getProduct(@NotNull String id) {
        return this.getProductsMap().get(id.toLowerCase());
    }
}
