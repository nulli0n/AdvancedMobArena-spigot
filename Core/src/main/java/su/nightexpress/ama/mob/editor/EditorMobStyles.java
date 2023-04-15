package su.nightexpress.ama.mob.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.mob.config.MobConfig;
import su.nightexpress.ama.mob.style.MobStyleType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EditorMobStyles extends AbstractEditorMenuAuto<AMA, MobConfig, MobStyleType> {

    public EditorMobStyles(@NotNull AMA plugin, @NotNull MobConfig customMob) {
        super(plugin, customMob, ArenaEditorHub.TITLE_MOB_EDITOR, 45);

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    this.parent.getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(MenuItemType.RETURN, 40);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
    }

    @Override
    protected int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<MobStyleType> getObjects(@NotNull Player player) {
        return new ArrayList<>(Arrays.asList(MobStyleType.get(this.parent.getEntityType())));
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull MobStyleType styleType) {
        ItemStack item = ArenaEditorType.MOB_STYLE_OBJECT.getItem();
        ItemUtil.replace(item, str -> str
            .replace(Placeholders.MOB_STYLE_TYPE, plugin.getLangManager().getEnum(styleType))
            .replace(Placeholders.MOB_STYLE_VALUE, this.parent.getStyle(styleType))
        );
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull MobStyleType styleType) {
        EditorInput<MobConfig, MobStyleType> input = (player2, mob, type, e) -> {
            String msg = Colorizer.strip(e.getMessage());
            mob.addStyle(type, msg);
            mob.save();
            return true;
        };

        return ((player2, type, e) -> {
            if (e.isLeftClick()) {
                EditorManager.startEdit(player2, this.parent, styleType, input);
                EditorManager.prompt(player2, plugin.getMessage(Lang.EDITOR_MOB_ENTER_STYLE).getLocalized());
                EditorManager.suggestValues(player2, Stream.of(styleType.getWrapper().getWriter().values()).map(String::valueOf).toList(), true);
                player2.closeInventory();
            }
            else if (e.isRightClick()) {
                this.parent.removeStyle(styleType);
                this.open(player2, this.getPage(player2));
            }
        });
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
