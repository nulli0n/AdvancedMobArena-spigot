package su.nightexpress.ama.arena.shop.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.event.ArenaShopProductEvent;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.editor.shop.ProductSettingsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.lock.Lockable;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.kit.impl.Kit;
import su.nightexpress.ama.stats.object.StatType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShopProduct implements ArenaChild, Lockable, Inspectable, Placeholder {

    private final AMA            plugin;
    private final ShopCategory   category;
    private final String         id;
    private final PlaceholderMap placeholderMap;

    private String          name;
    private List<String>    description;
    private LockState       lockState;
    private Currency        currency;
    private double          price;
    private Set<String>     kitsRequired;
    private ItemStack       icon;
    private List<String>    commands;
    private List<ItemStack> items;

    private ProductSettingsEditor editor;

    public ShopProduct(@NotNull AMA plugin, @NotNull ShopCategory category, @NotNull String id, @NotNull Currency currency) {
        this(plugin, category, id,
            StringUtil.capitalizeUnderscored(id),
            new ArrayList<>(),
            currency,
            10,
            new HashSet<>(),
            new ItemStack(Material.APPLE),
            new ArrayList<>(),
            new ArrayList<>());
    }

    public ShopProduct(
        @NotNull AMA plugin,
        @NotNull ShopCategory category,
        @NotNull String id,
        @NotNull String name,
        @NotNull List<String> description,
        @NotNull Currency currency,
        double price,
        @NotNull Set<String> applicableKits,
        @NotNull ItemStack icon,
        @NotNull List<String> commands,
        @NotNull List<ItemStack> items
    ) {
        this.plugin = plugin;
        this.category = category;
        this.id = id.toLowerCase();
        this.setName(name);
        this.setDescription(description);
        this.lockState = LockState.UNLOCKED;

        this.setCurrency(currency);
        this.setPrice(price);
        this.setKitsRequired(applicableKits);
        this.setIcon(icon);
        this.setCommands(commands);
        this.setItems(items);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.SHOP_PRODUCT_REPORT, () -> String.join("\n", this.getReport().getFullReport()))
            .add(Placeholders.SHOP_PRODUCT_ID, this::getId)
            .add(Placeholders.SHOP_PRODUCT_NAME, this::getName)
            .add(Placeholders.SHOP_PRODUCT_DESCRIPTION, () -> String.join("\n", this.getDescription()))
            .add(Placeholders.SHOP_PRODUCT_PRICE, () -> this.getCurrency().format(this.getPrice()))
            .add(Placeholders.SHOP_PRODUCT_CURRENCY, () -> this.getCurrency().getName())
            .add(Placeholders.SHOP_PRODUCT_KITS_REQUIRED, () -> {
                List<String> list = new ArrayList<>();

                if (this.getKitsRequired().isEmpty()) {
                    list.add(Report.good("All kits are allowed!"));
                }
                else {
                    this.getKitsRequired().forEach(kitId -> {
                        Kit kit = plugin.getKitManager().getKitById(kitId);
                        if (kit != null) {
                            list.add(Report.good(kit.getName()));
                        }
                        else list.add(Report.problem(kitId));
                    });
                }
                return String.join("\n", list);
            })
            .add(Placeholders.SHOP_PRODUCT_COMMANDS, () -> String.join("\n", this.getCommands()))
        ;
    }

    // TODO Option for limited purchases amount & cooldown

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        if (this.getCommands().isEmpty() && this.getItems().isEmpty()) {
            report.addWarn("Product has no commands & items!");
        }

        return report;
    }

    public boolean purchase(@NotNull ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();

        if (this.isLocked()) {
            plugin.getMessage(Lang.SHOP_PRODUCT_ERROR_LOCKED).send(player);
            return false;
        }
        if (!this.isAvailable(arenaPlayer)) {
            plugin.getMessage(Lang.SHOP_PRODUCT_ERROR_UNAVAILABLE).send(player);
            return false;
        }

        double price = this.getPrice();
        double balance = this.getCurrency().getHandler().getBalance(player);
        if (balance < price) {
            plugin.getMessage(Lang.SHOP_PRODUCT_ERROR_NOT_ENOUGH_FUNDS).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        this.getCurrency().getHandler().take(player, price);
        this.give(player);

        arenaPlayer.addStats(StatType.COINS_SPENT, (int) price);
        plugin.getMessage(Lang.SHOP_PRODUCT_PURCHASE).replace(this.replacePlaceholders()).send(player);
        return true;
    }

    public boolean isAvailable(@NotNull ArenaPlayer arenaPlayer) {
        if (this.getArenaConfig().getGameplaySettings().isKitsEnabled()) {
            if (this.getKitsRequired().isEmpty() || this.getKitsRequired().contains(Placeholders.WILDCARD)) return true;

            Kit kit = arenaPlayer.getKit();
            return kit != null && this.getKitsRequired().contains(kit.getId());
        }
        return true;
    }

    @NotNull
    public ProductSettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new ProductSettingsEditor(this.plugin, this);
        }
        return this.editor;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getDescription().clear();
        this.getItems().clear();
        this.getCommands().clear();
        this.getKitsRequired().clear();
    }

    public void give(@NotNull Player player) {
        this.getCommands().forEach(cmd -> PlayerUtil.dispatchCommand(player, cmd));
        this.getItems().forEach(item -> PlayerUtil.addItem(player, item));
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.getCategory().getArenaConfig();
    }

    @NotNull
    public ShopCategory getCategory() {
        return category;
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
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = Colorizer.apply(description);
    }

    @Override
    @NotNull
    public LockState getLockState() {
        return lockState;
    }

    @Override
    public void setLockState(@NotNull LockState lockState) {
        this.lockState = lockState;

        GameEventType eventType = this.isLocked() ? GameEventType.SHOP_ITEM_LOCKED : GameEventType.SHOP_ITEM_UNLOCKED;
        ArenaShopProductEvent regionEvent = new ArenaShopProductEvent(this.getArena(), eventType, this);
        this.plugin.getPluginManager().callEvent(regionEvent);
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull Currency currency) {
        this.currency = currency;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @NotNull
    public Set<String> getKitsRequired() {
        return this.kitsRequired;
    }

    public void setKitsRequired(@NotNull Set<String> kitsRequired) {
        this.kitsRequired = kitsRequired.stream().map(String::toLowerCase).collect(Collectors.toCollection(HashSet::new));
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(icon);
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
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = new ArrayList<>(commands);
    }

    @NotNull
    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(@NotNull List<ItemStack> items) {
        this.items = new ArrayList<>(items);
        this.getItems().removeIf(item -> item == null || item.getType().isAir());
    }
}
