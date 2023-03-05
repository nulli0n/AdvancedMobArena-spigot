package su.nightexpress.ama.arena.game;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.editor.game.EditorGameCommandSettings;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ArenaGameCommand implements IArenaGameEventListener, ArenaChild, IEditable, ICleanable, IPlaceholder {

    private final ArenaConfig arenaConfig;

    private final Set<ArenaGameEventTrigger<?>> triggers;
    private       ArenaTargetType               targetType;
    private       List<String>                  commands;

    private EditorGameCommandSettings editor;

    public ArenaGameCommand(
        @NotNull ArenaConfig arenaConfig, @NotNull Set<ArenaGameEventTrigger<?>> triggers,
        @NotNull ArenaTargetType targetType, @NotNull List<String> commands) {
        this.arenaConfig = arenaConfig;

        this.triggers = triggers;
        this.setTargetType(targetType);
        this.setCommands(commands);
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.GAME_COMMAND_TRIGGERS, Placeholders.format(this.getTriggers()))
            .replace(Placeholders.GAME_COMMAND_TARGET, plugin().getLangManager().getEnum(this.getTargetType()))
            .replace(Placeholders.GAME_COMMAND_COMMANDS, String.join("\n", this.getCommands()))
            ;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        Arena arena = gameEvent.getArena();
        if (this.getTargetType() == ArenaTargetType.GLOBAL) {
            this.getCommands().forEach(cmd -> arena.plugin().getServer().dispatchCommand(plugin().getServer().getConsoleSender(), cmd));
            return true;
        }

        arena.getPlayers(this.getTargetType()).forEach(arenaPlayer -> {
            this.getCommands().forEach(cmd -> PlayerUtil.dispatchCommand(arenaPlayer.getPlayer(), cmd));
        });
        return true;
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
    public EditorGameCommandSettings getEditor() {
        if (this.editor == null) {
            this.editor = new EditorGameCommandSettings(this);
        }
        return editor;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    @Override
    public Set<ArenaGameEventTrigger<?>> getTriggers() {
        return triggers;
    }

    @NotNull
    public ArenaTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(@NotNull ArenaTargetType targetType) {
        this.targetType = targetType;
    }

    @NotNull
    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = commands;
    }
}
