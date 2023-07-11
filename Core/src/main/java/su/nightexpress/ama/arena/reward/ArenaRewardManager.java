package su.nightexpress.ama.arena.reward;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.Loadable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.editor.reward.RewardListEditor;
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

public class ArenaRewardManager implements ArenaChild, ConfigHolder, Loadable, Problematic, Placeholder {

    private static final String CONFIG_NAME = "rewards.yml";

    private final ArenaConfig              arenaConfig;
    private final JYML                     config;
    private final Map<String, ArenaReward> rewards;
    private final PlaceholderMap placeholderMap;

    private boolean isRetainOnLeave;
    private boolean isRetainOnDeath;

    private RewardListEditor editor;

    public ArenaRewardManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.config = new JYML(this.arenaConfig.getFile().getParentFile().getAbsolutePath(), CONFIG_NAME);
        this.rewards = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.REWARD_MANAGER_RETAIN_ON_DEATH, () -> LangManager.getBoolean(this.isRetainOnDeath()))
            .add(Placeholders.REWARD_MANAGER_RETAIN_ON_LEAVE, () -> LangManager.getBoolean(this.isRetainOnLeave()))
        ;
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
                if (conditions.isEmpty()) {
                    conditions.put(Placeholders.DEFAULT, Collections.singletonList(new ScriptPreparedCondition(ScriptConditions.CHANCE, chance, ScriptCondition.Operator.SMALLER)));
                }
                script.getConditions().putAll(conditions);

                ScriptPreparedAction action = new ScriptPreparedAction(ScriptActions.GIVE_REWARD, new ParameterResult());
                action.getParameters().add(Parameters.REWARD, sId.toLowerCase());
                action.getParameters().add(Parameters.TARGET, targetType.name());
                script.getActions().add(action);

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            config.remove(path2 + "Triggers");
            // ----------- CONVERT SCRIPTS END -----------

            ArenaReward reward = new ArenaReward(arenaConfig, sId, name, isLate, commands, items);
            this.getRewardsMap().put(reward.getId(), reward);
        }
        config.saveChanges();
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
            config.set(path2 + "Name", reward.getName());
            config.set(path2 + "Late", reward.isLate());
            config.set(path2 + "Commands", reward.getCommands());
            config.setItemsEncoded(path2 + "Items", reward.getItems());
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
