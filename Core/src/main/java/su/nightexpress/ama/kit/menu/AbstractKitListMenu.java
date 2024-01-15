package su.nightexpress.ama.kit.menu;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.editor.EditorLocales;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.game.GameplaySettings;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static su.nexmedia.engine.utils.Colors2.*;
import static su.nightexpress.ama.Placeholders.*;

public abstract class AbstractKitListMenu extends ConfigMenu<AMA> implements AutoPaged<Kit> {

    protected static final String PLACEHOLDER_POTIONS    = "%potions%";
    protected static final String PLACEHOLDER_ATTRIBUTES = "%attributes%";
    protected static final String PLACEHOLDER_STATUS     = "%status%";
    protected static final String PLACEHOLDER_LIMIT      = "%limit%";

    protected String       kitName;
    protected List<String> kitLore;
    protected int[]        kitSlots;
    protected List<String> potionsLore;
    protected List<String> limitLore;
    protected List<String> attributesLore;
    protected String       attributeGoodText;
    protected String       attributeBadText;
    protected List<String> statusAvailableLore;
    protected List<String> statusPlayerLimitLore;
    protected List<String> statusNoPermLore;
    protected List<String> statusNoFundsLore;

    public AbstractKitListMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.registerHandler(ButtonType.class)
            .addClick(ButtonType.KIT_SELECTOR, (viewer, event) -> plugin.getKitManager().getSelectMenu().openNextTick(viewer.getPlayer(), 1))
            .addClick(ButtonType.KIT_SHOP, (viewer, event) -> plugin.getKitManager().getShopMenu().openNextTick(viewer.getPlayer(), 1));

