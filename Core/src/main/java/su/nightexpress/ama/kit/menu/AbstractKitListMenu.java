package su.nightexpress.ama.kit.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.kit.Kit;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractKitListMenu extends ConfigMenu<AMA> implements AutoPaged<Kit> {

    private final String       objectName;
    private final List<String> objectLore;
    private final int[]        objectSlots;

    public AbstractKitListMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        this.objectName = Colorizer.apply(cfg.getString("Object.Name", Placeholders.KIT_NAME));
        this.objectLore = Colorizer.apply(cfg.getStringList("Object.Lore"));
        this.objectSlots = cfg.getIntArray("Object.Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();
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
        return objectSlots;
    }

    @Override
    @NotNull
    public List<Kit> getObjects(@NotNull Player player) {
        ArenaUser user = plugin.getUserManager().getUserData(player);
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return Collections.emptyList();

        Predicate<Kit> isValidMenu = this.getFilter(user);
        Predicate<Kit> isAllowed = kit -> arenaPlayer.getArena().getConfig().getGameplayManager().isKitAllowed(kit.getId());

        return plugin.getKitManager().getKits().stream().filter(kit -> isValidMenu.and(isAllowed).test(kit)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
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
}
