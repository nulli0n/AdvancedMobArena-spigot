package su.nightexpress.ama.mob.editor;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.config.MobConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorMobList extends AbstractEditorMenuAuto<AMA, MobManager, MobConfig> {

    public EditorMobList(@NotNull MobManager mobManager) {
        super(mobManager.plugin(), mobManager, ArenaEditorHub.TITLE_MOB_EDITOR, 45);

        EditorInput<MobManager, ArenaEditorType> input = (player, mobManager2, type, e) -> {
            String msg = StringUtil.colorOff(e.getMessage());
            if (type == ArenaEditorType.MOB_CREATE) {
                String id = EditorManager.fineId(msg);
                if (mobManager2.getMobById(id) != null) {
                    EditorManager.error(player, plugin.getMessage(Lang.Editor_Mob_Error_Exist).getLocalized());
                    return false;
                }

                MobConfig mob = new MobConfig(plugin, plugin.getDataFolder() + "/mobs/" + id + ".yml", EntityType.ZOMBIE);
                mob.save();
                mobManager2.getMobsMap().put(mob.getId(), mob);
                return true;
            }
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    plugin.getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.MOB_CREATE) {
                    EditorManager.startEdit(player, mobManager, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.Editor_Mob_Enter_Create).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.MOB_CREATE, 41);
        map.put(MenuItemType.RETURN, 39);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<MobConfig> getObjects(@NotNull Player player) {
        return new ArrayList<>(plugin.getMobManager().getMobs().stream().sorted(Comparator.comparing(MobConfig::getId)).toList());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull MobConfig mob) {
        Material material = Material.getMaterial(mob.getEntityType().name() + "_SPAWN_EGG");
        if (mob.getEntityType() == EntityType.MUSHROOM_COW) material = Material.MOOSHROOM_SPAWN_EGG;
        if (material == null) material = Material.BAT_SPAWN_EGG;

        ItemStack item = ArenaEditorType.MOB_OBJECT.getItem();
        item.setType(material);

        ItemUtil.replace(item, mob.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull MobConfig mob) {
        return (player1, type, e) -> {
            if (e.isShiftClick()) {
                if (mob.getFile().delete()) {
                    mob.clear();
                    this.parent.getMobsMap().remove(mob.getId());
                    this.open(player, this.getPage(player));
                }
                return;
            }
            mob.getEditor().open(player, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
