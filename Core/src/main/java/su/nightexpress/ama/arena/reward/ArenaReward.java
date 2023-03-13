package su.nightexpress.ama.arena.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.editor.reward.EditorRewardSettings;
import su.nightexpress.ama.config.Lang;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ArenaReward implements IArenaGameEventListener, ArenaChild, IEditable, ICleanable {

    private final ArenaConfig arenaConfig;

    private       String                        name;
    private       boolean                       isLate;
    private final Set<ArenaGameEventTrigger<?>> triggers;
    @Deprecated private       ArenaTargetType               targetType;
    @Deprecated private       double                        chance;
    private       List<String>                  commands;
    private       List<ItemStack>               items;

    private EditorRewardSettings editor;

    public ArenaReward(
        @NotNull ArenaConfig arenaConfig,

        @NotNull String name,
        boolean isLate,
        @NotNull Set<ArenaGameEventTrigger<?>> triggers,
        @NotNull ArenaTargetType targetType,
        double chance,
        @NotNull List<String> commands,
        @NotNull List<ItemStack> items
    ) {
        this.arenaConfig = arenaConfig;

        this.setName(name);
        this.setLate(isLate);
        this.triggers = triggers;
        this.setTargetType(targetType);
        this.setChance(chance);
        this.setCommands(commands);
        this.setItems(items);
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.REWARD_TRIGGERS, Placeholders.format(this.getTriggers()))
            .replace(Placeholders.REWARD_NAME, this.getName())
            .replace(Placeholders.REWARD_TARGET_TYPE, plugin().getLangManager().getEnum(this.getTargetType()))
            .replace(Placeholders.REWARD_CHANCE, NumberUtil.format(this.getChance()))
            .replace(Placeholders.REWARD_IS_LATE, LangManager.getBoolean(this.isLate()))
            .replace(Placeholders.REWARD_COMMANDS, String.join("\n", this.getCommands()))
            ;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    @Override
    public EditorRewardSettings getEditor() {
        if (this.editor == null) {
            this.editor = new EditorRewardSettings(this);
        }
        return editor;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        this.give(gameEvent.getArena());
        return true;
    }

    @Deprecated
    public void give(@NotNull Arena arena) {
        if (Rnd.get(true) >= this.getChance()) return;

        arena.getPlayers(this.getTargetType()).forEach(arenaPlayer -> {
            if (this.isLate()) {
                arenaPlayer.getRewards().add(this);
            }
            else this.give(arenaPlayer.getPlayer());
        });
    }

    public void give(@NotNull Arena arena, ArenaTargetType targetType) {
        arena.getPlayers(targetType).forEach(arenaPlayer -> {
            if (this.isLate()) {
                arenaPlayer.getRewards().add(this);
            }
            else this.give(arenaPlayer.getPlayer());
        });
    }

    public void give(@NotNull Player player) {
        this.getItems().forEach(item -> PlayerUtil.addItem(player, item));
        this.getCommands().forEach(command -> PlayerUtil.dispatchCommand(player, command));

        // TODO Not here
        plugin().getMessage(Lang.Arena_Game_Notify_Reward).replace(this.replacePlaceholders()).send(player);
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUtil.color(name);
    }

    public boolean isLate() {
        return isLate;
    }

    public void setLate(boolean late) {
        this.isLate = late;
    }

    @NotNull
    @Override
    public Set<ArenaGameEventTrigger<?>> getTriggers() {
        return triggers;
    }

    @NotNull
    @Deprecated
    public ArenaTargetType getTargetType() {
        return targetType;
    }

    @Deprecated
    public void setTargetType(@NotNull ArenaTargetType targetType) {
        this.targetType = targetType;
    }

    @Deprecated
    public double getChance() {
        return this.chance;
    }

    @Deprecated
    public void setChance(double chance) {
        this.chance = chance;
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = commands;
    }

    @NotNull
    public List<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(@NotNull List<ItemStack> items) {
        this.items = items;
    }
}