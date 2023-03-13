package su.nightexpress.ama.arena.reward;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.editor.reward.EditorRewardList;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.Parameters;
import su.nightexpress.ama.arena.script.action.ScriptActions;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptCondition;
import su.nightexpress.ama.arena.script.condition.ScriptConditions;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;

import java.util.*;
import java.util.function.UnaryOperator;

public class ArenaRewardManager implements ArenaChild, ConfigHolder, ILoadable, IEditable, IProblematic {

    private final ArenaConfig      arenaConfig;
    private final JYML             config;
    private final Map<String, ArenaReward> rewards;

    private boolean isRetainOnLeave;
    private boolean isRetainOnDeath;

    private EditorRewardList editor;

    private static final String CONFIG_NAME = "rewards.yml";

    public ArenaRewardManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.config = new JYML(this.arenaConfig.getFile().getParentFile().getAbsolutePath(), CONFIG_NAME);
        this.rewards = new HashMap<>();
    }

    @Override
    public void setup() {
        this.isRetainOnLeave = config.getBoolean("Retain_On_Leave");
        this.isRetainOnDeath = config.getBoolean("Retain_On_Death");

        for (String sId : config.getSection("List")) {
            String path2 = "List." + sId + ".";

            String name = config.getString(path2 + "Name", sId);
            boolean isLate = config.getBoolean(path2 + "Late");
            double chance = config.getDouble(path2 + "Chance");
            Set<ArenaGameEventTrigger<?>> triggers = ArenaGameEventTrigger.parse(config, path2 + "Triggers");
            ArenaTargetType targetType = config.getEnum(path2 + "Target", ArenaTargetType.class, ArenaTargetType.PLAYER_ALL);
            List<String> commands = config.getStringList(path2 + "Commands");
            List<ItemStack> items = new ArrayList<>(Arrays.asList(config.getItemsEncoded(path2 + "Items")));

            // ----------- CONVERT SCRIPTS START -----------
            for (String eventRaw : config.getSection(path2 + "Triggers")) {
                ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                if (eventType == null) continue;

                String sName = "reward_" + sId;
                ArenaScript script = new ArenaScript(this.arenaConfig, sName, eventType);

                String values = config.getString(path2 + "Triggers." + eventRaw, "");
                Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                conditions.values().forEach(list -> {
                    list.add(new ScriptPreparedCondition(ScriptConditions.CHANCE, chance, ScriptCondition.Operator.SMALLER));
                });
                script.getConditions().putAll(conditions);

                ScriptPreparedAction action = new ScriptPreparedAction(ScriptActions.GIVE_REWARD, new ParameterResult());
                action.getParameters().add(Parameters.REWARD, sId.toLowerCase());
                action.getParameters().add(Parameters.TARGET, targetType.name());
                script.getActions().add(action);

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            // ----------- CONVERT SCRIPTS END -----------

            ArenaReward reward = new ArenaReward(arenaConfig, name, isLate, triggers, targetType, chance, commands, items);
            this.getRewardsMap().put(sId.toLowerCase(), reward);
        }
    }

    @Override
    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getRewards().forEach(ArenaReward::clear);
        this.getRewardsMap().clear();
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
        this.getRewardsMap().forEach((id, reward) -> {
            String path2 = "List." + id + ".";
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
