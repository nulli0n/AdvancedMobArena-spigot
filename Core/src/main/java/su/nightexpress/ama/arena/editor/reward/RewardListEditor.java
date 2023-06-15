package su.nightexpress.ama.arena.editor.reward;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.reward.ArenaReward;
import su.nightexpress.ama.arena.reward.ArenaRewardManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RewardListEditor extends EditorMenu<AMA, ArenaRewardManager> implements AutoPaged<ArenaReward> {

    public RewardListEditor(@NotNull ArenaRewardManager rewardManager) {
        super(rewardManager.plugin(), rewardManager, EditorHub.TITLE_REWARD_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            rewardManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.REWARD_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_ID, wrapper -> {
                if (!rewardManager.createReward(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_REWARD_ERROR_EXIST).getLocalized());
                    return false;
                }
                rewardManager.save();
                return true;
            });
        });

        this.addItem(Material.ENDER_CHEST, EditorLocales.REWARDS_RETAIN, 40).setClick((viewer, event) -> {
            if (event.isLeftClick()) rewardManager.setRetainOnDeath(!rewardManager.isRetainOnDeath());
            else if (event.isRightClick()) rewardManager.setRetainOnLeave(!rewardManager.isRetainOnLeave());
            rewardManager.save();
            this.openNextTick(viewer, viewer.getPage());
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, rewardManager.replacePlaceholders()));
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<ArenaReward> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getRewards());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaReward reward) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.REWARD_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.REWARD_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, reward.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaReward reward) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getRewardsMap().remove(reward.getId());
                reward.clear();
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            reward.getEditor().openNextTick(viewer, 1);
        };
    }
}
