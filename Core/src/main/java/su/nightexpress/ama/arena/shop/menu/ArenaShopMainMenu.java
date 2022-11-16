package su.nightexpress.ama.arena.shop.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.arena.ArenaPlayer;
import su.nightexpress.ama.arena.shop.ArenaShopCategory;
import su.nightexpress.ama.arena.shop.ArenaShopManager;
import su.nightexpress.ama.config.Lang;

import java.util.*;

public class ArenaShopMainMenu extends AbstractMenuAuto<AMA, ArenaShopCategory> {

    protected ArenaShopManager shopManager;

    private final String                            objectName;
    private final Map<ArenaLockState, List<String>> objectLore;
    private final int[]                             objectSlots;

    public ArenaShopMainMenu(@NotNull ArenaShopManager shopManager) {
        super(shopManager.plugin(), JYML.loadOrExtract(shopManager.plugin(), "/menu/arena.shop.main.yml"), "");
        this.shopManager = shopManager;

        this.objectName = StringUtil.color(cfg.getString("Category.Name", Placeholders.SHOP_CATEGORY_NAME));
        this.objectSlots = cfg.getIntArray("Category.Slots");
        this.objectLore = new HashMap<>();
        for (ArenaLockState lockState : ArenaLockState.values()) {
            this.objectLore.put(lockState, StringUtil.color(cfg.getStringList("Category.Lore." + lockState.name())));
        }

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
        };

        for (String sId : cfg.getSection("Content")) {
            IMenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    public int[] getObjectSlots() {
        return objectSlots;
    }

    @Override
    @NotNull
    protected List<ArenaShopCategory> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.shopManager.getCategories());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaShopCategory shopCategory) {
        ItemStack item = shopCategory.getIcon();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setLore(this.objectLore.getOrDefault(shopCategory.getState(), Collections.emptyList()));
        meta.setDisplayName(this.objectName);
        item.setItemMeta(meta);
        ItemUtil.replace(item, shopCategory.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull ArenaShopCategory shopCategory) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null || arenaPlayer.getArena().getState() != ArenaState.INGAME) return (p, type, e) -> {
        };

        return (player2, type, e) -> {
            if (shopCategory.getState() == ArenaLockState.LOCKED) {
                plugin.getMessage(Lang.Shop_Buy_Error_Locked).send(player2);
                return;
            }
            if (!shopCategory.isAvailable(arenaPlayer)) {
                plugin.getMessage(Lang.Shop_Buy_Error_BadKit).send(player2);
                return;
            }

            shopCategory.open(arenaPlayer);
        };
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
