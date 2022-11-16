package su.nightexpress.ama.arena.editor.region;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionContainer;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorRegionContainerList extends AbstractEditorMenuAuto<AMA, ArenaRegion, ArenaRegionContainer> {

    public EditorRegionContainerList(@NotNull ArenaRegion region) {
        super(region.plugin(), region, ArenaEditorUtils.TITLE_REGION_EDITOR, 45);

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    region.getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<ArenaRegionContainer> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getContainers());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaRegionContainer container) {
        ItemStack item = ArenaEditorType.REGION_CONTAINER_OBJECT.getItem();
        ItemUtil.replace(item, container.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull ArenaRegionContainer container) {
        return (player2, type, e) -> {
            if (e.isShiftClick()) {
                if (e.isRightClick()) {
                    if (!this.parent.getContainers().remove(container)) return;

                    container.clear();
                    this.parent.save();
                    this.open(player2, this.getPage(player2));
                    return;
                }
                return;
            }
            if (e.isRightClick()) {
                player2.teleport(container.getLocation());
                return;
            }
            container.getEditor().open(player2, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
