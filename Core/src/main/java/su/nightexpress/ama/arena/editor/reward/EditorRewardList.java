package su.nightexpress.ama.arena.editor.reward;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.reward.ArenaReward;
import su.nightexpress.ama.arena.reward.ArenaRewardManager;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorRewardList extends AbstractEditorMenuAuto<AMA, ArenaRewardManager, ArenaReward> {

    public EditorRewardList(@NotNull ArenaRewardManager rewardManager) {
        super(rewardManager.plugin(), rewardManager, ArenaEditorUtils.TITLE_REWARD_EDITOR, 45);

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    rewardManager.getArenaConfig().getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case REWARD_CREATE -> {
                        ArenaReward reward = new ArenaReward(rewardManager.getArenaConfig(), "New Reward",
                            true, new HashSet<>(), ArenaTargetType.PLAYER_ALL, 0D,
                            new ArrayList<>(), new ArrayList<>());
                        rewardManager.getRewards().add(reward);
                    }
                    case REWARDS_CHANGE_RETAIN -> {
                        if (e.isLeftClick()) rewardManager.setRetainOnDeath(!rewardManager.isRetainOnDeath());
                        else if (e.isRightClick()) rewardManager.setRetainOnLeave(!rewardManager.isRetainOnLeave());
                    }
                }
                rewardManager.save();
                this.open(player, this.getPage(player));
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.REWARDS_CHANGE_RETAIN, 40);
        map.put(ArenaEditorType.REWARD_CREATE, 41);
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
    protected List<ArenaReward> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getRewards());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaReward reward) {
        ItemStack item = ArenaEditorType.REWARD_OBJECT.getItem();
        ItemUtil.replace(item, reward.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull ArenaReward reward) {
        return (player1, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                this.parent.getRewards().remove(reward);
                reward.clear();
                this.parent.save();
                this.open(player, this.getPage(player));
                return;
            }
            reward.getEditor().open(player, 1);
        };
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.parent.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
