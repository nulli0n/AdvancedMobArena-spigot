package su.nightexpress.ama.arena.reward;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.lang.LangManager;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.reward.EditorRewardList;

import java.util.*;
import java.util.function.UnaryOperator;

public class ArenaRewardManager implements IArenaObject, ConfigHolder, ILoadable, IEditable, IProblematic {

    private final ArenaConfig      arenaConfig;
    private final JYML             config;
    private       EditorRewardList editor;

    private boolean          isRetainOnLeave;
    private boolean          isRetainOnDeath;
    private Set<ArenaReward> rewards;

    private static final String CONFIG_NAME = "rewards.yml";

    public ArenaRewardManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.config = new JYML(this.arenaConfig.getFile().getParentFile().getAbsolutePath(), CONFIG_NAME);
    }

    @Override
    @Deprecated
    public void setup() {
        this.isRetainOnLeave = config.getBoolean("Retain_On_Leave");
        this.isRetainOnDeath = config.getBoolean("Retain_On_Death");
        this.rewards = new HashSet<>();
        for (String sId : config.getSection("List")) {
            String path2 = "List." + sId + ".";

            String name = config.getString(path2 + "Name", sId);
            boolean isLate = config.getBoolean(path2 + "Late");
            double chance = config.getDouble(path2 + "Chance");
            Set<ArenaGameEventTrigger<?>> triggers = ArenaGameEventTrigger.parse(config, path2 + "Triggers");
            ArenaTargetType targetType = config.getEnum(path2 + "Target", ArenaTargetType.class, ArenaTargetType.PLAYER_ALL);
            List<String> commands = config.getStringList(path2 + "Commands");
            List<ItemStack> items = new ArrayList<>(Arrays.asList(config.getItemsEncoded(path2 + "Items")));

            ArenaReward reward = new ArenaReward(arenaConfig, name, isLate, triggers, targetType, chance, commands, items);
            this.getRewards().add(reward);
        }
    }

    @Override
    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.rewards != null) {
            this.rewards.clear();
            this.rewards = null;
        }
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()))
            .replace(Placeholders.REWARD_MANAGER_RETAIN_ON_DEATH, LangManager.getBoolean(this.isRetainOnDeath()))
            .replace(Placeholders.REWARD_MANAGER_RETAIN_ON_LEAVE, LangManager.getBoolean(this.isRetainOnLeave()))
            ;
    }

    @NotNull
    @Override
    public EditorRewardList getEditor() {
        if (this.editor == null) {
            this.editor = new EditorRewardList(this);
        }
        return editor;
    }

    @NotNull
    @Override
    public JYML getConfig() {
        return config;
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        return new ArrayList<>();
    }

    @Override
    public void onSave() {
        config.set("Retain_On_Death", this.isRetainOnDeath());
        config.set("Retain_On_Leave", this.isRetainOnLeave());
        config.set("List", null);
        this.getRewards().forEach(reward -> {
            String path2 = "List." + UUID.randomUUID() + ".";
            reward.getTriggers().forEach(trigger -> {
                config.set(path2 + "Triggers." + trigger.getType().name(), trigger.getValuesRaw());
            });
            config.set(path2 + "Name", reward.getName());
            config.set(path2 + "Late", reward.isLate());
            config.set(path2 + "Chance", reward.getChance());
            config.set(path2 + "Target", reward.getTargetType().name());
            config.set(path2 + "Commands", reward.getCommands());
            config.setItemsEncoded(path2 + "Items", reward.getItems());
        });
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
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

    @NotNull
    public Set<ArenaReward> getRewards() {
        return rewards;
    }

    public void setRewards(@NotNull Set<ArenaReward> rewards) {
        this.rewards = rewards;
    }
}
