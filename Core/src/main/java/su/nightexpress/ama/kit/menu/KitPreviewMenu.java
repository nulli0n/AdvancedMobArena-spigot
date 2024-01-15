package su.nightexpress.ama.kit.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.api.menu.link.Linked;
import su.nexmedia.engine.api.menu.link.ViewLink;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.kit.impl.Kit;

public class KitPreviewMenu extends ConfigMenu<AMA> implements Linked<Kit> {

    public static final String FILE_NAME = "kit_preview.yml";

    private final ViewLink<Kit> link;

    private final int[] itemSlots;
    private final int[] armorSlots;

    public KitPreviewMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.link = new ViewLink<>();
        this.itemSlots = cfg.getIntArray("Item_Slots");
        this.armorSlots = cfg.getIntArray("Armor_Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, ClickHandler.forClose(this))
            .addClick(MenuItemType.RETURN, (viewer, event) -> this.handleReturn(viewer));

        this.load();
    }

    @NotNull
    @Override
    public ViewLink<Kit> getLink() {
        return link;
    }

    private void handleReturn(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        ArenaUser user = plugin.getUserManager().getUserData(player);
        Kit kit = this.getLink().get(player);
        if (user.hasKit(kit)) {
            plugin.getKitManager().getSelectMenu().openNextTick(player, 1);
        }
        else {
            plugin.getKitManager().getShopMenu().openNextTick(player, 1);
        }
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);

        Player player = viewer.getPlayer();
        Kit kit = this.getLink().get(player);

        options.setTitle(kit.replacePlaceholders().apply(options.getTitle()));

        ItemStack[] items = kit.getItems();
        for (int itemIndex = 0; itemIndex < Kit.INVENTORY_SIZE; itemIndex++) {
            if (itemIndex >= itemSlots.length) break;

            ItemStack item = items[itemIndex];
            if (item == null) continue;

            this.addWeakItem(player, item, itemSlots[itemIndex]);
        }

        int armorIndex = 0;
        for (EquipmentSlot slot : Kit.EQUIPMENT_SLOTS) {
            if (armorIndex >= armorSlots.length) break;

            this.addWeakItem(player, kit.getEquipment(slot), armorSlots[armorIndex++]);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        this.handleReturn(viewer);
        super.onClose(viewer, event);
    }
}
