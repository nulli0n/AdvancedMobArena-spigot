package su.nightexpress.ama.mob.editor;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.mob.config.MobConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EditorMobMain extends AbstractEditorMenu<AMA, MobConfig> {

    private EditorMobStyles editorMobStyles;

    public EditorMobMain(@NotNull MobConfig mob) {
        super(mob.plugin(), mob, ArenaEditorHub.TITLE_MOB_EDITOR, 45);

        EditorInput<MobConfig, ArenaEditorType> input = (player, mob2, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            switch (type) {
                case MOB_CHANGE_BOSSBAR_TITLE -> mob2.setBarTitle(msg);
                case MOB_CHANGE_ATTRIBUTES_BASE, MOB_CHANGE_ATTRIBUTES_LEVEL -> {
                    String[] split = msg.split(" ");
                    if (split.length != 2) {
                        return false;
                    }

                    Attribute attribute = CollectionsUtil.getEnum(split[0], Attribute.class);
                    if (attribute == null) {
                        return false;
                    }

                    double value = StringUtil.getDouble(split[1], 0D);
                    double[] valuesHas = mob2.getAttributes().computeIfAbsent(attribute, k -> new double[2]);
                    int index = type == ArenaEditorType.MOB_CHANGE_ATTRIBUTES_BASE ? 0 : 1;
                    valuesHas[index] = value;
                    mob2.getAttributes().put(attribute, valuesHas);
                }
                case MOB_CHANGE_ENTITY_TYPE -> {
                    EntityType entityType = CollectionsUtil.getEnum(msg, EntityType.class);
                    if (entityType == null || !entityType.isSpawnable() || !entityType.isAlive()) {
                        return false;
                    }
                    mob2.setEntityType(entityType);
                }
                case MOB_CHANGE_NAME -> mob2.setName(msg);
                case MOB_CHANGE_LEVEL_MIN -> mob2.setLevelMin(StringUtil.getInteger(msg, 1));
                case MOB_CHANGE_LEVEL_MAX -> mob2.setLevelMax(StringUtil.getInteger(msg, 1));
            }

            mob2.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    plugin.getEditor().getMobEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case MOB_CHANGE_NAME -> {
                        if (e.isRightClick()) {
                            mob.setNameVisible(!mob.isNameVisible());
                            break;
                        }
                        EditorManager.startEdit(player, mob, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Mob_Enter_Name).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case MOB_CHANGE_ENTITY_TYPE -> {
                        EditorManager.startEdit(player, mob, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Mob_Enter_Type).getLocalized());
                        EditorManager.suggestValues(player, Stream.of(EntityType.values())
                            .filter(EntityType::isSpawnable).filter(EntityType::isAlive).map(Enum::name).toList(), true);
                        player.closeInventory();
                        return;
                    }
                    case MOB_CHANGE_LEVEL -> {
                        if (e.isLeftClick()) type2 = ArenaEditorType.MOB_CHANGE_LEVEL_MIN;
                        else type2 = ArenaEditorType.MOB_CHANGE_LEVEL_MAX;

                        EditorManager.startEdit(player, mob, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Mob_Enter_Create).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case MOB_CHANGE_BOSSBAR -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                mob.setBarColor(CollectionsUtil.next(mob.getBarColor()));
                            }
                            else if (e.isRightClick()) {
                                mob.setBarStyle(CollectionsUtil.next(mob.getBarStyle()));
                            }
                        }
                        else {
                            if (e.isLeftClick()) {
                                mob.setBarEnabled(!mob.isBarEnabled());
                            }
                            else if (e.isRightClick()) {
                                EditorManager.startEdit(player, mob, ArenaEditorType.MOB_CHANGE_BOSSBAR_TITLE, input);
                                EditorManager.tip(player, plugin.getMessage(Lang.Editor_Mob_Enter_Create).getLocalized());
                                player.closeInventory();
                                return;
                            }
                        }
                    }
                    case MOB_CHANGE_ATTRIBUTES -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                mob.getAttributes().clear();
                            }
                            break;
                        }

                        if (e.isLeftClick()) type2 = ArenaEditorType.MOB_CHANGE_ATTRIBUTES_BASE;
                        else type2 = ArenaEditorType.MOB_CHANGE_ATTRIBUTES_LEVEL;

                        EditorManager.startEdit(player, mob, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Mob_Enter_Attribute).getLocalized());
                        EditorManager.suggestValues(player, Stream.of(Attribute.values()).map(Enum::name).toList(), false);
                        player.closeInventory();
                        return;
                    }
                    case MOB_CHANGE_EQUIPMENT -> {
                        new EquipmentEditor(mob).open(player, 1);
                        return;
                    }
                    case MOB_CHANGE_STYLES -> {
                        this.getEditorMobStyles().open(player, 1);
                        return;
                    }
                }
                mob.save();
                this.open(player, 1);
            }
        };

        this.loadItems(click);
    }

    @NotNull
    public EditorMobStyles getEditorMobStyles() {
        if (this.editorMobStyles == null) {
            this.editorMobStyles = new EditorMobStyles(plugin, this.object);
        }
        return editorMobStyles;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.MOB_CHANGE_NAME, 10);
        map.put(ArenaEditorType.MOB_CHANGE_ENTITY_TYPE, 11);
        map.put(ArenaEditorType.MOB_CHANGE_LEVEL, 12);
        map.put(ArenaEditorType.MOB_CHANGE_BOSSBAR, 13);
        map.put(ArenaEditorType.MOB_CHANGE_ATTRIBUTES, 14);
        map.put(ArenaEditorType.MOB_CHANGE_EQUIPMENT, 15);
        map.put(ArenaEditorType.MOB_CHANGE_STYLES, 16);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }

    static class EquipmentEditor extends AbstractMenu<AMA> {

        private final MobConfig                   mob;
        private final Map<EquipmentSlot, Integer> equipmentSlots;

        public EquipmentEditor(@NotNull MobConfig mob) {
            super(mob.plugin(), ArenaEditorHub.TITLE_MOB_EDITOR, 9);
            this.mob = mob;
            this.equipmentSlots = new HashMap<>();
            this.equipmentSlots.put(EquipmentSlot.FEET, 0);
            this.equipmentSlots.put(EquipmentSlot.LEGS, 1);
            this.equipmentSlots.put(EquipmentSlot.CHEST, 2);
            this.equipmentSlots.put(EquipmentSlot.HEAD, 3);
            this.equipmentSlots.put(EquipmentSlot.HAND, 4);
            this.equipmentSlots.put(EquipmentSlot.OFF_HAND, 5);
        }

        @Nullable
        private EquipmentSlot getTypeBySlot(int slot) {
            return this.equipmentSlots.entrySet().stream().filter(entry -> entry.getValue() == slot).findFirst()
                .map(Map.Entry::getKey).orElse(null);
        }

        private void saveEquipment(@NotNull Player player, @NotNull Inventory inventory) {
            this.equipmentSlots.forEach((equipmentSlot, slot) -> {
                this.mob.setEquipment(equipmentSlot, inventory.getItem(slot));
            });
            this.mob.save();
        }

        @Override
        public boolean onReady(@NotNull Player player, @NotNull Inventory inventory) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                inventory.setItem(this.equipmentSlots.getOrDefault(equipmentSlot, 0), this.mob.getEquipment(equipmentSlot));
            }
            return true;
        }

        @Override
        public boolean destroyWhenNoViewers() {
            return true;
        }

        @Override
        public boolean cancelClick(@NotNull InventoryDragEvent e) {
            super.cancelClick(e);
            return e.getRawSlots().stream().anyMatch(slot -> this.getTypeBySlot(slot) == null);
        }

        @Override
        public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
            return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER && this.getTypeBySlot(e.getRawSlot()) == null;
        }

        @Override
        public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
            this.saveEquipment(player, e.getInventory());
            super.onClose(player, e);

            plugin.runTask(c -> this.mob.getEditor().open(player, 1), false);
        }
    }
}
