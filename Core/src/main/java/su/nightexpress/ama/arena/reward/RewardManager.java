package su.nightexpress.ama.arena.reward;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.editor.reward.RewardListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.*;

public class RewardManager extends AbstractConfigHolder<AMA> implements ArenaChild, Inspectable, Placeholder {

    private final ArenaConfig         arenaConfig;
    private final Map<String, Reward> rewardMap;
    private final PlaceholderMap      placeholderMap;

    private boolean keepOnLeave;
    private boolean keepOnDeath;

    private RewardListEditor editor;

    public RewardManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.rewardMap = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.REWARD_MANAGER_KEEP_ON_DEATH, () -> LangManager.getBoolean(this.isKeepOnDeath()))
            .add(Placeholders.REWARD_MANAGER_KEEP_ON_LEAVE, () -> LangManager.getBoolean(this.isKeepOnLeave()))
        ;
    }

    @Override
    public boolean load() {
        this.keepOnLeave = cfg.getBoolean("Retain_On_Leave");
        this.keepOnDeath = cfg.getBoolean("Retain_On_Death");

        for (String rewardId : cfg.getSection("List")) {
            String path2 = "List." + rewardId + ".";

            String name = cfg.getString(path2 + "Name", rewardId);
            boolean completeRequired = cfg.getBoolean(path2 + "Late");
            double chance = cfg.getDouble(path2 + "Chance");

            ArenaTargetType targetType = cfg.getEnum(path2 + "Target", ArenaTargetType.class, ArenaTargetType.PLAYER_ALL);
            List<String> commands = cfg.getStringList(path2 + "Commands");
            List<ItemStack> items = new ArrayList<>(Arrays.asList(cfg.getItemsEncoded(path2 + "Items")));

            Reward reward = new Reward(arenaConfig, rewardId, name, completeRequired, commands, items);
            this.getRewardMap().put(reward.getId(), reward);
        }
        return true;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getRewards().forEach(Reward::clear);
        this.getRewardMap().clear();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public RewardListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new RewardListEditor(this);
        }
        return editor;
    }

    @Override
    @NotNull
    public Report getReport() {
        Report report = new Report();

        this.getRewards().forEach(reward -> {
            Report rewardReport = reward.getReport();
            if (rewardReport.hasProblems()) {
                report.addProblem("Major issues with '" + reward.getId() + "' reward!");
            }
            else if (rewardReport.hasWarns()) {
                report.addWarn("Minor issues with '" + reward.getId() + "' reward!");
            }
        });

        if (!report.hasProblems() && !report.hasWarns()) {
            report.addGood("All " + this.getRewards().size() + " rewards are fine!");
        }

        return report;
    }

    @Override
    public void onSave() {
        cfg.set("Retain_On_Death", this.isKeepOnDeath());
        cfg.set("Retain_On_Leave", this.isKeepOnLeave());
        cfg.remove("List");
        this.getRewardMap().forEach((id, reward) -> {
            String path2 = "List." + id + ".";
            cfg.set(path2 + "Name", reward.getName());
            cfg.set(path2 + "Late", reward.isCompletionRequired());
            cfg.set(path2 + "Commands", reward.getCommands());
            cfg.setItemsEncoded(path2 + "Items", reward.getItems());
        });
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    public boolean createReward(@NotNull String id) {
        if (this.getReward(id).isPresent()) return false;

        Reward reward = new Reward(this.getArenaConfig(), id);
        this.getRewardMap().put(reward.getId(), reward);
        return true;
    }

    public void removeReward(@NotNull Reward reward) {
        reward.clear();
        this.getRewardMap().remove(reward.getId());
        this.save();
    }

    @NotNull
    public Map<String, Reward> getRewardMap() {
        return this.rewardMap;
    }

    @NotNull
    public Collection<Reward> getRewards() {
        return rewardMap.values();
    }

    @NotNull
    public Optional<Reward> getReward(@NotNull String id) {
        return Optional.ofNullable(this.getRewardMap().get(id.toLowerCase()));
    }

    public boolean isKeepOnDeath() {
        return keepOnDeath;
    }

    public boolean isKeepOnLeave() {
        return keepOnLeave;
    }

    public void setKeepOnDeath(boolean isRetainOnDeath) {
        this.keepOnDeath = isRetainOnDeath;
    }

    public void setKeepOnLeave(boolean keepOnLeave) {
        this.keepOnLeave = keepOnLeave;
    }
}
