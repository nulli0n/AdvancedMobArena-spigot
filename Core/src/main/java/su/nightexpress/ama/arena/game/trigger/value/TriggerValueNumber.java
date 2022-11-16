package su.nightexpress.ama.arena.game.trigger.value;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.api.arena.game.ArenaGameTriggerValue;

public class TriggerValueNumber extends ArenaGameTriggerValue<Double> {

    private static final ValueParser<Double> PARSER = (raw) -> StringUtil.getDouble(raw, 0D, true);

    public TriggerValueNumber(@NotNull String valueRaw) {
        super(valueRaw, PARSER);
    }

    @Override
    public void addOperators() {
        this.addOperator(OPERATOR_NEGATE + OPERATOR_INTERVAL, ((arenaValue, triggerValue) -> {
            return (int) (arenaValue % triggerValue) != 0;
        }));
        this.addOperator(OPERATOR_INTERVAL, ((arenaValue, triggerValue) -> {
            return (int) (arenaValue % triggerValue) == 0;
        }));
        this.addOperator(OPERATOR_NEGATE, ((arenaValue, triggerValue) -> {
            return arenaValue.doubleValue() != triggerValue.doubleValue();
        }));
        this.addOperator(OPERATOR_GREATER, ((arenaValue, triggerValue) -> {
            return arenaValue > triggerValue;
        }));
        this.addOperator(OPERATOR_SMALLER, ((arenaValue, triggerValue) -> {
            return arenaValue < triggerValue;
        }));
    }

    @NotNull
    @Override
    public Operator<Double> getDefaultOperator() {
        return ((arenaValue, triggerValue) -> arenaValue.doubleValue() == triggerValue.doubleValue());
    }
}
