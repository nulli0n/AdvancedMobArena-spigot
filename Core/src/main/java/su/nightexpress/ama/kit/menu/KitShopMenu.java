package su.nightexpress.ama.kit.menu;

import com.google.common.collect.Lists;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.List;
import java.util.function.Predicate;

import static su.nexmedia.engine.utils.Colors2.*;

public class KitShopMenu extends AbstractKitListMenu {

    public static final String FILE_NAME = "kit_shop.yml";

    public KitShopMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
    }

    @NotNull
    @Override
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BOLD + "Kit Shop", 54, InventoryType.CHEST);
    }

    @NotNull
    @Override
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = super.createDefaultItems();

        ItemStack mykitsItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3MzUxNWFhOWI2MDUzN2QwYTQ2ZmE2NjkxMmY5OTk4NjViYjk1Y2ViMDJkN2M1ZTI3MjVkNDE0ZGRiNDlkIn19fQ==");
        ItemUtil.mapMeta(mykitsItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW + BOLD + "My Kits");
            meta.setLore(Lists.newArrayList(
                LIGHT_GRAY + "Click to browse all your kits."
            ));
        });
        list.add(new MenuItem(mykitsItem).setType(ButtonType.KIT_SELECTOR).setSlots(4).setPriority(10));

        return list;
    }

    @Override
    protected void loadAdditional() {
        super.loadAdditional();

        this.kitLore = JOption.create("Object.Lore", Lists.newArrayList(
            LIGHT_YELLOW + Placeholders.KIT_COST,
            "",
            Placeholders.KIT_DESCRIPTION,
            "",
            PLACEHOLDER_ATTRIBUTES,
            "",
            PLACEHOLDER_POTIONS,
            "",
            PLACEHOLDER_STATUS,
            LIGHT_YELLOW + "[▶] " + LIGHT_GRAY + "Right-Click to " + LIGHT_YELLOW + "preview" + LIGHT_GRAY + "."
        )).read(cfg);

        this.statusAvailableLore = JOption.create("Object.Status.Available", Lists.newArrayList(
            LIGHT_GREEN + "[▶] " + LIGHT_GRAY + "Left-Click to " + LIGHT_GREEN + "purchase" + LIGHT_GRAY + "."
        )).read(cfg);
    }

    @Override
    @NotNull
    public Predicate<Kit> getFilter(@Nullable ArenaUser user) {
        return kit -> user != null && !user.hasKit(kit.getId());
    }
}
