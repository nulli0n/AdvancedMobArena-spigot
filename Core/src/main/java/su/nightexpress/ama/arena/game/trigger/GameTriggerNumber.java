package su.nightexpress.ama.arena.game.trigger;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.ArenaGameTriggerValue;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaScoreChangeEvent;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.game.trigger.value.TriggerValueNumber;

public class GameTriggerNumber extends ArenaGameEventTrigger<Double> {

    public GameTriggerNumber(@NotNull ArenaGameEventType eventType, @NotNull String input) {
        super(eventType, input);
    }

    @NotNull
    @Override
    protected ArenaGameTriggerValue<Double> loadValue(@NotNull String input) {
        return new TriggerValueNumber(input);
    }

    /*@Override
    @NotNull
    public String formatValue(@NotNull ArenaGameTriggerValue<Double> triggerValue) {
        AMA plugin = this.arenaConfig.plugin();
        String value = NumberUtil.format(triggerValue.getValue());
        LangMessage msg;



        if (triggerValue.isInterval()) {
            if (triggerValue.isNegated()) {
                msg = plugin.getMessage(Lang.Arena_Game_Trigger_Format_Value_Number_EachNot);
            }
            else msg = plugin.getMessage(Lang.Arena_Game_Trigger_Format_Value_Number_Each);
        }
        else {
            if (triggerValue.isNegated()) {
                msg = plugin.getMessage(Lang.Arena_Game_Trigger_Format_Value_Number_Not);
            }
            else msg = plugin.getMessage(Lang.Arena_Game_Trigger_Format_Value_Number_Raw);
        }

        return msg.getLocalized().replace(Placeholders.GENERIC_VALUE, value);
    }*/

    @Override
    @NotNull
    public Double getEventRawValue(@NotNull ArenaGameGenericEvent event) {
        Arena arena = event.getArena();
        return switch (this.getType()) {
            case WAVE_START, WAVE_END -> (double) arena.getWaveNumber();
            case SCORE_INCREASED, SCORE_DECREASED -> {
                ArenaScoreChangeEvent changeEvent = (ArenaScoreChangeEvent) event;
                yield (double) changeEvent.getAmount();
            }
            default -> 0D;
        };
    }

    @Override
    public boolean isReady(@NotNull ArenaGameGenericEvent event) {
        // Fix for the 'Score' triggers because of the calculation/validation issues.
        if (this.getType() != ArenaGameEventType.SCORE_DECREASED && this.getType() != ArenaGameEventType.SCORE_INCREASED) {
            return super.isReady(event);
        }
        if (event.getEventType() != this.getType()) return false;

        int scoreAdd = this.getEventRawValue(event).intValue();
        int scoreOld = event.getArena().getGameScore() - scoreAdd;
        int diff = Math.abs(event.getArena().getGameScore() - scoreOld);

        for (int point = 0; point < diff; point++) {
            int point2 = scoreAdd < 0 ? -(point + 1) : point + 1;
            if (point2 < 0) break;
            if (this.test((double) (scoreOld + point2))) {
                return true;
            }
        }
        return false;
    }
}
