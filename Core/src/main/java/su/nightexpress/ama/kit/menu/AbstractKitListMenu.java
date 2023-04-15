package su.nightexpress.ama.kit.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.kit.Kit;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractKitListMenu extends AbstractMenuAuto<AMA, Kit> {

    private final String       objectName;
    private final List<String> objectLore;
    private final int[]        objectSlots;

    public AbstractKitListMenu(@NotNull AMA plugin, @NotNull JYML cfg, @NotNull String path) {
        super(plugin, cfg, path);

        this.objectName = Colorizer.apply(cfg.getString(path + "Object.Name", Placeholders.KIT_NAME));
        this.objectLore = Colorizer.apply(cfg.getStringList(path + "Object.Lore"));
        this.objectSlots = cfg.getIntArray(path + "Object.Slots");

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
        };

        for (String sId : cfg.getSection(path + "Content")) {
            MenuItem guiItem = cfg.getMenuItem(path + "Content." + sId, MenuItemType.class);

            if (guiItem.getType() != null) {
                guiItem.setClickHandler(click);
            }
            this.addItem(guiItem);
        }
    }

    @NotNull
    public abstract Predicate<Kit> getFilter(@Nullable ArenaUser user);

    @Override
    public int[] getObjectSlots() {
        return objectSlots;
    }

    @Override
    @NotNull
    protected List<Kit> getObjects(@NotNull Player player) {
        ArenaUser user = plugin.getUserManager().getUserData(player);
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return Collections.emptyList();

        Predicate<Kit> isValidMenu = this.getFilter(user);
        Predicate<Kit> isAllowed = kit -> arenaPlayer.getArena().getConfig().getGameplayManager().isKitAllowed(kit.getId());

        return plugin.getKitManager().getKits().stream().filter(kit -> isValidMenu.and(isAllowed).test(kit)).toList();
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack item = new ItemStack(kit.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return item;

        meta.setDisplayName(this.objectName);
        meta.setLore(this.objectLore);
        item.setItemMeta(meta);
        ItemUtil.replace(item, str -> kit.replacePlaceholders().apply(str
            .replace(Placeholders.KIT_IS_AVAILABLE, LangManager.getBoolean(kit.isAvailable(arenaPlayer, false)))
        ));

        return item;
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
