package su.nightexpress.ama.arena.game.trigger;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.ArenaGameTriggerValue;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.game.trigger.value.TriggerValueBoolean;

public class GameTriggerBoolean extends ArenaGameEventTrigger<Boolean> {

    public GameTriggerBoolean(@NotNull ArenaGameEventType eventType, @NotNull String inputs) {
        super(eventType, inputs);
    }

    @NotNull
    @Override
    protected ArenaGameTriggerValue<Boolean> loadValue(@NotNull String input) {
        return new TriggerValueBoolean(input);
    }

    @Override
    @NotNull
    public Boolean getEventRawValue(@NotNull ArenaGameGenericEvent event) {
        AbstractArena arena = event.getArena();
        return switch (this.getType()) {
            case GAME_START, GAME_END_LOSE, GAME_END_TIME, GAME_END_WIN -> true;
            default -> false;
        };
    }

    /*@Override
    @NotNull
    public String formatValue(@NotNull ArenaGameTriggerValue<Boolean> triggerValue) {
        return LangManager.getBoolean(triggerValue.getValue());
    }*/
}
