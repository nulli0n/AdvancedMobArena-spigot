package su.nightexpress.ama.kit.menu;

import com.google.common.collect.Lists;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nexmedia.engine.api.menu.link.Linked;
import su.nexmedia.engine.api.menu.link.ViewLink;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.ArrayList;
import java.util.List;

import static su.nexmedia.engine.utils.Colors2.*;
import static su.nightexpress.ama.Placeholders.*;

public class KitConfirmMenu extends ConfigMenu<AMA> implements Linked<Kit> {

    public static final String FILE_NAME = "kit_confirm.yml";

    private final ViewLink<Kit> link;

    private String kitName;
    private List<String> kitLore;
    private int kitSlot;

    public KitConfirmMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.link = new ViewLink<>();

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CONFIRMATION_ACCEPT, (viewer, event) -> {
                Kit kit = this.getLink().get(viewer);
                this.plugin.getKitManager().purchase(viewer.getPlayer(), kit);
                this.plugin.runTask(task -> viewer.getPlayer().closeInventory());
            })
            .addClick(MenuItemType.CONFIRMATION_DECLINE, (viewer, event) -> {
                this.plugin.getKitManager().getShopMenu().openNextTick(viewer, 1);
            });

        this.load();

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            Kit kit = this.getLink().get(viewer);
            ItemReplacer.create(item).readMeta().replace(GENERIC_PRICE, kit.getCurrency().format(kit.getCost())).writeMeta();
        }));
    }

    @NotNull
    @Override
    public ViewLink<Kit> getLink() {
        return link;
    }

    @Override
    public boolean isCodeCreation() {
        return true;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);

        Kit kit = this.getLink().get(viewer);

        options.setTitle(kit.replacePlaceholders().apply(options.getTitle()));

        ItemStack icon = kit.getIcon();
        ItemReplacer.create(icon).hideFlags().trimmed()
            .setDisplayName(this.kitName)
            .setLore(this.kitLore)
            .replace(kit.replacePlaceholders())
            .replace(Colorizer::apply)
            .writeMeta();
        this.addWeakItem(viewer.getPlayer(), icon, this.kitSlot);
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions("Purchase " + KIT_NAME + " kit?", 9, InventoryType.CHEST);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack cancelItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==");
        ItemUtil.mapMeta(cancelItem, meta -> {
            meta.setDisplayName(LIGHT_RED + BOLD + "Cancel");
        });
        list.add(new MenuItem(cancelItem).setSlots(0).setPriority(5).setType(MenuItemType.CONFIRMATION_DECLINE));

        ItemStack acceptItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=");
        ItemUtil.mapMeta(acceptItem, meta -> {
            meta.setDisplayName(LIGHT_GREEN + BOLD + "Accept");
            meta.setLore(Lists.newArrayList(
                LIGHT_GREEN + "► " + LIGHT_GRAY + "Price: " + LIGHT_GREEN + GENERIC_PRICE
            ));
        });
        list.add(new MenuItem(acceptItem).setSlots(8).setPriority(5).setType(MenuItemType.CONFIRMATION_ACCEPT));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.kitName = JOption.create("Kit.Name", LIGHT_YELLOW + BOLD + KIT_NAME).read(cfg);
        this.kitLore = JOption.create("Kit.Lore", Lists.newArrayList(Placeholders.KIT_DESCRIPTION)).read(cfg);
        this.kitSlot = JOption.create("Kit.Slot", 4).read(cfg);
    }
}
