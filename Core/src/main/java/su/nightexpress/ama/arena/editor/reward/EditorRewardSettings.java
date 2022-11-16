package su.nightexpress.ama.arena.editor.reward;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.reward.ArenaReward;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class EditorRewardSettings extends AbstractEditorMenu<AMA, ArenaReward> {

    public EditorRewardSettings(@NotNull ArenaReward reward) {
        super(reward.plugin(), reward, ArenaEditorUtils.TITLE_REWARD_EDITOR, 45);

        EditorInput<ArenaReward, ArenaEditorType> input = (player, reward2, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            switch (type) {
                case REWARD_CHANGE_NAME -> reward2.setName(msg);
                case REWARD_CHANGE_CHANCE -> {
                    double value = StringUtil.getDouble(msg, -1D);
                    if (value < 0) {
                        EditorManager.error(player, EditorManager.ERROR_NUM_INVALID);
                        return false;
                    }
                    reward2.setChance(value);
                }
                case REWARD_CHANGE_COMMANDS -> reward2.getCommands().add(msg);
            }
            reward2.getArenaConfig().getRewardManager().save();
            return true;
        };

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    reward.getArenaConfig().getRewardManager().getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case REWARD_CHANGE_LATE -> reward.setLate(!reward.isLate());
                    case REWARD_CHANGE_NAME -> {
                        EditorManager.startEdit(player, reward, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Reward_Enter_Name).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case REWARD_CHANGE_ITEMS -> {
                        new RewardItems(reward).open(player, 1);
                        return;
                    }
                    case REWARD_CHANGE_CHANCE -> {
                        EditorManager.startEdit(player, reward, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Reward_Enter_Chance).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case REWARD_CHANGE_COMMANDS -> {
                        if (e.isRightClick()) {
                            reward.getCommands().clear();
                            break;
                        }
                        EditorManager.startEdit(player, reward, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Reward_Enter_Command).getLocalized());
                        EditorManager.sendCommandTips(player);
                        player.closeInventory();
                        return;
                    }
                    case REWARD_CHANGE_TRIGGERS -> {
                        ArenaEditorUtils.handleTriggersClick(player, reward, type2, e.isRightClick());
                        if (e.isRightClick()) break;
                        return;
                    }
                    case REWARD_CHANGE_TARGET_TYPE -> reward.setTargetType(CollectionsUtil.switchEnum(reward.getTargetType()));
                }
                reward.getArenaConfig().getRewardManager().save();
                this.open(player, 1);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.REWARD_CHANGE_NAME, 10);
        map.put(ArenaEditorType.REWARD_CHANGE_CHANCE, 11);
        map.put(ArenaEditorType.REWARD_CHANGE_LATE, 12);
        map.put(ArenaEditorType.REWARD_CHANGE_TARGET_TYPE, 13);
        map.put(ArenaEditorType.REWARD_CHANGE_TRIGGERS, 14);
        map.put(ArenaEditorType.REWARD_CHANGE_COMMANDS, 15);
        map.put(ArenaEditorType.REWARD_CHANGE_ITEMS, 16);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }

    static class RewardItems extends AbstractMenu<AMA> {

        private final ArenaReward reward;

        public RewardItems(@NotNull ArenaReward reward) {
            super(reward.plugin(), "Reward Items", 27);
            this.reward = reward;
        }

        @Override
        public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
            inventory.setContents(this.reward.getItems().toArray(new ItemStack[this.getSize()]));
        }

        @Override
        public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
            Inventory inventory = e.getInventory();
            this.reward.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
            this.reward.getArenaConfig().getRewardManager().save();
            super.onClose(player, e);

            this.plugin.runTask(c -> this.reward.getEditor().open(player, 1), false);
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
