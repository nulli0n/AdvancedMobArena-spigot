package su.nightexpress.ama.arena.editor.region;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorRegionMain extends AbstractEditorMenu<AMA, ArenaRegion> {

    private EditorRegionWaveList      waveList;
    private EditorRegionContainerList containerList;

    public EditorRegionMain(@NotNull ArenaRegion region) {
        super(region.plugin(), region, ArenaEditorUtils.TITLE_REGION_EDITOR, 45);

        EditorInput<ArenaRegion, ArenaEditorType> input = (player, region2, type, e) -> {
            String msg = StringUtil.color(e.getMessage());

            switch (type) {
                case REGION_CHANGE_LINKED -> {
                    ArenaRegion regionLink = region2.getArenaConfig().getRegionManager().getRegion(msg);
                    if (regionLink == null) return false;

                    region2.addLinkedRegion(regionLink);
                }
                case REGION_CHANGE_NAME -> region2.setName(msg);
            }
            region2.save();
            return true;
        };

        IMenuClick click = (player, type, e) -> {
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
                        ArenaRegion def = region.getArenaConfig().getRegionManager().getRegionDefault();
                        if (def != null && !this.object.equals(def)) return;

                        region.setDefault(!region.isDefault());
                        region.getArenaConfig().save();
                        this.open(player, 1);
                    }
                    case REGION_CHANGE_NAME -> {
                        EditorManager.startEdit(player, region, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Region_Enter_Name).getLocalized());
                        player.closeInventory();
                    }
                    case REGION_CHANGE_LINKED -> {
                        if (e.isRightClick()) {
                            region.getLinkedRegions().clear();
                            region.save();
                            this.open(player, 1);
                            return;
                        }
                        EditorManager.startEdit(player, region, type2, input);
                        EditorManager.suggestValues(player, region.getArenaConfig().getRegionManager().getRegions().stream().map(ArenaRegion::getId).toList(), true);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Region_Enter_Id).getLocalized());
                        player.closeInventory();
                    }
                    case REGION_CHANGE_TRIGGERS_LOCKED, REGION_CHANGE_TRIGGERS_UNLOCKED -> {
                        ArenaEditorUtils.handleTriggersClick(player, region, type2, e.isRightClick());
                        if (e.isRightClick()) {
                            region.save();
                            this.open(player, 1);
                        }
                    }
                    case REGION_SETUP_KIT -> {
                        if (region.isActive()) {
                            plugin.getMessage(Lang.Setup_Region_Error_Enabled).send(player);
                            return;
                        }
                        player.closeInventory();
                        plugin.getArenaSetupManager().getRegionSetupManager().startSetup(player, region);
                    }
                    case REGION_OPEN_WAVES -> this.getWaveList().open(player, 1);
                    case REGION_OPEN_CONTAINERS -> this.getContainerList().open(player, 1);
                    default -> {}
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void clear() {
        if (this.waveList != null) {
            this.waveList.clear();
            this.waveList = null;
        }
        if (this.containerList != null) {
            this.containerList.clear();
            this.containerList = null;
        }
        super.clear();
    }

    @NotNull
    public EditorRegionWaveList getWaveList() {
        if (this.waveList == null) {
            this.waveList = new EditorRegionWaveList(this.object);
        }
        return this.waveList;
    }

    @NotNull
    public EditorRegionContainerList getContainerList() {
        if (this.containerList == null) {
            this.containerList = new EditorRegionContainerList(this.object);
        }
        return containerList;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.REGION_CHANGE_ACTIVE, 4);
        map.put(ArenaEditorType.REGION_SETUP_KIT, 13);
        map.put(ArenaEditorType.REGION_OPEN_WAVES, 23);
        map.put(ArenaEditorType.REGION_OPEN_CONTAINERS, 25);
        map.put(ArenaEditorType.REGION_CHANGE_DEFAULT, 21);
        map.put(ArenaEditorType.REGION_CHANGE_LINKED, 22);
        map.put(ArenaEditorType.REGION_CHANGE_NAME, 19);
        map.put(ArenaEditorType.REGION_CHANGE_TRIGGERS_LOCKED, 2);
        map.put(ArenaEditorType.REGION_CHANGE_TRIGGERS_UNLOCKED, 6);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof ArenaEditorType type2) {
            if (type2 == ArenaEditorType.REGION_CHANGE_ACTIVE) {
                item.setType(object.isActive() ? (!object.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE);
            }
            if (type2 == ArenaEditorType.REGION_CHANGE_DEFAULT) {
                item.setType(object.isDefault() ? Material.GRASS_BLOCK : Material.PODZOL);
            }
        }
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
