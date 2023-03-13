package su.nightexpress.ama.arena.game.trigger.value;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.game.ArenaGameTriggerValue;

@Deprecated
public class TriggerValueString extends ArenaGameTriggerValue<String> {

    private static final ValueParser<String> PARSER = (raw) -> raw;

    public TriggerValueString(@NotNull String valueRaw) {
        super(valueRaw, PARSER);
    }

    @Override
    public void addOperators() {
        this.addOperator(OPERATOR_NEGATE, ((arenaValue, triggerValue) -> !arenaValue.equalsIgnoreCase(triggerValue)));
    }

    @NotNull
    @Override
    public Operator<String> getDefaultOperator() {
        return (String::equalsIgnoreCase);
    }
}
