package su.nightexpress.ama.arena.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListenerState;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaShopProductEvent;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.editor.shop.EditorShopProductSettings;
import su.nightexpress.ama.kit.Kit;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ArenaShopProduct implements IArenaGameEventListenerState, ArenaChild, ICleanable, IEditable {

    private final ArenaShopCategory                                  shopCategory;
    private final String                                             id;
    private final Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> stateTriggers;

    private String          name;
    private List<String>    description;
    private ArenaLockState  state;
    private ICurrency       currency;
    private double          price;
    private Set<String>     applicableKits;
    private ItemStack       icon;
    private List<String>    commands;
    private List<ItemStack> items;

    private EditorShopProductSettings editor;

    public ArenaShopProduct(@NotNull ArenaShopCategory shopCategory, @NotNull String id, @NotNull ICurrency currency) {
        this(shopCategory, id,
            StringUtil.capitalizeFully(id.replace("_", " ")),
            new ArrayList<>(),
            currency,
            10,
            new HashMap<>(),
            new HashSet<>(),
            new ItemStack(Material.APPLE),
            new ArrayList<>(),
            new ArrayList<>());
    }

    public ArenaShopProduct(
        @NotNull ArenaShopCategory shopCategory,
        @NotNull String id,
        @NotNull String name,
        @NotNull List<String> description,
        @NotNull ICurrency currency,
        double price,
        @NotNull Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> stateTriggers,
        @NotNull Set<String> applicableKits,
        @NotNull ItemStack icon,
        @NotNull List<String> commands,
        @NotNull List<ItemStack> items
    ) {
        this.shopCategory = shopCategory;
        this.id = id.toLowerCase();
        this.setName(name);
        this.setDescription(description);
        this.setState(ArenaLockState.UNLOCKED);

        this.stateTriggers = stateTriggers;
        this.setCurrency(currency);
        this.setPrice(price);
        this.setApplicableKits(applicableKits);
        this.setIcon(icon);
        this.setCommands(commands);
        this.setItems(items);
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.SHOP_PRODUCT_TRIGGERS_LOCKED, Placeholders.format(this.getStateTriggers(ArenaLockState.LOCKED)))
            .replace(Placeholders.SHOP_PRODUCT_TRIGGERS_UNLOCKED, Placeholders.format(this.getStateTriggers(ArenaLockState.UNLOCKED)))
            .replace(Placeholders.SHOP_PRODUCT_ID, this.getId())
            .replace(Placeholders.SHOP_PRODUCT_CURRENCY, this.getCurrency().getConfig().getName())
            .replace(Placeholders.SHOP_PRODUCT_NAME, this.getName())
            .replace(Placeholders.SHOP_PRODUCT_DESCRIPTION, String.join("\n", this.getDescription()))
            .replace(Placeholders.SHOP_PRODUCT_PRICE, this.getCurrency().format(this.getPrice()))
            .replace(Placeholders.SHOP_PRODUCT_APPLICABLE_KITS, this.getApplicableKits().stream()
                .map(kidId -> ArenaAPI.getKitManager().getKitById(kidId))
                .filter(Objects::nonNull).map(Kit::getName).collect(Collectors.joining(", ")))
            .replace(Placeholders.SHOP_PRODUCT_COMMANDS, String.join("\n", this.getCommands()))
            //.replace(Placeholders.SHOP_PRODUCT_ITEM_LORE, String.join("\n", ItemUtil.getLore(this.getIcon())))
            .replace(Placeholders.SHOP_PRODUCT_ICON_TYPE, this.getIcon().getType().name())
            ;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        ArenaLockState state = this.getState().getOpposite();
        this.setState(state);

        ArenaGameEventType eventType = state == ArenaLockState.LOCKED ? ArenaGameEventType.SHOP_ITEM_LOCKED : ArenaGameEventType.SHOP_ITEM_UNLOCKED;
        ArenaShopProductEvent regionEvent = new ArenaShopProductEvent(gameEvent.getArena(), eventType, this);
        ArenaAPI.PLUGIN.getPluginManager().callEvent(regionEvent);

        return true;
    }

    public boolean isAvailable(@NotNull ArenaPlayer arenaPlayer) {
        if (!this.getApplicableKits().isEmpty()) {
            if (arenaPlayer.getArena().getConfig().getGameplayManager().isKitsEnabled()) {
                Kit kit = arenaPlayer.getKit();
                if (kit == null || !this.getApplicableKits().contains(kit.getId())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    @NotNull
    public EditorShopProductSettings getEditor() {
        if (this.editor == null) {
            this.editor = new EditorShopProductSettings(this);
        }
        return this.editor;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    public void give(@NotNull Player player) {
        this.getCommands().forEach(cmd -> PlayerUtil.dispatchCommand(player, cmd));
        this.getItems().forEach(item -> PlayerUtil.addItem(player, item));
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.getShopCategory().getArenaConfig();
    }

    @NotNull
    public ArenaShopCategory getShopCategory() {
        return shopCategory;
    }

    @NotNull
    public String getId() {
        return this.id;
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

    @Override
    @NotNull
    public ArenaLockState getState() {
        return state;
    }

    @Override
    public void setState(@NotNull ArenaLockState state) {
        this.state = state;
    }

    @Override
    @NotNull
    public Map<ArenaLockState, Set<ArenaGameEventTrigger<?>>> getStateTriggers() {
        return this.stateTriggers;
    }

    @NotNull
    public ICurrency getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull ICurrency currency) {
        this.currency = currency;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @NotNull
    public Set<String> getApplicableKits() {
        return this.applicableKits;
    }

    public void setApplicableKits(@NotNull Set<String> applicableKits) {
        this.applicableKits = new HashSet<>(applicableKits.stream().map(String::toLowerCase).toList());
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = commands;
    }

    @NotNull
    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(@NotNull List<ItemStack> items) {
        this.items = new ArrayList<>(items);
        this.items.removeIf(item -> item == null || item.getType().isAir());
    }
}