        this.load();
    }

    @Override
    public boolean isCodeCreation() {
        return true;
    }

    @Override
    @NotNull
    protected abstract MenuOptions createDefaultOptions();

    @NotNull
    @Override
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack blackFillItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        list.add(new MenuItem(blackFillItem).setSlots(0,8,45,53));

        ItemStack whiteFillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        list.add(new MenuItem(whiteFillItem).setSlots(1,2,3,4,5,6,7,9,18,27,36,17,26,35,44,46,47,48,49,50,51,52));

        ItemStack exitItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==");
        ItemUtil.mapMeta(exitItem, meta -> {
            meta.setDisplayName(LIGHT_RED + BOLD + "Close");
        });
        list.add(new MenuItem(exitItem).setType(MenuItemType.CLOSE).setSlots(49).setPriority(10));

        ItemStack nextPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
        ItemUtil.mapMeta(nextPageItem, meta -> {
            meta.setDisplayName(EditorLocales.NEXT_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(nextPageItem).setType(MenuItemType.PAGE_NEXT).setSlots(53).setPriority(10));

        ItemStack backPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
        ItemUtil.mapMeta(backPageItem, meta -> {
            meta.setDisplayName(EditorLocales.PREVIOUS_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(backPageItem).setType(MenuItemType.PAGE_PREVIOUS).setSlots(45).setPriority(10));

        return list;
    }

    @Override
    protected void loadAdditional() {
        super.loadAdditional();

        this.kitName = JOption.create("Object.Name", LIGHT_YELLOW + BOLD + KIT_NAME).read(cfg);

        this.kitSlots = new JOption<int[]>("Object.Slots",
            (cfg, path, def) -> cfg.getIntArray(path),
            () -> new int[] {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43}
        ).setWriter(JYML::setIntArray).read(cfg);

        this.potionsLore = JOption.create("Object.Potions", Lists.newArrayList(
            LIGHT_YELLOW + BOLD + "Permanent Effects:",
            LIGHT_YELLOW + "► " + LIGHT_GRAY + GENERIC_NAME + " " + LIGHT_YELLOW + GENERIC_VALUE
        )).read(cfg);

        this.limitLore = JOption.create("Object.Limit", Lists.newArrayList(
            LIGHT_YELLOW + GENERIC_CURRENT + LIGHT_GRAY + "/" + LIGHT_YELLOW + GENERIC_MAX + LIGHT_GRAY + " players."
        )).read(cfg);

        this.attributesLore = JOption.create("Object.Attributes.Info", Lists.newArrayList(
            LIGHT_YELLOW + BOLD + "Stat Modifiers:",
            LIGHT_YELLOW + "► " + LIGHT_GRAY + GENERIC_NAME + ": " + LIGHT_YELLOW + GENERIC_VALUE
        )).read(cfg);

        this.attributeBadText = JOption.create("Object.Attributes.Negative",
            LIGHT_RED + GENERIC_VALUE
        ).read(cfg);

        this.attributeGoodText = JOption.create("Object.Attributes.Positive",
            LIGHT_GREEN + "+" + GENERIC_VALUE
        ).read(cfg);

        this.statusPlayerLimitLore = JOption.create("Object.Status.PlayerLimit", Lists.newArrayList(
            LIGHT_RED + "[❗] " + LIGHT_GRAY + "There are " + LIGHT_RED + "enough" + LIGHT_GRAY + " players with this kit."
        )).read(cfg);

        this.statusNoPermLore = JOption.create("Object.Status.NoPermission", Lists.newArrayList(
            LIGHT_RED + "[❗] " + LIGHT_GRAY + "You don''t have " + LIGHT_RED + "permission" + LIGHT_GRAY + " for this kit."
        )).read(cfg);

        this.statusNoFundsLore = JOption.create("Object.Status.NotEnoughFunds", Lists.newArrayList(
            LIGHT_RED + "[❗] " + LIGHT_GRAY + "You don''t have enough " + LIGHT_RED + "funds" + LIGHT_GRAY + "."
        )).read(cfg);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @NotNull
    public abstract Predicate<Kit> getFilter(@Nullable ArenaUser user);

    @Override
    public int[] getObjectSlots() {
        return kitSlots;
    }

    @Override
    @NotNull
    public List<Kit> getObjects(@NotNull Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return Collections.emptyList();

        Arena arena = arenaPlayer.getArena();
        ArenaUser user = plugin.getUserManager().getUserData(player);
        GameplaySettings settings = arena.getConfig().getGameplaySettings();
        Predicate<Kit> filter = this.getFilter(user);

        return this.plugin.getKitManager().getKits().stream()
            .filter(kit -> settings.isKitAllowed(kit) && filter.test(kit))
            .sorted(Comparator.comparing(kit -> Colorizer.restrip(kit.getName())))
            .toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack item = kit.getIcon();

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return item;

        Arena arena = arenaPlayer.getArena();
        ArenaUser user = plugin.getUserManager().getUserData(player);

        List<String> potions = new ArrayList<>();
        if (!kit.getPotionEffects().isEmpty()) {
            for (String line : this.potionsLore) {
                if (line.contains(GENERIC_NAME)) {
                    kit.getPotionEffects().forEach(effect -> {
                        potions.add(line
                            .replace(GENERIC_NAME, LangManager.getPotionType(effect.getType()))
                            .replace(GENERIC_VALUE, NumberUtil.toRoman(effect.getAmplifier() + 1))
                        );
                    });
                }
                else potions.add(line);
            }
        }

        List<String> limit = new ArrayList<>();
        if (arena.getConfig().getGameplaySettings().getKitLimit(kit) > 0) {
            limit.addAll(this.limitLore);
            limit.replaceAll(str -> str
                .replace(GENERIC_CURRENT, NumberUtil.format(arena.countKits(kit)))
                .replace(GENERIC_MAX, NumberUtil.format(arena.getConfig().getGameplaySettings().getKitLimit(kit)))
            );
        }

        List<String> attributes = new ArrayList<>();
        if (!kit.getAttributeMap().isEmpty()) {
            for (String line : this.attributesLore) {
                if (line.contains(GENERIC_NAME)) {
                    kit.getAttributeMap().forEach((attribute, pair) -> {
                        double value = pair.getFirst();
                        boolean scalar = pair.getSecond() == AttributeModifier.Operation.ADD_SCALAR;
                        String text = value >= 0D ? this.attributeGoodText : this.attributeBadText;

                        String name = plugin.getLangManager().getEnum(attribute);
                        String formatted = NumberUtil.format(scalar ? (value * 100D) : value);
                        if (scalar) formatted += "%";

                        attributes.add(line
                            .replace(GENERIC_NAME, name)
                            .replace(GENERIC_VALUE, text.replace(GENERIC_VALUE, formatted))
                        );
                    });
                }
                else attributes.add(line);
            }
        }

        List<String> status = this.statusAvailableLore;
        if (!kit.hasPermission(player)) status = this.statusNoPermLore;
        else if (!user.hasKit(kit) && !kit.canAfford(player)) status = this.statusNoFundsLore;
        else if (user.hasKit(kit) && kit.isPlayerLimitReached(arena)) status = this.statusPlayerLimitLore;

        ItemReplacer.create(item).hideFlags().trimmed()
            .setDisplayName(this.kitName).setLore(this.kitLore)
            .replaceLoreExact(PLACEHOLDER_POTIONS, potions)
            .replaceLoreExact(PLACEHOLDER_LIMIT, limit)
            .replaceLoreExact(PLACEHOLDER_ATTRIBUTES, attributes)
            .replaceLoreExact(PLACEHOLDER_STATUS, status)
            .replace(kit.getPlaceholders())
            .replace(Colorizer::apply)
            .writeMeta();

        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Kit kit) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.isLeftClick()) {
                this.plugin.runTask(task -> {
                    player.closeInventory();
                    this.plugin.getKitManager().selectOrPurchase(player, kit);
                });
            }
            else if (event.isRightClick()) {
                kit.openPreview(player);
            }
        };
    }
}
