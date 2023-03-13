package su.nightexpress.ama.arena.game.trigger.value;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.game.ArenaGameTriggerValue;

@Deprecated
public class TriggerValueBoolean extends ArenaGameTriggerValue<Boolean> {

    private static final ValueParser<Boolean> PARSER = (Boolean::valueOf);

    public TriggerValueBoolean(@NotNull String valueRaw) {
        super(valueRaw, PARSER);
    }

    @Override
    public void addOperators() {
        this.addOperator(OPERATOR_NEGATE, ((arenaValue, triggerValue) -> arenaValue.booleanValue() != triggerValue.booleanValue()));
    }

    @NotNull
    @Override
    public Operator<Boolean> getDefaultOperator() {
        return ((arenaValue, triggerValue) -> arenaValue.booleanValue() == triggerValue.booleanValue());
    }
}
