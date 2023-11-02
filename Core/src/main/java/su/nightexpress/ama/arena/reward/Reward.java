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
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.type.PlayerType;
import su.nightexpress.ama.arena.editor.reward.RewardSettingsEditor;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.config.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Reward implements ArenaChild, Inspectable, Placeholder {

    private final AMA            plugin;
    private final ArenaConfig    arenaConfig;
    private final String         id;
    private final PlaceholderMap placeholderMap;

    private String          name;
    private boolean         completionRequired;
    private List<String>    commands;
    private List<ItemStack> items;

    private RewardSettingsEditor editor;

    public Reward(@NotNull ArenaConfig arenaConfig, @NotNull String id) {
        this(arenaConfig, id, StringUtil.capitalizeUnderscored(id),
            true,
            new ArrayList<>(),
            new ArrayList<>()
        );
    }

    public Reward(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull String name,
        boolean completionRequired,
        @NotNull List<String> commands,
        @NotNull List<ItemStack> items
    ) {
        this.plugin = arenaConfig.plugin();
        this.id = id.toLowerCase();
        this.arenaConfig = arenaConfig;

        this.setName(name);
        this.setCompletionRequired(completionRequired);
        this.setCommands(commands);
        this.setItems(items);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.REWARD_REPORT, () -> String.join("\n", this.getReport().getFullReport()))
            .add(Placeholders.REWARD_ID, this::getId)
            .add(Placeholders.REWARD_NAME, this::getName)
            .add(Placeholders.REWARD_COMPLETTION_REQUIRED, () -> LangManager.getBoolean(this.isCompletionRequired()))
            .add(Placeholders.REWARD_COMMANDS, () -> {
                return this.getCommands().stream().map(Report::good).collect(Collectors.joining("\n"));
            })
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        if (this.getCommands().isEmpty() && this.getItems().isEmpty()) {
            report.addWarn("Reward has no items & commands!");
        }

        return report;
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
            this.editor = new RewardSettingsEditor(this.plugin, this);
        }
        return editor;
    }

    public void give(@NotNull Arena arena, ArenaTargetType targetType) {
        arena.getPlayers().select(targetType, PlayerType.REAL).forEach(arenaPlayer -> {
            if (this.isCompletionRequired()) {
                arenaPlayer.getRewards().add(this);
            }
            else this.give(arenaPlayer.getPlayer());

            this.plugin.getMessage(Lang.ARENA_GAME_NOTIFY_REWARD).replace(this.replacePlaceholders()).send(arenaPlayer.getPlayer());
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

    public boolean isCompletionRequired() {
        return completionRequired;
    }

    public void setCompletionRequired(boolean completionRequired) {
        this.completionRequired = completionRequired;
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