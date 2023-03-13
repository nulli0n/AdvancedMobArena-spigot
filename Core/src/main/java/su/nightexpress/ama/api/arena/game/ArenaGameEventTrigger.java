package su.nightexpress.ama.api.arena.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.game.trigger.GameTriggerBoolean;
import su.nightexpress.ama.arena.game.trigger.GameTriggerNumber;
import su.nightexpress.ama.arena.game.trigger.GameTriggerString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Deprecated
public abstract class ArenaGameEventTrigger<T> {

    protected ArenaGameEventType             eventType;
    protected List<ArenaGameTriggerValue<T>> values;

    private static final String OR = " OR ";

    public ArenaGameEventTrigger(@NotNull ArenaGameEventType eventType, @NotNull String inputs) {
        this.eventType = eventType;
        this.values = new ArrayList<>();
        for (String input : inputs.split(OR)) {
            this.values.add(this.loadValue(input));
        }
    }

    public boolean isReady(@NotNull ArenaGameGenericEvent event) {
        if (event.getEventType() != this.getType()) return false;

        T value = this.getEventRawValue(event);
        return this.test(value);
    }

    public boolean test(@NotNull T arenaValue) {
        return this.getValues().stream().anyMatch(triggerValue -> triggerValue.test(arenaValue));
    }

    @NotNull
    public String getValuesRaw() {
        return String.join(OR, this.getValues().stream().map(ArenaGameTriggerValue::getRaw).toList());
    }

    @NotNull
    public abstract T getEventRawValue(@NotNull ArenaGameGenericEvent event);

    @NotNull
    public final ArenaGameEventType getType() {
        return this.eventType;
    }

    @NotNull
    public final List<ArenaGameTriggerValue<T>> getValues() {
        return this.values;
    }

    @NotNull
    protected abstract ArenaGameTriggerValue<T> loadValue(@NotNull String input);

    @NotNull
    public static Set<ArenaGameEventTrigger<?>> parse(@NotNull JYML cfg, @NotNull String path) {
        Set<ArenaGameEventTrigger<?>> triggers = new HashSet<>();
        for (String typeRaw : cfg.getSection(path)) {
            String input = cfg.getString(path + "." + typeRaw, "");
            ArenaGameEventTrigger<?> trigger = parse(typeRaw, input);
            if (trigger == null) continue;

            triggers.add(trigger);
        }
        return triggers;
    }

    @Nullable
    public static ArenaGameEventTrigger<?> parse(@NotNull String typeRaw, @NotNull String input) {
        ArenaGameEventType eventType = CollectionsUtil.getEnum(typeRaw, ArenaGameEventType.class);
        if (eventType == null || input.isEmpty()) return null;

        return switch (eventType) {
            case GAME_START, GAME_END_LOSE, GAME_END_TIME, GAME_END_WIN,
                SHOP_LOCKED, SHOP_UNLOCKED -> new GameTriggerBoolean(eventType, input);
            case WAVE_END, WAVE_START, SCORE_DECREASED, SCORE_INCREASED -> new GameTriggerNumber(eventType, input);
            case MOB_KILLED, SHOP_ITEM_LOCKED, SHOP_ITEM_UNLOCKED, SPOT_CHANGED,
                SHOP_CATEGORY_LOCKED, SHOP_CATEGORY_UNLOCKED,
                REGION_LOCKED, REGION_UNLOCKED,
                PLAYER_DEATH, PLAYER_JOIN, PLAYER_LEAVE -> new GameTriggerString(eventType, input);
        };
    }
}
