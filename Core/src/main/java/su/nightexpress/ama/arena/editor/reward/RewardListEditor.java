package su.nightexpress.ama.arena.editor.reward;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.reward.Reward;
import su.nightexpress.ama.arena.reward.RewardManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class RewardListEditor extends EditorMenu<AMA, RewardManager> implements AutoPaged<Reward> {

    public RewardListEditor(@NotNull RewardManager rewardManager) {
        super(rewardManager.plugin(), rewardManager, "Rewards Editor [" + rewardManager.getRewards().size() + " rewards]", 45);

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
            if (event.isLeftClick()) rewardManager.setKeepOnDeath(!rewardManager.isKeepOnDeath());
            else if (event.isRightClick()) rewardManager.setKeepOnLeave(!rewardManager.isKeepOnLeave());
            rewardManager.save();
            this.openNextTick(viewer, viewer.getPage());
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, rewardManager.replacePlaceholders());
        }));
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
    public List<Reward> getObjects(@NotNull Player player) {
        return this.object.getRewards().stream().sorted(Comparator.comparing(Reward::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Reward reward) {
        ItemStack item;
        if (reward.getItems().isEmpty()) {
            if (reward.getCommands().isEmpty()) {
                item = new ItemStack(Material.GOLD_INGOT);
            }
            else item = new ItemStack(Material.COMMAND_BLOCK);
        }
        else item = new ItemStack(reward.getItems().get(0));

        ItemReplacer.create(item).readLocale(EditorLocales.REWARD_OBJECT).trimmed().hideFlags()
            .replace(reward.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Reward reward) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.removeReward(reward);
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            reward.getEditor().openNextTick(viewer, 1);
        };
    }
}
