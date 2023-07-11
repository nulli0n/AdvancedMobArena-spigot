package su.nightexpress.ama.arena.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.editor.reward.RewardSettingsEditor;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.type.PlayerType;
import su.nightexpress.ama.config.Lang;

import java.util.ArrayList;
import java.util.List;

public class ArenaReward implements ArenaChild, Placeholder {

    private final String      id;
    private final ArenaConfig arenaConfig;
    private final PlaceholderMap placeholderMap;

    private String          name;
    private boolean         isLate;
    private List<String>    commands;
    private List<ItemStack> items;

    private RewardSettingsEditor editor;

    public ArenaReward(@NotNull ArenaConfig arenaConfig, @NotNull String id) {
        this(arenaConfig, id, StringUtil.capitalizeUnderscored(id), true, new ArrayList<>(), new ArrayList<>());
    }

    public ArenaReward(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull String name,
        boolean isLate,
        @NotNull List<String> commands,
        @NotNull List<ItemStack> items
    ) {
        this.id = id.toLowerCase();
        this.arenaConfig = arenaConfig;

        this.setName(name);
        this.setLate(isLate);
        this.setCommands(commands);
        this.setItems(items);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.REWARD_ID, this::getId)
            .add(Placeholders.REWARD_NAME, this::getName)
            .add(Placeholders.REWARD_IS_LATE, () -> LangManager.getBoolean(this.isLate()))
            .add(Placeholders.REWARD_COMMANDS, () -> String.join("\n", this.getCommands()))
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public RewardSettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new RewardSettingsEditor(this);
        }
        return editor;
    }

    public void give(@NotNull Arena arena, ArenaTargetType targetType) {
        arena.getPlayers(targetType, PlayerType.REAL).forEach(arenaPlayer -> {
            if (this.isLate()) {
                arenaPlayer.getRewards().add(this);
            }
            else this.give(arenaPlayer.getPlayer());

            plugin().getMessage(Lang.ARENA_GAME_NOTIFY_REWARD).replace(this.replacePlaceholders()).send(arenaPlayer.getPlayer());
        });
    }

    public void give(@NotNull Player player) {
        this.getItems().forEach(item -> PlayerUtil.addItem(player, item));
        this.getCommands().forEach(command -> PlayerUtil.dispatchCommand(player, command));
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    public boolean isLate() {
        return isLate;
    }

    public void setLate(boolean late) {
        this.isLate = late;
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = new ArrayList<>(commands);
    }

    @NotNull
    public List<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(@NotNull List<ItemStack> items) {
        this.items = new ArrayList<>(items);
        this.getItems().removeIf(item -> item == null || item.getType().isAir());
    }
}