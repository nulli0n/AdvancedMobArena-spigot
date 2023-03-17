package su.nightexpress.ama.arena.editor.region;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Map;

public class RegionSettingsEditor extends AbstractEditorMenu<AMA, ArenaRegion> {

    public RegionSettingsEditor(@NotNull ArenaRegion region) {
        super(region.plugin(), region, ArenaEditorHub.TITLE_REGION_EDITOR, 45);

        EditorInput<ArenaRegion, ArenaEditorType> input = (player, region2, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.REGION_CHANGE_NAME) {
                region2.setName(msg);
            }
            region2.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    region.getArenaConfig().getRegionManager().getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case REGION_CHANGE_ACTIVE -> {
                        region.setActive(!region.isActive());
                        region.save();
                        this.open(player, 1);
                    }
                    case REGION_CHANGE_DEFAULT -> {
                        ArenaRegion def = region.getArenaConfig().getRegionManager().getDefaultRegion();
                        if (def != null && !this.object.equals(def)) return;

                        region.setDefault(!region.isDefault());
                        region.getArenaConfig().save();
                        this.open(player, 1);
                    }
                    case REGION_CHANGE_NAME -> {
                        EditorManager.startEdit(player, region, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NAME).getLocalized());
                        player.closeInventory();
                    }
                    case REGION_SETUP_KIT -> {
                        if (region.isActive()) {
                            plugin.getMessage(Lang.Setup_Region_Error_Enabled).send(player);
                            return;
                        }
                        player.closeInventory();
                        plugin.getArenaSetupManager().getRegionSetupManager().startSetup(player, region);
                    }
                    default -> {}
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.REGION_CHANGE_ACTIVE, 4);
        map.put(ArenaEditorType.REGION_SETUP_KIT, 22);
        map.put(ArenaEditorType.REGION_CHANGE_DEFAULT, 24);
        map.put(ArenaEditorType.REGION_CHANGE_NAME, 20);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof ArenaEditorType type2) {
            if (type2 == ArenaEditorType.REGION_CHANGE_ACTIVE) {
                item.setType(object.isActive() ? (!object.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE);
            }
            if (type2 == ArenaEditorType.REGION_CHANGE_DEFAULT) {
                item.setType(object.isDefault() ? Material.GRASS_BLOCK : Material.COAL_BLOCK);
            }
        }
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
