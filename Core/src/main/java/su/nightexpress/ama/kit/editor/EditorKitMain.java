package su.nightexpress.ama.kit.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.kit.Kit;

import java.util.Map;
import java.util.stream.Stream;

public class EditorKitMain extends AbstractEditorMenu<AMA, Kit> {

    public EditorKitMain(@NotNull Kit kit) {
        super(kit.plugin(), kit, ArenaEditorHub.TITLE_KIT_EDITOR, 45);

        EditorInput<Kit, ArenaEditorType> input = (player, kit2, type, e) -> {
            String msg = Colorizer.apply(e.getMessage());
            switch (type) {
                case KIT_CHANGE_NAME -> kit2.setName(msg);
                case KIT_CHANGE_DESCRIPTION -> kit2.getDescription().add(msg);
                case KIT_CHANGE_COMMANDS -> kit2.getCommands().add(Colorizer.strip(msg));
                case KIT_CHANGE_POTIONS -> {
                    String[] split = msg.split(":");
                    PotionEffectType effectType = PotionEffectType.getByName(split[0].toUpperCase());
                    if (effectType == null) {
                        EditorManager.error(player, "&cInvalid Effect!");
                        return false;
                    }
                    int amp = split.length >= 2 ? StringUtil.getInteger(split[1], 1) : 1;
                    PotionEffect potionEffect = new PotionEffect(effectType, Integer.MAX_VALUE, Math.max(0, amp - 1));
                    kit2.getPotionEffects().add(potionEffect);
                }
                case KIT_CHANGE_CURRENCY -> {
                    String curId = Colorizer.strip(msg);
                    ICurrency currency = plugin.getCurrencyManager().getCurrency(curId);
                    if (currency == null) {
                        EditorManager.error(player, plugin.getMessage(Lang.ERROR_CURRENCY_INVALID).getLocalized());
                        return false;
                    }
                    kit2.setCurrency(currency);
                }
                case KIT_CHANGE_COST -> {
                    int cost = StringUtil.getInteger(msg, -999);
                    if (cost == -999) {
                        EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ERROR_NUMBER_GENERIC).getLocalized());
                        return false;
                    }
                    kit2.setCost(cost);
                }
                default -> {return true;}
            }

            kit2.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    plugin.getEditor().getKitEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case KIT_CHANGE_NAME -> {
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.Editor_Kit_Enter_Name).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_DESCRIPTION -> {
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_DESCRIPTION).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_ICON -> {
                        if (e.getClick() == ClickType.MIDDLE) {
                            PlayerUtil.addItem(player, kit.getIcon());
                            return;
                        }
                        ItemStack cursor = e.getCursor();
                        if (cursor == null || cursor.getType().isAir()) return;

                        kit.setIcon(cursor);
                        e.getView().setCursor(null);
                    }
                    case KIT_CHANGE_COMMANDS -> {
                        if (e.isLeftClick()) {
                            EditorManager.startEdit(player, kit, type2, input);
                            EditorManager.prompt(player, plugin.getMessage(Lang.Editor_Kit_Enter_Command).getLocalized());
                            EditorManager.sendCommandTips(player);
                            player.closeInventory();
                            return;
                        }
                        else if (e.isRightClick()) {
                            kit.getCommands().clear();
                        }
                    }
                    case KIT_CHANGE_POTIONS -> {
                        if (e.isLeftClick()) {
                            EditorManager.startEdit(player, kit, type2, input);
                            EditorManager.prompt(player, plugin.getMessage(Lang.Editor_Kit_Enter_Effect).getLocalized());
                            EditorManager.suggestValues(player, Stream.of(PotionEffectType.values()).map(PotionEffectType::getName).toList(), false);
                            player.closeInventory();
                            return;
                        }
                        else if (e.isRightClick()) {
                            kit.getPotionEffects().clear();
                        }
                    }
                    case KIT_CHANGE_ARMOR -> {
                        new ContentEditor(kit, 9).open(player, 1);
                        return;
                    }
                    case KIT_CHANGE_INVENTORY -> {
                        new ContentEditor(kit, 27).open(player, 1);
                        return;
                    }
                    case KIT_CHANGE_PERMISSION -> kit.setPermissionRequired(!kit.isPermissionRequired());
                    case KIT_CHANGE_DEFAULT -> kit.setDefault(!kit.isDefault());
                    case KIT_CHANGE_CURRENCY -> {
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.suggestValues(player, plugin.getCurrencyManager().getCurrencyIds(), true);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_CURRENCY).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_COST -> {
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.Editor_Kit_Enter_Cost).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    default -> {return;}
                }
                this.object.save();
                this.open(player, 1);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.KIT_CHANGE_COST, 2);
        map.put(ArenaEditorType.KIT_CHANGE_DEFAULT, 4);
        map.put(ArenaEditorType.KIT_CHANGE_CURRENCY, 6);

        map.put(ArenaEditorType.KIT_CHANGE_NAME, 11);
        map.put(ArenaEditorType.KIT_CHANGE_DESCRIPTION, 12);
        map.put(ArenaEditorType.KIT_CHANGE_ICON, 14);
        map.put(ArenaEditorType.KIT_CHANGE_PERMISSION, 15);

        map.put(ArenaEditorType.KIT_CHANGE_COMMANDS, 20);
        map.put(ArenaEditorType.KIT_CHANGE_POTIONS, 21);
        map.put(ArenaEditorType.KIT_CHANGE_ARMOR, 23);
        map.put(ArenaEditorType.KIT_CHANGE_INVENTORY, 24);

        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof ArenaEditorType type2) {
            if (type2 == ArenaEditorType.KIT_CHANGE_DEFAULT) {
                item.setType(this.object.isDefault() ? Material.LIME_DYE : Material.GRAY_DYE);
            }
            else if (type2 == ArenaEditorType.KIT_CHANGE_ICON) {
                item.setType(this.object.getIcon().getType());
            }
        }
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER;
    }

    static class ContentEditor extends AbstractMenu<AMA> {

        private final Kit     kit;
        private final boolean isArmor;

        public ContentEditor(@NotNull Kit kit, int size) {
            super(kit.plugin(), ArenaEditorHub.TITLE_KIT_EDITOR, size);
            this.kit = kit;
            this.isArmor = size == 9;
        }

        @Override
        public boolean onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
            inventory.setContents(this.isArmor ? this.kit.getArmor() : this.kit.getItems());
            return true;
        }

        @Override
        public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
            Inventory inventory = e.getInventory();
            ItemStack[] items = new ItemStack[this.isArmor ? 4 : 27];

            for (int slot = 0; slot < items.length; slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item == null) continue;

                items[slot] = item;
            }

            if (this.isArmor) {
                this.kit.setArmor(items);
                this.kit.setExtras(new ItemStack[]{inventory.getItem(4)});
            }
            else this.kit.setItems(items);

            this.kit.save();
            super.onClose(player, e);

            plugin.runTask(c -> this.kit.getEditor().open(player, 1), false);
        }

        @Override
        public boolean destroyWhenNoViewers() {
            return true;
        }

        @Override
        public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
            return false;
        }
    }
}
