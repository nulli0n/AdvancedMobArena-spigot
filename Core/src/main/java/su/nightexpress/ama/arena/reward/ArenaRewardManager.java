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
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.editor.reward.RewardListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.*;

public class ArenaRewardManager extends AbstractConfigHolder<AMA> implements ArenaChild, Problematic, Placeholder {

    public static final String CONFIG_NAME = "rewards.yml";

    private final ArenaConfig              arenaConfig;
    private final Map<String, ArenaReward> rewards;
    private final PlaceholderMap placeholderMap;

    private boolean isRetainOnLeave;
    private boolean isRetainOnDeath;

    private RewardListEditor editor;

    public ArenaRewardManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.rewards = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.REWARD_MANAGER_RETAIN_ON_DEATH, () -> LangManager.getBoolean(this.isRetainOnDeath()))
            .add(Placeholders.REWARD_MANAGER_RETAIN_ON_LEAVE, () -> LangManager.getBoolean(this.isRetainOnLeave()))
        ;
    }

    @Override
    public boolean load() {
        this.isRetainOnLeave = cfg.getBoolean("Retain_On_Leave");
        this.isRetainOnDeath = cfg.getBoolean("Retain_On_Death");

        for (String sId : cfg.getSection("List")) {
            String path2 = "List." + sId + ".";

            String name = cfg.getString(path2 + "Name", sId);
            boolean isLate = cfg.getBoolean(path2 + "Late");
            double chance = cfg.getDouble(path2 + "Chance");

            ArenaTargetType targetType = cfg.getEnum(path2 + "Target", ArenaTargetType.class, ArenaTargetType.PLAYER_ALL);
            List<String> commands = cfg.getStringList(path2 + "Commands");
            List<ItemStack> items = new ArrayList<>(Arrays.asList(cfg.getItemsEncoded(path2 + "Items")));

            ArenaReward reward = new ArenaReward(arenaConfig, sId, name, isLate, commands, items);
            this.getRewardsMap().put(reward.getId(), reward);
        }
        return true;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getRewards().forEach(ArenaReward::clear);
        this.getRewardsMap().clear();
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
    public List<String> getProblems() {
        return new ArrayList<>();
    }

    @Override
    public void onSave() {
        cfg.set("Retain_On_Death", this.isRetainOnDeath());
        cfg.set("Retain_On_Leave", this.isRetainOnLeave());
        cfg.remove("List");
        this.getRewardsMap().forEach((id, reward) -> {
            String path2 = "List." + id + ".";
            cfg.set(path2 + "Name", reward.getName());
            cfg.set(path2 + "Late", reward.isLate());
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

        ArenaReward reward = new ArenaReward(this.getArenaConfig(), id);
        this.getRewardsMap().put(reward.getId(), reward);
        return true;
    }

    @NotNull
    public Map<String, ArenaReward> getRewardsMap() {
        return this.rewards;
    }

    @NotNull
    public Collection<ArenaReward> getRewards() {
        return rewards.values();
    }

    @NotNull
    public Optional<ArenaReward> getReward(@NotNull String id) {
        return Optional.ofNullable(this.getRewardsMap().get(id.toLowerCase()));
    }

    public boolean isRetainOnDeath() {
        return isRetainOnDeath;
    }

    public boolean isRetainOnLeave() {
        return isRetainOnLeave;
    }

    public void setRetainOnDeath(boolean isRetainOnDeath) {
        this.isRetainOnDeath = isRetainOnDeath;
    }

    public void setRetainOnLeave(boolean retainOnLeave) {
        this.isRetainOnLeave = retainOnLeave;
    }
}
