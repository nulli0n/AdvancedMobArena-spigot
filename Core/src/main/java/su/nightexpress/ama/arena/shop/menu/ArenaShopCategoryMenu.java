package su.nightexpress.ama.arena.shop.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.shop.ArenaShopCategory;
import su.nightexpress.ama.arena.shop.ArenaShopProduct;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.stats.object.StatType;

import java.util.*;

public class ArenaShopCategoryMenu extends AbstractMenuAuto<AMA, ArenaShopProduct> {

    protected ArenaShopCategory shopCategory;

    private final String                            objectName;
    private final Map<ArenaLockState, List<String>> objectLore;
    private final int[]                             objectSlots;

    public ArenaShopCategoryMenu(@NotNull ArenaShopCategory shopCategory) {
        super(shopCategory.plugin(), JYML.loadOrExtract(shopCategory.plugin(), "/menu/arena.shop.category.yml"), "");
        this.shopCategory = shopCategory;

        this.objectName = StringUtil.color(cfg.getString("Product.Name", Placeholders.SHOP_PRODUCT_NAME));
        this.objectSlots = cfg.getIntArray("Product.Slots");
        this.objectLore = new HashMap<>();
        for (ArenaLockState lockState : ArenaLockState.values()) {
            this.objectLore.put(lockState, StringUtil.color(cfg.getStringList("Product.Lore." + lockState.name())));
        }

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
        };

        for (String sId : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
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
    protected List<ArenaShopProduct> getObjects(@NotNull Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null || arenaPlayer.getArena().getState() != GameState.INGAME) return Collections.emptyList();

        List<ArenaShopProduct> items = new ArrayList<>(this.shopCategory.getProducts());
        if (shopCategory.getShopManager().isHideOtherKitProducts()) {
            items.removeIf(item -> !item.isAvailable(arenaPlayer));
        }
        return items;
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaShopProduct shopProduct) {
        ItemStack item = shopProduct.getIcon();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setLore(this.objectLore.getOrDefault(shopProduct.getState(), Collections.emptyList()));
        meta.setDisplayName(this.objectName);
        item.setItemMeta(meta);
        ItemUtil.replace(item, shopProduct.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ArenaShopProduct shopProduct) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null || arenaPlayer.getArena().getState() != GameState.INGAME) return (p, type, e) -> {
        };

        return (player2, type, e) -> {
            if (shopProduct.getState() == ArenaLockState.LOCKED) {
                plugin.getMessage(Lang.Shop_Buy_Error_Locked).send(player2);
                return;
            }
            if (!shopProduct.isAvailable(arenaPlayer)) {
                plugin.getMessage(Lang.Shop_Buy_Error_BadKit).send(player2);
                return;
            }


            double price = shopProduct.getPrice();
            double balance = shopProduct.getCurrency().getBalance(player2);
            if (balance < price) {
                plugin.getMessage(Lang.Shop_Buy_Error_NoMoney).replace(shopProduct.replacePlaceholders()).send(player2);
                return;
            }

            shopProduct.getCurrency().take(player2, price);
            arenaPlayer.addStats(StatType.COINS_SPENT, (int) price);
            // TODO Item Option for single buy
            shopProduct.give(player2);

            plugin.getMessage(Lang.Shop_Buy_Success).replace(shopProduct.replacePlaceholders()).send(player2);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
